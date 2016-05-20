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
import sron.cg.compiler.ast.Operator
import sron.cg.type.Type

/**
 * Generates code for string concatenation (operator + with operands of type
 * string)
 */
private fun addString(mv: MethodVisitor) {
    mv.visitMethodInsn(INVOKESTATIC, "sron/cg/runtime/rt/Str", "concat",
            "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", false);
}

/**
 * Generates code for addition (operator +)
 *
 * @param mv The method visitor
 * @param type The type of the operands
 */
private fun add(mv: MethodVisitor, type: Type) = when (type) {
    Type.int -> mv.visitInsn(IADD)
    Type.float -> mv.visitInsn(FADD)
    else -> addString(mv)
}

/**
 * Generates code for substraction (operator -)
 *
 * @param mv The method visitor
 * @param type The type of the operands
 */
private fun sub(mv: MethodVisitor, type: Type) = when (type) {
    Type.int -> mv.visitInsn(ISUB)
    else -> mv.visitInsn(FSUB)
}

/**
 * Generates code for division (operator /)
 *
 * @param mv The method visitor
 * @param type The type of the operands
 */
private fun div(mv: MethodVisitor, type: Type) = when (type) {
    Type.int -> mv.visitInsn(IDIV)
    else -> mv.visitInsn(FDIV)
}

/**
 * Generates code for module (operator %)
 *
 * @param mv The method visitor
 * @param type The type of the operands
 */
private fun mod(mv: MethodVisitor, type: Type) = when (type) {
    Type.int -> mv.visitInsn(IREM)
    else -> mv.visitInsn(FREM)
}

/**
 * Generates code for multiplication (operator *)
 *
 * @param mv The method visitor
 * @param type The type of the operands
 */
private fun mul(mv: MethodVisitor, type: Type) = when (type) {
    Type.int -> mv.visitInsn(IMUL)
    else -> mv.visitInsn(FMUL)
}

/**
 * Generates code for binary AND and OR operations.
 *
 * i && j gets translated to (assuming i and j are both on top of the stack):
 *
 * IAND
 * IFEQ false
 * ICONST_1
 * goto end:
 * false:
 * ICONST_0
 * GOTO end
 * end:
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

/**
 * Generates code for integer equality
 *
 * i (==|!=) j gets translated to (assuming i and j are both on top of the
 * stack):
 *
 * IF_ICMPEQ equal // If both integers are equal, go to `equal`
 * ICONST_0 // ICONST_1 if op is NOT_EQUAL
 * GOTO end
 *
 * equal:
 * ICONST_1 // ICONST_0 if op is NOT_EQUAL
 * GOTO end
 *
 * end:
 *
 * @param mv The method visitor
 * @param op The operation performed
 */
private fun intEquality(mv: MethodVisitor, op: Operator) {
    val start = Label()
    val equal = Label()
    val end = Label()

    mv.visitLabel(start)
    mv.visitJumpInsn(IF_ICMPEQ, equal)

    if (op == Operator.EQUAL) {
        mv.visitInsn(ICONST_0)
    } else {
        mv.visitInsn(ICONST_1)
    }

    mv.visitJumpInsn(GOTO, end)

    mv.visitLabel(equal)
    if (op == Operator.EQUAL) {
        mv.visitInsn(ICONST_1)
    } else {
        mv.visitInsn(ICONST_0)
    }

    mv.visitLabel(end)
}

/**
 * Generates code for float equality
 *
 * i (==|!=) j gets translated to (assuming i and j are both on top of the
 * stack):
 *
 * FCMPG // Pushes the result to the stack
 * IFEQ equal // If result if 0, both floats were equal, so go to `equal`
 * ICONST_0 // ICONST_1 if op is NOT_EQUAL
 * GOTO end
 *
 * equal:
 * ICONST_1 // ICONST_0 if op is NOT_EQUAL
 * GOTO end
 *
 * end:
 *
 * @param mv The method visitor
 * @param op The operation performed
 */
private fun floatEquality(mv: MethodVisitor, op: Operator) {
    val start = Label()
    val equal = Label()
    val end = Label()

    mv.visitLabel(start)
    mv.visitInsn(FCMPG)
    mv.visitJumpInsn(IFEQ, equal)
    if (op == Operator.EQUAL) {
        mv.visitInsn(ICONST_0)
    } else {
        mv.visitInsn(ICONST_1)
    }
    mv.visitJumpInsn(GOTO, end)

    mv.visitLabel(equal)
    if (op == Operator.EQUAL) {
        mv.visitInsn(ICONST_1)
    } else {
        mv.visitInsn(ICONST_0)
    }

    mv.visitLabel(end)
}

/**
 * Generates code for string equality
 *
 * Assuming that both string references to be compared are on top of the stack,
 * then the static method from the runtime `Str.equal` gets called and its
 * result is pushed to the stack. If operator is NOT_EQUAL the the result gets
 * inverted.
 *
 * @param mv The method visitor
 * @param op The operator
 */
private fun stringEquality(mv: MethodVisitor, op: Operator) {
    mv.visitMethodInsn(INVOKESTATIC, "sron/cg/runtime/rt/Str", "equal",
            "(Ljava/lang/String;Ljava/lang/String;)Z", false)
    if (op == Operator.NOT_EQUAL) {
        not(mv)
    }
}

/**
 * Generates equality check code for expressions
 *
 * @param mv The method visitor
 * @param type The type of the expression
 */
private fun equal(mv: MethodVisitor, type: Type) {
    if (type == Type.int) {
        intEquality(mv, Operator.EQUAL)
    } else if (type == Type.float) {
        floatEquality(mv, Operator.EQUAL)
    } else if (type == Type.bool) {
        intEquality(mv, Operator.EQUAL)
    } else if (type == Type.string) {
        stringEquality(mv, Operator.EQUAL)
    }
}

/**
 * Generates inequality check code for expressions
 *
 * @param mv The method visitor
 * @param type The type of the expression
 */
private fun notEqual(mv: MethodVisitor, type: Type) {
    if (type == Type.int) {
        intEquality(mv, Operator.NOT_EQUAL)
    } else if (type == Type.float) {
        floatEquality(mv, Operator.NOT_EQUAL)
    } else if (type == Type.bool) {
        intEquality(mv, Operator.NOT_EQUAL)
    } else if (type == Type.string) {
        stringEquality(mv, Operator.NOT_EQUAL)
    }
}

/**
 * Receives an operator and returns the bytecode needed for that operation
 * applied to integers
 *
 * @param op The operator
 * @return The opcode needed
 */
private fun fromOpToIntCompBytecode(op: Operator) = if (op == Operator.LESS) {
    IF_ICMPLT
} else if (op == Operator.LESS_EQUAL) {
    IF_ICMPLE
} else if (op == Operator.GREATER) {
    IF_ICMPGT
} else {
    IF_ICMPGE
}

/**
 * Generates code for comparison operations (>, <, >=, <=) between integers.
 *
 * i (>|<|<=|>=) j gets translated to (assuming i and j are both on top of the
 * stack):
 *
 * OPCODE isTrue // Replaced by the corresponding opcode of the operator
 * ICONST_0
 * GOTO end
 * isTrue:
 * ICONST_1
 * end:
 *
 * @param mv The method visitor
 * @param op The operation
 */
private fun intComparison(mv: MethodVisitor, op: Operator) {
    val start = Label()
    val isTrue = Label()
    val end = Label()
    val opcode = fromOpToIntCompBytecode(op)

    mv.visitLabel(start)
    mv.visitJumpInsn(opcode, isTrue)
    mv.visitInsn(ICONST_0)
    mv.visitJumpInsn(GOTO, end)
    mv.visitLabel(isTrue)
    mv.visitInsn(ICONST_1)
    mv.visitLabel(end)
}

/**
 * Receives an operator and returns the bytecodes needed for that operation
 * applied to floats
 *
 * @param op The operator
 * @return A pair with both opcodes needed
 */
private fun fromOpToFloatCompBytecodes(op: Operator) = if (op == Operator.LESS) {
    FCMPG to IFLT
} else if (op == Operator.LESS_EQUAL) {
    FCMPG to IFLE
} else if (op == Operator.GREATER) {
    FCMPL to IFGT
} else {
    FCMPL to IFGE
}

/**
 * Generates code for comparison operations (>, <, >=, <=) between floats.
 *
 * i (>|<|<=|>=) j gets translated to (assuming i and j are both on top of the
 * stack):
 *
 * OPCODE // Replaced by the corresponding opcode of the comparison operator
 * CHECKOP // Replaced by the corresponding opcode of the checking operation
 *
 * ICONST_0
 * GOTO end
 * isTrue:
 * ICONST_1
 * end:
 *
 * @param mv The method visitor
 * @param op The operation
 */
private fun floatComparison(mv: MethodVisitor, op: Operator) {
    val start = Label()
    val isTrue = Label()
    val end = Label()
    val (compOp, checkOp) = fromOpToFloatCompBytecodes(op)

    mv.visitLabel(start)
    mv.visitInsn(compOp)
    mv.visitJumpInsn(checkOp, isTrue)
    mv.visitInsn(ICONST_0)
    mv.visitJumpInsn(GOTO, end)
    mv.visitLabel(isTrue)
    mv.visitInsn(ICONST_1)
    mv.visitLabel(end)
}

/**
 * Generates code for comparison (>, <, >=, <=)
 *
 * @param mv The method visitor
 * @param op The operator
 * @param type The operand type
 */
private fun comparison(mv: MethodVisitor, op: Operator, type: Type) {
    when (type) {
        Type.int -> intComparison(mv, op)
        else -> floatComparison(mv, op)
    }
}

/**
 * Generates code for binary operation between expressions
 *
 * @param mv The method visitor
 * @param op The operator
 * @param operandType The type of the operands
 */
fun binaryOp(mv: MethodVisitor, op: Operator, operandType: Type) {
    when (op) {
        Operator.ADD -> add(mv, operandType)
        Operator.SUB -> sub(mv, operandType)
        Operator.MUL -> mul(mv, operandType)
        Operator.DIV -> div(mv, operandType)
        Operator.MOD -> mod(mv, operandType)
        Operator.AND, Operator.OR -> binaryAndOr(mv, op)
        Operator.EQUAL -> equal(mv, operandType)
        Operator.NOT_EQUAL -> notEqual(mv, operandType)
        Operator.GREATER, Operator.LESS, Operator.GREATER_EQUAL, Operator.LESS_EQUAL -> {
            comparison(mv, op, operandType)
        }
        else -> { // Non-binary operators
        }
    }
}
