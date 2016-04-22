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

package sron.cgpl.type

import sron.cgpl.compiler.ast.Operator

object OpTable {
    val arithmetic = listOf(Operator.ADD, Operator.SUB, Operator.MUL,
            Operator.DIV, Operator.MOD)

    val comparison = listOf(Operator.EQUAL, Operator.LESS_EQUAL,
            Operator.HIGHER_EQUAL, Operator.NOT_EQUAL, Operator.HIGHER,
            Operator.LESS)

    val sign = listOf(Operator.PLUS, Operator.MINUS)

    val logic = listOf(Operator.AND, Operator.OR)

    private data class BinOp(val ops: List<Operator>, val lhs: Type, val rhs: Type, val result: Type) {

        constructor(ops: List<Operator>, type: Type) : this(ops, type, type, type)

        constructor(ops: List<Operator>, type: Type, ret: Type) : this(ops, type, type, ret)

        fun match(operation: Triple<Operator, Type, Type>): Boolean {
            val (op, lhs, rhs) = operation

            var opMatches = ops.contains(op)
            var typesMatch = (lhs == this.lhs && rhs == this.rhs) || (lhs == this.rhs && rhs == this.lhs)

            return opMatches && typesMatch
        }
    }

    private data class UnaryOp(val ops: List<Operator>, val exp: Type, val result: Type) {

        constructor(ops: List<Operator>, type: Type) : this(ops, type, type)

        fun match(operation: Pair<Operator, Type>): Boolean {
            val (op, exp) = operation
            var result = true

            result = result && ops.contains(op)
            result = result && this.exp == exp

            return result
        }
    }

    private val binOps = listOf(
            BinOp(arithmetic, Type.int),
            BinOp(arithmetic, Type.float),

            BinOp(comparison, Type.int, Type.bool),
            BinOp(comparison, Type.float, Type.bool),

            BinOp(comparison, Type.char, Type.bool),
            BinOp(comparison, Type.string, Type.bool),
            BinOp(comparison, Type.bool),

            BinOp(listOf(Operator.ADD), Type.char, Type.string, Type.string),
            BinOp(listOf(Operator.ADD), Type.char, Type.char, Type.string),

            BinOp(logic, Type.bool)
    )

    private val unaryOps = listOf (
            UnaryOp(sign, Type.int),
            UnaryOp(sign, Type.float),

            UnaryOp(listOf(Operator.NOT), Type.bool)
    )

    fun checkBinaryOp(op: Operator, lhs: Type, rhs: Type): Type {
        val operations = binOps.filter { it.match(Triple(op, lhs, rhs)) }
        val operation = operations.firstOrNull()
        return operation?.result ?: Type.ERROR
    }

    fun checkUnaryOp(op: Operator, x: Type): Type {
        val operation = unaryOps.filter { it.match(Pair(op, x)) }.firstOrNull()
        return operation?.result ?: Type.ERROR
    }
}
