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

package sron.cg.compiler.phase.generation.helper

import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.*
import sron.cg.compiler.ast.Expr
import sron.cg.compiler.ast.Operator
import sron.cg.type.Type

/**
 * Generates code for binary AND and OR operations.
 *
 * For example, the operation i && j gets translated to:
 *
 * temp = i & j // Bitwise and
 * if (temp == 0) goto FALSE
 * 1
 * goto END
 * FALSE:
 * 0
 * goto END
 * END:
 *
 * This assumes that the two operands in the stack are either a 0 or a 1
 *
 * @param mv The method visitor
 * @param op The operator, whether OR or AND
 */
private fun binaryAndOr(mv: MethodVisitor, op: Operator) {
    val opcode = if (op == Operator.AND) {
        IAND
    } else {
        IOR
    }

    val start = Label()
    val isFalse = Label()
    val end = Label()

    mv.visitLabel(start)
    mv.visitInsn(opcode)
    mv.visitJumpInsn(IFEQ, isFalse)
    mv.visitInsn(ICONST_1)
    mv.visitJumpInsn(GOTO, end)

    mv.visitLabel(isFalse)
    mv.visitInsn(ICONST_0)
    mv.visitJumpInsn(GOTO, end)

    mv.visitLabel(end)
}

private fun intEqual(mv: MethodVisitor) {
    val start = Label()
    val equal = Label()
    val end = Label()

    mv.visitLabel(start)
    mv.visitJumpInsn(IF_ICMPEQ, equal)

    mv.visitInsn(ICONST_0)
    mv.visitJumpInsn(GOTO, end)

    mv.visitLabel(equal)
    mv.visitInsn(ICONST_1)

    mv.visitLabel(end)
}

private fun floatEqual(mv: MethodVisitor) {
    val start = Label()
    val equal = Label()
    val end = Label()

    mv.visitLabel(start)
    mv.visitInsn(FCMPG)
    mv.visitJumpInsn(IFEQ, equal)

    mv.visitInsn(ICONST_0)
    mv.visitJumpInsn(GOTO, end)

    mv.visitLabel(equal)
    mv.visitInsn(ICONST_1)

    mv.visitLabel(end)
}

private fun equal(mv: MethodVisitor, lhs: Expr, rhs: Expr) {
    if (lhs.type == Type.int && rhs.type == Type.int) {
        intEqual(mv)
    } else if (lhs.type == Type.float && rhs.type == Type.float) {
        floatEqual(mv)
    } else if (lhs.type == Type.bool && rhs.type == Type.bool) {
        intEqual(mv)
    } else if (lhs.type == Type.string && rhs.type == Type.string) {

    }
}

fun binaryOp(mv: MethodVisitor, op: Operator, type: Type, lhs: Expr, rhs: Expr) {
    when (op) {
        Operator.ADD -> {
            if (type == Type.int) {
                mv.visitInsn(IADD)
            } else {
                mv.visitInsn(FADD)
            }
        }
        Operator.SUB -> {
            if (type == Type.int) {
                mv.visitInsn(ISUB)
            } else {
                return mv.visitInsn(FSUB)
            }
        }
        Operator.MUL -> {
            if (type == Type.int) {
                mv.visitInsn(IMUL)
            } else {
                mv.visitInsn(FMUL)
            }
        }
        Operator.DIV -> {
            if (type == Type.int) {
                mv.visitInsn(IDIV)
            } else {
                mv.visitInsn(FDIV)
            }
        }
        Operator.MOD -> {
            if (type == Type.int) {
                mv.visitInsn(IREM)
            } else {
                mv.visitInsn(FREM)
            }
        }

        Operator.AND -> {
            binaryAndOr(mv, Operator.AND)
        }
        Operator.OR -> {
            binaryAndOr(mv, Operator.OR)
        }

        Operator.EQUAL -> {
            equal(mv, lhs, rhs)
        }
        else -> {
        }
    }
}
