/*
 * Copyright 2016 Simón Oroño & La Universidad del Zulia
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sron.cg.compiler.phase

import org.objectweb.asm.ClassWriter
import org.objectweb.asm.ClassWriter.COMPUTE_FRAMES
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.*
import sron.cg.compiler.State
import sron.cg.compiler.ast.*
import sron.cg.compiler.phase.generation.*
import sron.cg.compiler.symbol.Function
import sron.cg.compiler.symbol.SymType
import sron.cg.compiler.symbol.Variable
import sron.cg.compiler.type.Type
import java.util.*

object Generation : Phase() {
    private val cw = ClassWriter(COMPUTE_FRAMES)
    private val varQueue = LinkedList<Triple<Variable, Label, Label>>()
    private var continueTargetLabel: Label? = null
    private var breakTargetLabel: Label? = null
    lateinit private var state: State

    private fun handleStmts(mv: MethodVisitor, scope: String, fd: FuncDef, stmt: List<Stmt>, range: Pair<Label, Label>) {
        val (start, end) = range
        stmt.forEach {
            when (it) {
                is VarDec -> {
                    val variable = state.symbolTable[it.name, scope, SymType.VAR] as Variable
                    varQueue += Triple(variable, start, end)

                    it.generate(mv, fd)
                }
                is FunctionCall -> {
                    it.generate(mv, fd)
                    if (it.type != Type.void) {
                        mv.visitInsn(POP)
                    }
                }
                else -> it.generate(mv, fd)
            }
        }
    }

    override fun execute(s: State, init: Init) {
        state = s
        init.generate()
    }

    private fun Init.generate() {
        cw.visit(V1_8, ACC_PUBLIC + ACC_SUPER, "EntryPoint", null,
                "java/lang/Object", null)

        constructor(cw)

        glVarDec.map { it.generate() }

        // Generate default values for graphs and digraphs
        initializer(cw, glVarDec)

        funcDef.map { it.generate() }

        main(cw)
        cw.visitEnd()

        val classBytes = cw.toByteArray()
        createExec(classBytes, state)
    }

    private fun GlVarDec.generate() {
        val initial: Any? = if (exp != null) {
            when (exp.type) {
                Type.int -> exp.text.toInt()
                Type.float -> exp.text.toFloat()
                Type.string -> withoutQuotes(exp.text)
                Type.bool -> exp.text.toBoolean()
                else -> null
            }
        } else {
            type.defaultValue()
        }

        val fv = cw.visitField(ACC_PUBLIC + ACC_STATIC, name, type.descriptor(), null, initial)
        fv.visitEnd()
    }

    private fun FuncDef.generate() {
        val function = state.symbolTable[name, SymType.FUNC] as Function
        val desc = function.signatureString()
        val mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, name, desc, null, null)

        val start = Label()
        val end = Label()

        mv.visitCode()
        mv.visitLabel(start)

        handleStmts(mv, scope, this, stmts, start to end)

        mv.visitInsn(RETURN)
        mv.visitLabel(end)

        while (varQueue.size > 0) {
            val (variable, ls, le) = varQueue.remove()
            val varDesc = variable.type.descriptor()
            val idx = getVarIndex(state, name, variable.name)
            mv.visitLocalVariable(variable.name, varDesc, null, ls, le, idx)
        }

        args.map {
            val arg = state.symbolTable[it.name, scope, SymType.VAR]!!
            val argDesc = it.type.descriptor()
            val idx = getVarIndex(state, name, arg.name)
            mv.visitLocalVariable(arg.name, argDesc, null, start, end, idx)
        }

        if (type == Type.void) {
            mv.visitInsn(RETURN)
        }

        mv.visitMaxs(0, 0)
        mv.visitEnd()
    }

    private fun Stmt.generate(mv: MethodVisitor, fd: FuncDef) {
        when (this) {
            is Expr -> this.generate(mv, fd)
            is VarDec -> this.generate(mv, fd)
            is Assignment -> this.generate(mv, fd)
            is Return -> this.generate(mv, fd)
            is Control -> this.generate(mv)
            is If -> this.generate(mv, fd)
            is For -> this.generate(mv, fd)
            is While -> this.generate(mv, fd)
            is Print -> this.generate(mv, fd)
            is Assertion -> this.generate(mv, fd)
        }
    }

    private fun Expr.generate(mv: MethodVisitor, fd: FuncDef) {
        when (this) {
            is Literal -> this.generate(mv)
            is UnaryExpr -> this.generate(mv, fd)
            is BinaryExpr -> this.generate(mv, fd)
            is Identifier -> this.generate(mv, fd)
            is FunctionCall -> this.generate(mv, fd)
            is Cast -> this.generate(mv, fd)
            is Graph -> this.generate(mv, fd)
        }
    }

    private fun Literal.generate(mv: MethodVisitor) {
        if (type == Type.int) {
            mv.visitLdcInsn(text.toInt())
        } else if (type == Type.float) {
            mv.visitLdcInsn(text.toFloat())
        } else if (type == Type.string) {
            mv.visitLdcInsn(withoutQuotes(text))
        } else if (type == Type.bool) {
            mv.visitLdcInsn(text.toBoolean())
        }
    }

    private fun UnaryExpr.generate(mv: MethodVisitor, fd: FuncDef) {
        expr.generate(mv, fd)

        if (operator == Operator.NOT) {
            not(mv, expr.type)
        } else if (operator == Operator.MINUS) {
            minus(mv, expr.type)
        }
    }

    private fun BinaryExpr.generate(mv: MethodVisitor, fd: FuncDef) {
        lhs.generate(mv, fd)
        rhs.generate(mv, fd)

        binaryOp(mv, operator, lhs.type)
    }

    private fun Identifier.generate(mv: MethodVisitor, fd: FuncDef) {
        val variable = state.symbolTable[name, "global", SymType.VAR] as Variable?
        if (variable != null) {
            mv.visitFieldInsn(GETSTATIC, "EntryPoint", variable.name,
                    variable.type.descriptor())
        } else {
            val idx = getVarIndex(state, fd.name, name)

            when (type) {
                Type.int -> mv.visitVarInsn(ILOAD, idx)
                Type.float -> mv.visitVarInsn(FLOAD, idx)
                Type.bool -> mv.visitVarInsn(ILOAD, idx)
                else -> mv.visitVarInsn(ALOAD, idx)
            }
        }
    }

    private fun FunctionCall.generate(mv: MethodVisitor, fd: FuncDef) {
        for (e in expr) {
            e.generate(mv, fd)
        }

        val function = state.symbolTable[name, SymType.FUNC] as Function

        if (function.isSpecial) {
            handleSpecial(mv, function)
        } else {
            val desc = function.signatureString()
            mv.visitMethodInsn(INVOKESTATIC, "EntryPoint", name, desc, false)
        }
    }

    private fun Cast.generate(mv: MethodVisitor, fd: FuncDef) {
        expr.generate(mv, fd)

        // Trivial case
        if (expr.type == type) {
            // Do nothing
        } else if (expr.type == Type.int && type == Type.float) {
            mv.visitInsn(I2F)
        } else if (expr.type == Type.float && type == Type.int) {
            mv.visitInsn(F2I)
        } else if (expr.type == Type.int && type == Type.string) {
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "toString",
                    "(I)Ljava/lang/String;", false)
        } else if (expr.type == Type.float && type == Type.string) {
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "toString",
                    "(F)Ljava/lang/String;", false)
        } else if (expr.type == Type.bool && type == Type.string) {
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "toString",
                    "(Z)Ljava/lang/String;", false)
        } else if (expr.type == Type.string && type == Type.int) {
            mv.visitMethodInsn(INVOKESTATIC, RT_STR_CLASS, "toInt",
                    "(Ljava/lang/String;)I", false)
        } else if (expr.type == Type.string && type == Type.float) {
            mv.visitMethodInsn(INVOKESTATIC, RT_STR_CLASS, "toFloat",
                    "(Ljava/lang/String;)F", false)
        } else if (expr.type == Type.string && type == Type.bool) {
            mv.visitMethodInsn(INVOKESTATIC, RT_STR_CLASS, "toBool",
                    "(Ljava/lang/String;)Z", false)
        } else {
            throw IllegalStateException()
        }
    }

    private fun Graph.generate(mv: MethodVisitor, fd: FuncDef) {
        if (gtype == GraphType.GRAPH) {
            mv.visitTypeInsn(NEW, GRAPH_CLASS)
        } else {
            mv.visitTypeInsn(NEW, DIGRAPH_CLASS)
        }

        mv.visitInsn(DUP)
        num.generate(mv, fd)

        if (gtype == GraphType.GRAPH) {
            mv.visitMethodInsn(INVOKESPECIAL, GRAPH_CLASS, "<init>", "(I)V", false)
        } else {
            mv.visitMethodInsn(INVOKESPECIAL, DIGRAPH_CLASS,
                    "<init>", "(I)V", false)
        }

        for (edge in edges) {
            mv.visitInsn(DUP)
            edge.source.generate(mv, fd)
            edge.target.generate(mv, fd)

            if (gtype == GraphType.GRAPH) {
                mv.visitMethodInsn(INVOKEVIRTUAL, GRAPH_CLASS,
                        "addEdge", "(II)V", false)
            } else {
                mv.visitMethodInsn(INVOKEVIRTUAL, DIGRAPH_CLASS,
                        "addEdge", "(II)V", false)
            }
        }
    }

    private fun VarDec.generate(mv: MethodVisitor, fd: FuncDef) {
        val idx = getVarIndex(state, fd.name, name)

        if (exp == null) {
            pushDefaultToStack(mv, type)
        } else {
            exp.generate(mv, fd)
        }

        when (type) {
            Type.int -> mv.visitVarInsn(ISTORE, idx)
            Type.float -> mv.visitVarInsn(FSTORE, idx)
            Type.bool -> mv.visitVarInsn(ISTORE, idx)
            else -> mv.visitVarInsn(ASTORE, idx)
        }
    }

    private fun Assignment.generate(mv: MethodVisitor, fd: FuncDef) {
        val variable = lhs.referencedVar!!
        rhs.generate(mv, fd)

        if (variable.scope == "global") {
            mv.visitFieldInsn(PUTSTATIC, "EntryPoint", variable.name, variable.type.descriptor())
        } else {
            val idx = getVarIndex(state, fd.name, variable.name)

            when (lhs.type) {
                Type.int -> mv.visitVarInsn(ISTORE, idx)
                Type.float -> mv.visitVarInsn(FSTORE, idx)
                Type.bool -> mv.visitVarInsn(ISTORE, idx)
                else -> mv.visitVarInsn(ASTORE, idx)
            }
        }
    }

    private fun Return.generate(mv: MethodVisitor, fd: FuncDef) {
        if (fd.type != Type.void) {
            expr?.generate(mv, fd)

            when (fd.type) {
                Type.int -> mv.visitInsn(IRETURN)
                Type.float -> mv.visitInsn(FRETURN)
                Type.bool -> mv.visitInsn(IRETURN)
                else -> mv.visitInsn(ARETURN)
            }
        } else {
            mv.visitInsn(RETURN)
        }
    }

    private fun Control.generate(mv: MethodVisitor) {
        when (type) {
            ControlType.CONTINUE -> mv.visitJumpInsn(GOTO, continueTargetLabel)
            ControlType.BREAK -> mv.visitJumpInsn(GOTO, breakTargetLabel)
        }
    }

    private fun If.generate(mv: MethodVisitor, fd: FuncDef) {
        val start = Label()
        val end = Label()
        val finish = Label()

        mv.visitLabel(start)
        cond.generate(mv, fd)
        mv.visitJumpInsn(IFEQ, end)

        handleStmts(mv, scope, fd, stmts, start to end)

        mv.visitJumpInsn(GOTO, finish)
        mv.visitLabel(end)

        for (elif in elifs) {
            elif.generate(mv, fd, finish)
        }

        elsec?.generate(mv, fd, finish)

        mv.visitLabel(finish)
    }

    private fun Elif.generate(mv: MethodVisitor, fd: FuncDef, finish: Label) {
        val start = Label()
        val end = Label()

        mv.visitLabel(start)
        cond.generate(mv, fd)
        mv.visitJumpInsn(IFEQ, end)

        handleStmts(mv, scope, fd, stmts, start to end)

        mv.visitJumpInsn(GOTO, finish)
        mv.visitLabel(end)
    }

    private fun Else.generate(mv: MethodVisitor, fd: FuncDef, finish: Label) {
        val start = Label()
        val end = Label()

        mv.visitLabel(start)

        handleStmts(mv, scope, fd, stmts, start to end)

        mv.visitJumpInsn(GOTO, finish)
        mv.visitLabel(end)
    }

    private fun For.generate(mv: MethodVisitor, fd: FuncDef) {
        val start = Label()
        val modifier = Label()
        val end = Label()

        continueTargetLabel = modifier
        breakTargetLabel = end

        initial.generate(mv, fd)

        mv.visitLabel(start)

        cond.generate(mv, fd)
        mv.visitJumpInsn(IFEQ, end)

        handleStmts(mv, scope, fd, stmts, start to end)

        mv.visitLabel(modifier)
        mod.generate(mv, fd)
        mv.visitJumpInsn(GOTO, start)
        mv.visitLabel(end)

        continueTargetLabel = null
        breakTargetLabel = null
    }

    private fun While.generate(mv: MethodVisitor, fd: FuncDef) {
        val start = Label()
        val end = Label()

        continueTargetLabel = start
        breakTargetLabel = end

        mv.visitLabel(start)
        cond.generate(mv, fd)
        mv.visitJumpInsn(IFEQ, end)

        handleStmts(mv, scope, fd, stmts, start to end)

        mv.visitJumpInsn(GOTO, start)
        mv.visitLabel(end)

        continueTargetLabel = null
        breakTargetLabel = null
    }

    private fun Print.generate(mv: MethodVisitor, fd: FuncDef) {
        expr.generate(mv, fd)

        when (expr.type) {
            Type.int -> mv.visitMethodInsn(INVOKESTATIC, RT_IO_CLASS, "print",
                    "(I)V", false)
            Type.float -> mv.visitMethodInsn(INVOKESTATIC, RT_IO_CLASS, "print",
                    "(F)V", false)
            Type.bool -> mv.visitMethodInsn(INVOKESTATIC, RT_IO_CLASS, "print",
                    "(Z)V", false)
            else -> mv.visitMethodInsn(INVOKESTATIC, RT_IO_CLASS, "print",
                    "(Ljava/lang/Object;)V", false)
        }
    }

    private fun Assertion.generate(mv: MethodVisitor, fd: FuncDef) {
        expr.generate(mv, fd)
        mv.visitLdcInsn(this.location.line)

        mv.visitMethodInsn(INVOKESTATIC, RT_ASSERT_CLASS, "assertF", "(ZI)V",
                false)
    }
}
