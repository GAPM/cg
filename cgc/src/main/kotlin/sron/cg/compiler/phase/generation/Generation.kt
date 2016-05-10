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
        val desc = signatureString(this)
        val mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, name, desc, null, null)
        val varQueue = LinkedList<Variable>()

        mv.visitCode()

        val ls = Label()
        mv.visitLabel(ls)

        for (stmt in stmts) {
            if (stmt is VarDec) {
                val v = s.symbolTable.getSymbol(stmt.name, scope, SymType.VAR) as Variable
                varQueue.add(v)
            } else {
                stmt.generate(s, mv)
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

        mv.visitMaxs(0, 0)
        mv.visitEnd()
    }

    private fun Stmt.generate(s: State, mv: MethodVisitor) {
        when (this) {
            is Expr -> this.generate(s, mv)
        //is Assignment -> this.generate(s, mv)
        //is Return -> this.generate(s, mv)
        //is If -> this.generate(s, mv)
        //is For -> this.generate(s, mv)
        //is While -> this.generate(s, mv)
        }
    }

    private fun Expr.generate(s: State, mv: MethodVisitor) {
        when (this) {
            is Literal -> this.generate(mv)
            is UnaryExpr -> this.generate(s, mv)
            is BinaryExpr -> this.generate(s, mv)
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

    private fun UnaryExpr.generate(s: State, mv: MethodVisitor) {
        expr.generate(s, mv)
        when (operator) {
            Operator.NOT -> not(mv)
            Operator.MINUS -> minus(mv, type)
            else -> {
            }
        }
    }

    private fun BinaryExpr.generate(s: State, mv: MethodVisitor) {
        lhs.generate(s, mv)
        rhs.generate(s, mv)

        binaryOp(mv, operator, type, lhs, rhs)
    }
}
