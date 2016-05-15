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
import sron.cg.type.JVMDescriptor
import sron.cg.type.Type
import sron.cg.type.defaultValue
import java.io.File
import java.util.*

object Generation {
    private val cw = ClassWriter(COMPUTE_FRAMES)

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

        File("EntryPoint.class").outputStream().use {
            it.write(cw.toByteArray())
        }
    }

    private fun GlVarDec.generate() {
        val initial: Any? = if (exp != null) {
            when (exp.type) {
                Type.int -> exp.text.toInt()
                Type.float -> exp.text.toFloat()
                Type.string -> exp.text.subSequence(1, exp.text.length - 1)
                Type.bool -> exp.text.toBoolean()
                else -> null
            }
        } else {
            type.defaultValue()
        }

        val fv = cw.visitField(ACC_PUBLIC + ACC_STATIC, name, type.JVMDescriptor(), null, initial)
        fv.visitEnd()
    }

    private fun FuncDef.generate(s: State) {
        val func = s.symbolTable.getSymbol(name, SymType.FUNC) as Function
        val desc = signatureString(func)
        val mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, name, desc, null, null)
        val varQueue = LinkedList<Variable>()

        mv.visitCode()

        val ls = Label()
        mv.visitLabel(ls)

        for (stmt in stmts) {
            if (stmt is VarDec) {
                val v = s.symbolTable.getSymbol(stmt.name, scope, SymType.VAR) as Variable
                varQueue.add(v)
                stmt.generate(s, mv, this)
            } else {
                stmt.generate(s, mv, this)
            }
        }

        mv.visitInsn(RETURN)
        val le = Label()
        mv.visitLabel(le)

        while (varQueue.size > 0) {
            val variable = varQueue.remove()
            val varDesc = variable.type.JVMDescriptor()
            val idx = getVarIndex(s, name, variable.name)
            mv.visitLocalVariable(variable.name, varDesc, null, ls, le, idx)
        }

        args.map {
            val arg = s.symbolTable.getSymbol(it.name, scope, SymType.VAR)!!
            val argDesc = it.type.JVMDescriptor()
            val idx = getVarIndex(s, name, arg.name)
            mv.visitLocalVariable(arg.name, argDesc, null, ls, le, idx)
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
        //is Return -> this.generate(s, mv)
        //is If -> this.generate(s, mv)
        //is For -> this.generate(s, mv)
        //is While -> this.generate(s, mv)
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
        }
    }

    private fun Literal.generate(mv: MethodVisitor) {
        if (type == Type.int) {
            mv.visitLdcInsn(text.toInt())
        } else if (type == Type.float) {
            mv.visitLdcInsn(text.toFloat())
        } else if (type == Type.string) {
            mv.visitLdcInsn(text.trimQuotes())
        } else if (type == Type.bool) {
            mv.visitLdcInsn(text.toBoolean())
        }
    }

    private fun UnaryExpr.generate(s: State, mv: MethodVisitor, fd: FuncDef) {
        expr.generate(s, mv, fd)
        when (operator) {
            Operator.NOT -> not(mv)
            Operator.MINUS -> minus(mv, type)
            else -> {
            }
        }
    }

    private fun BinaryExpr.generate(s: State, mv: MethodVisitor, fd: FuncDef) {
        lhs.generate(s, mv, fd)
        rhs.generate(s, mv, fd)

        binaryOp(mv, operator, type, lhs.type)
    }

    private fun Identifier.generate(s: State, mv: MethodVisitor, fd: FuncDef) {
        identifier(mv, this, s, fd)
    }

    private fun FunctionCall.generate(s: State, mv: MethodVisitor, fd: FuncDef) {
        for (e in expr) {
            e.generate(s, mv, fd)
        }

        val function = s.symbolTable.getSymbol(name, SymType.FUNC) as Function
        val desc = signatureString(function)

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
        } else if (expr.type == Type.float && type == Type.float) {
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "toString",
                    "(F)Ljava/lang/String;", false);
        } else if (expr.type == Type.bool && type == Type.string) {
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "toString",
                    "(Z)Ljava/lang/String;", false);
        }
    }

    private fun VarDec.generate(s: State, mv: MethodVisitor, fd: FuncDef) {
        val idx = getVarIndex(s, fd.name, name)

        if (exp == null) {
            generateDefault(mv, type)
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
