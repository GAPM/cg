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

package sron.cg.compiler.lang

data class UnaryOp(val op: Operator, val arg: Type, val result: Type)
data class BinaryOp(val op: Operator, val args: Pair<Type, Type>, val result: Type) {
    constructor(op: Operator, arg: Type) : this(op, arg to arg, arg)
    constructor(op: Operator, arg: Type, result: Type) : this(op, arg to arg, result)
}

object Operations {
    private val unary = listOf(
            UnaryOp(Operator.NOT, AtomType.bool, AtomType.bool),
            UnaryOp(Operator.ADD, AtomType.int, AtomType.int),
            UnaryOp(Operator.ADD, AtomType.int, AtomType.int),
            UnaryOp(Operator.SUB, AtomType.float, AtomType.float),
            UnaryOp(Operator.SUB, AtomType.float, AtomType.float),
            UnaryOp(Operator.NOT, AtomType.bool, AtomType.bool),
            UnaryOp(Operator.NOT, AtomType.graph, AtomType.graph),
            UnaryOp(Operator.NOT, AtomType.digraph, AtomType.digraph)
    )

    private val binary = mutableListOf<BinaryOp>()

    init {
        for (i in listOf(Operator.ADD, Operator.SUB, Operator.MUL,
                Operator.DIV)) {
            binary += BinaryOp(i, AtomType.int)
            binary += BinaryOp(i, AtomType.float)
        }

        for (i in listOf(Operator.LESS, Operator.LESS_EQUAL, Operator.GREATER,
                Operator.GREATER_EQUAL, Operator.EQUAL, Operator.NOT_EQUAL)) {
            binary += BinaryOp(i, AtomType.int, AtomType.bool)
            binary += BinaryOp(i, AtomType.float, AtomType.bool)
            binary += BinaryOp(i, AtomType.bool)
        }

        binary += BinaryOp(Operator.ADD, AtomType.string)
        binary += BinaryOp(Operator.ADD, AtomType.string to AtomType.char, AtomType.string)
    }

    fun findUnary(op: Operator, arg: Type) =
            unary.filter { it.op == op && it.arg == arg }.firstOrNull()?.result
                    ?: AtomType.ERROR

    fun findBinary(op: Operator, args: Pair<Type, Type>) =
            binary.filter { it.op == op }
                    .filter {
                        val (lhs, rhs) = it.args
                        val (l, r) = args
                        (lhs == l && rhs == r) || (lhs == r && rhs == l)
                    }.firstOrNull()?.result ?: AtomType.ERROR
}
