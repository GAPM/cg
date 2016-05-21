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

package sron.cg.compiler.phase.generation

import org.objectweb.asm.ClassWriter
import org.objectweb.asm.ClassWriter.COMPUTE_FRAMES
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.*
import sron.cg.compiler.State
import sron.cg.compiler.ast.*
import sron.cg.compiler.phase.generation.helper.*
import sron.cg.symbol.Function
import sron.cg.symbol.SymType
import sron.cg.symbol.Variable
import sron.cg.type.Type
import sron.cg.type.defaultValue
import sron.cg.type.descriptor
import java.util.*

object Generation {
    private val cw = ClassWriter(COMPUTE_FRAMES)
    private val varQueue = LinkedList<Triple<Variable, Label, Label>>()
    private var loopStart: Label? = null
    private var loopEnd: Label? = null

    operator fun invoke(s: State, init: Init) = init.generate(s)

    private fun Init.generate(s: State) {
        cw.visit(V1_8, ACC_PUBLIC + ACC_SUPER, "EntryPoint", null, "java/lang/Object", null)

        constructor(cw)

        for (gvd in glVarDec) {
            gvd.generate()
        }

        // Generate default values for graphs and digraphs
        initializer(cw, glVarDec)

        for (fd in funcDef) {
            fd.generate(s)
        }

        main(cw)
        cw.visitEnd()

        val classBytes = cw.toByteArray()
        createExec(classBytes, s)
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

    private fun FuncDef.generate(s: State) {
        val function = s.symbolTable.getSymbol(name, SymType.FUNC) as Function
        val desc = function.signatureString()
        val mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, name, desc, null, null)

        val start = Label()
        val end = Label()

        mv.visitCode()
        mv.visitLabel(start)

        for (stmt in stmts) {
            if (stmt is VarDec) {
                val variable = s.symbolTable.getSymbol(stmt.name, scope, SymType.VAR) as Variable
                varQueue += Triple(variable, start, end)

                stmt.generate(s, mv, this)
            } else {
                stmt.generate(s, mv, this)
            }
        }

        mv.visitInsn(RETURN)
        mv.visitLabel(end)

        while (varQueue.size > 0) {
            val (variable, ls, le) = varQueue.remove()
            val varDesc = variable.type.descriptor()
            val idx = getVarIndex(s, name, variable.name)
            mv.visitLocalVariable(variable.name, varDesc, null, ls, le, idx)
        }

        args.map {
            val arg = s.symbolTable.getSymbol(it.name, scope, SymType.VAR)!!
            val argDesc = it.type.descriptor()
            val idx = getVarIndex(s, name, arg.name)
            mv.visitLocalVariable(arg.name, argDesc, null, start, end, idx)
        }

        if (type == Type.void) {
            mv.visitInsn(RETURN)
        }

        mv.visitMaxs(0, 0)
        mv.visitEnd()
    }

    private fun Stmt.generate(s: State, mv: MethodVisitor, fd: FuncDef) {
        when (this) {
            is Expr -> this.generate(s, mv, fd)
            is VarDec -> this.generate(s, mv, fd)
            is Assignment -> this.generate(s, mv, fd)
            is Return -> this.generate(s, mv, fd)
            is Control -> this.generate(mv)
            is If -> this.generate(s, mv, fd)
            is For -> this.generate(s, mv, fd)
            is While -> this.generate(s, mv, fd)
            is Print -> this.generate(s, mv, fd)
        }
    }

    private fun Expr.generate(s: State, mv: MethodVisitor, fd: FuncDef) {
        when (this) {
            is Literal -> this.generate(mv)
            is UnaryExpr -> this.generate(s, mv, fd)
            is BinaryExpr -> this.generate(s, mv, fd)
            is Identifier -> this.generate(s, mv, fd)
            is FunctionCall -> this.generate(s, mv, fd)
            is Cast -> this.generate(s, mv, fd)
            is Graph -> this.generate(s, mv, fd)
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

    private fun UnaryExpr.generate(s: State, mv: MethodVisitor, fd: FuncDef) {
        expr.generate(s, mv, fd)

        if (operator == Operator.NOT) {
            not(mv)
        } else if (operator == Operator.MINUS) {
            minus(mv, type)
        }
    }

    private fun BinaryExpr.generate(s: State, mv: MethodVisitor, fd: FuncDef) {
        lhs.generate(s, mv, fd)
        rhs.generate(s, mv, fd)

        binaryOp(mv, operator, lhs.type)
    }

    private fun Identifier.generate(s: State, mv: MethodVisitor, fd: FuncDef) {
        val variable = s.symbolTable.getSymbol(name, "global", SymType.VAR) as Variable?
        if (variable != null) {
            mv.visitFieldInsn(GETSTATIC, "EntryPoint", variable.name, variable.type.descriptor())
        } else {
            val idx = getVarIndex(s, fd.name, name)

            when (type) {
                Type.int -> mv.visitVarInsn(ILOAD, idx)
                Type.float -> mv.visitVarInsn(FLOAD, idx)
                Type.bool -> mv.visitVarInsn(ILOAD, idx)
                else -> mv.visitVarInsn(ALOAD, idx)
            }
        }
    }

    private fun FunctionCall.generate(s: State, mv: MethodVisitor, fd: FuncDef) {
        for (e in expr) {
            e.generate(s, mv, fd)
        }

        val function = s.symbolTable.getSymbol(name, SymType.FUNC) as Function
        val desc = function.signatureString()

        mv.visitMethodInsn(INVOKESTATIC, "EntryPoint", name, desc, false)
    }

    private fun Cast.generate(s: State, mv: MethodVisitor, fd: FuncDef) {
        expr.generate(s, mv, fd)

        if (expr.type == Type.int && type == Type.float) {
            mv.visitInsn(I2F)
        } else if (expr.type == Type.float && type == Type.int) {
            mv.visitInsn(F2I)
        } else if (expr.type == Type.int && type == Type.string) {
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "toString",
                    "(I)Ljava/lang/String;", false);
        } else if (expr.type == Type.float && type == Type.string) {
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "toString",
                    "(F)Ljava/lang/String;", false);
        } else if (expr.type == Type.bool && type == Type.string) {
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "toString",
                    "(Z)Ljava/lang/String;", false);
        }
    }

    private fun Graph.generate(s: State, mv: MethodVisitor, fd: FuncDef) {
        if (gtype == GraphType.GRAPH) {
            mv.visitTypeInsn(NEW, "sron/cg/runtime/graph/Graph")
        } else {
            mv.visitTypeInsn(NEW, "sron/cg/runtime/graph/DiGraph");
        }

        mv.visitInsn(DUP)
        num.generate(s, mv, fd)

        if (gtype == GraphType.GRAPH) {
            mv.visitMethodInsn(INVOKESPECIAL, "sron/cg/runtime/graph/Graph", "<init>", "(I)V", false);
        } else {
            mv.visitMethodInsn(INVOKESPECIAL, "sron/cg/runtime/graph/DiGraph",
                    "<init>", "(I)V", false);
        }

        for (edge in edges) {
            mv.visitInsn(DUP)
            edge.source.generate(s, mv, fd)
            edge.target.generate(s, mv, fd)

            if (gtype == GraphType.GRAPH) {
                mv.visitMethodInsn(INVOKEVIRTUAL, "sron/cg/runtime/graph/Graph",
                        "addEdge", "(II)V", false);
            } else {
                mv.visitMethodInsn(INVOKEVIRTUAL, "sron/cg/runtime/graph/DiGraph",
                        "addEdge", "(II)V", false);
            }
        }
    }

    private fun VarDec.generate(s: State, mv: MethodVisitor, fd: FuncDef) {
        val idx = getVarIndex(s, fd.name, name)

        if (exp == null) {
            pushDefaultToStack(mv, type)
        } else {
            exp.generate(s, mv, fd)
        }

        when (type) {
            Type.int -> mv.visitVarInsn(ISTORE, idx)
            Type.float -> mv.visitVarInsn(FSTORE, idx)
            Type.bool -> mv.visitVarInsn(ISTORE, idx)
            else -> mv.visitVarInsn(ASTORE, idx)
        }
    }

    private fun Assignment.generate(s: State, mv: MethodVisitor, fd: FuncDef) {
        val variable = lhs.referencedVar!!

        if (variable.scope == "global") {
            mv.visitFieldInsn(PUTSTATIC, "EntryPoint", variable.name, variable.type.descriptor())
        } else {
            val idx = getVarIndex(s, fd.name, variable.name)

            rhs.generate(s, mv, fd)

            when (lhs.type) {
                Type.int -> mv.visitVarInsn(ISTORE, idx)
                Type.float -> mv.visitVarInsn(FSTORE, idx)
                Type.bool -> mv.visitVarInsn(ISTORE, idx)
                else -> mv.visitVarInsn(ASTORE, idx)
            }
        }
    }

    private fun Return.generate(s: State, mv: MethodVisitor, fd: FuncDef) {
        if (fd.type != Type.void) {
            expr?.generate(s, mv, fd)

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
            ControlType.CONTINUE -> mv.visitJumpInsn(GOTO, loopStart)
            ControlType.BREAK -> mv.visitJumpInsn(GOTO, loopEnd)
        }
    }

    private fun If.generate(s: State, mv: MethodVisitor, fd: FuncDef) {
        val start = Label()
        val end = Label()
        val finish = Label()

        mv.visitLabel(start)
        cond.generate(s, mv, fd)
        mv.visitJumpInsn(IFEQ, end)

        for (stmt in stmts) {
            if (stmt is VarDec) {
                val variable = s.symbolTable.getSymbol(stmt.name, scope, SymType.VAR) as Variable
                varQueue += Triple(variable, start, end)

                stmt.generate(s, mv, fd)
            } else {
                stmt.generate(s, mv, fd)
            }
        }
        mv.visitJumpInsn(GOTO, finish)
        mv.visitLabel(end)

        for (elif in elifs) {
            elif.generate(s, mv, fd, finish)
        }

        elsec?.generate(s, mv, fd, finish)

        mv.visitLabel(finish)
    }

    private fun Elif.generate(s: State, mv: MethodVisitor, fd: FuncDef, finish: Label) {
        val start = Label()
        val end = Label()

        mv.visitLabel(start)
        cond.generate(s, mv, fd)
        mv.visitJumpInsn(IFEQ, end)

        for (stmt in stmts) {
            if (stmt is VarDec) {
                val variable = s.symbolTable.getSymbol(stmt.name, scope, SymType.VAR) as Variable
                varQueue += Triple(variable, start, end)

                stmt.generate(s, mv, fd)
            } else {
                stmt.generate(s, mv, fd)
            }
        }

        mv.visitJumpInsn(GOTO, finish)
        mv.visitLabel(end)
    }

    private fun Else.generate(s: State, mv: MethodVisitor, fd: FuncDef, finish: Label) {
        val start = Label()
        val end = Label()

        mv.visitLabel(start)

        for (stmt in stmts) {
            if (stmt is VarDec) {
                val variable = s.symbolTable.getSymbol(stmt.name, scope, SymType.VAR) as Variable
                varQueue += Triple(variable, start, end)

                stmt.generate(s, mv, fd)
            } else {
                stmt.generate(s, mv, fd)
            }
        }

        mv.visitJumpInsn(GOTO, finish)
        mv.visitLabel(end)
    }

    private fun For.generate(s: State, mv: MethodVisitor, fd: FuncDef) {
        val start = Label()
        val end = Label()

        loopStart = start
        loopEnd = end

        initial.generate(s, mv, fd)

        mv.visitLabel(start)

        cond.generate(s, mv, fd)
        mv.visitJumpInsn(IFEQ, end)

        for (stmt in stmts) {
            if (stmt is VarDec) {
                val variable = s.symbolTable.getSymbol(stmt.name, scope, SymType.VAR) as Variable
                varQueue += Triple(variable, start, end)

                stmt.generate(s, mv, fd)
            } else {
                stmt.generate(s, mv, fd)
            }
        }

        mod.generate(s, mv, fd)
        mv.visitJumpInsn(GOTO, start)
        mv.visitLabel(end)

        loopStart = null
        loopEnd = null
    }

    private fun While.generate(s: State, mv: MethodVisitor, fd: FuncDef) {
        val start = Label()
        val end = Label()

        loopStart = start
        loopEnd = end

        mv.visitLabel(start)
        cond.generate(s, mv, fd)
        mv.visitJumpInsn(IFEQ, end)

        for (stmt in stmts) {
            if (stmt is VarDec) {
                val variable = s.symbolTable.getSymbol(stmt.name, scope, SymType.VAR) as Variable
                varQueue += Triple(variable, start, end)

                stmt.generate(s, mv, fd)
            } else {
                stmt.generate(s, mv, fd)
            }
        }

        mv.visitJumpInsn(GOTO, start)
        mv.visitLabel(end)

        loopStart = null
        loopEnd = null
    }

    private fun Print.generate(s: State, mv: MethodVisitor, fd: FuncDef) {
        expr.generate(s, mv, fd)

        when (expr.type) {
            Type.int -> mv.visitMethodInsn(INVOKESTATIC,
                    "sron/cg/runtime/rt/Print", "print", "(I)V", false)
            Type.float -> mv.visitMethodInsn(INVOKESTATIC,
                    "sron/cg/runtime/rt/Print", "print", "(F)V", false)
            Type.bool -> mv.visitMethodInsn(INVOKESTATIC,
                    "sron/cg/runtime/rt/Print", "print", "(Z)V", false)
            else -> mv.visitMethodInsn(INVOKESTATIC,
                    "sron/cg/runtime/rt/Print", "print", "(Ljava/lang/Object;)V", false)
        }
    }
}
