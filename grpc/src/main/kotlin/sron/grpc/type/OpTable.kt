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

package sron.grpc.type

object OpTable {
    val arithmetic = listOf("+", "-", "*", "/", "%")
    val comparison = listOf("==", "<=", ">=", "!=", ">", "<")
    val sign = listOf("+", "-")
    val logic = listOf("&&", "||")

    private data class BinOp(val ops: List<String>, val x: Type, val y: Type,
                             val result: Type) {

        constructor(ops: List<String>, t: Type) : this(ops, t, t, t)

        constructor(ops: List<String>, t: Type, r: Type) : this(ops, t, t, r)
    }

    private data class UnaryOp(val ops: List<String>, val x: Type,
                               val result: Type) {

        constructor(ops: List<String>, t: Type) : this(ops, t, t)
    }

    private val binOps = listOf(
            BinOp(arithmetic, Type.int8),
            BinOp(arithmetic, Type.int16),
            BinOp(arithmetic, Type.int32),
            BinOp(arithmetic, Type.int64),
            BinOp(arithmetic, Type.uint8),
            BinOp(arithmetic, Type.uint16),
            BinOp(arithmetic, Type.uint32),
            BinOp(arithmetic, Type.uint64),
            BinOp(arithmetic, Type.float),
            BinOp(arithmetic, Type.double),

            BinOp(comparison, Type.int8, Type.bool),
            BinOp(comparison, Type.int16, Type.bool),
            BinOp(comparison, Type.int32, Type.bool),
            BinOp(comparison, Type.int64, Type.bool),
            BinOp(comparison, Type.uint8, Type.bool),
            BinOp(comparison, Type.uint16, Type.bool),
            BinOp(comparison, Type.uint32, Type.bool),
            BinOp(comparison, Type.uint64, Type.bool),
            BinOp(comparison, Type.float, Type.bool),
            BinOp(comparison, Type.double, Type.bool),

            BinOp(comparison, Type.char, Type.bool),
            BinOp(comparison, Type.string, Type.bool),
            BinOp(comparison, Type.bool),

            BinOp(listOf("+"), Type.char, Type.string, Type.string),
            BinOp(listOf("+"), Type.char, Type.char, Type.string),

            BinOp(logic, Type.bool)
    )

    private val unaryOps = listOf (
            UnaryOp(sign, Type.int8),
            UnaryOp(sign, Type.int16),
            UnaryOp(sign, Type.int32),
            UnaryOp(sign, Type.int64),
            UnaryOp(sign, Type.uint8),
            UnaryOp(sign, Type.uint16),
            UnaryOp(sign, Type.uint32),
            UnaryOp(sign, Type.uint64),
            UnaryOp(sign, Type.float),
            UnaryOp(sign, Type.double),

            UnaryOp(listOf("!"), Type.bool)
    )

    fun checkBinary(op: String, x: Type, y: Type): Type {
        val o = binOps
                .filter { it.ops.contains(op) }
                .filter {
                    (it.x == x && it.y == y) || (it.x == y && it.y == x)
                }.firstOrNull()
        return o?.result ?: Type.error
    }

    fun checkUnary(op: String, x: Type): Type {
        val o = unaryOps
                .filter { it.ops.contains(op) }
                .filter { it.x == x }.firstOrNull()
        return o?.result ?: Type.error
    }
}
