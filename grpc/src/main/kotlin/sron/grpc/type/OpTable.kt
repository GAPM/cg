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
            BinOp(arithmetic, Type.BYTE),
            BinOp(arithmetic, Type.SHORT),
            BinOp(arithmetic, Type.INT),
            BinOp(arithmetic, Type.LONG),
            BinOp(arithmetic, Type.FLOAT),
            BinOp(arithmetic, Type.DOUBLE),

            BinOp(comparison, Type.BYTE, Type.BOOL),
            BinOp(comparison, Type.SHORT, Type.BOOL),
            BinOp(comparison, Type.INT, Type.BOOL),
            BinOp(comparison, Type.LONG, Type.BOOL),
            BinOp(comparison, Type.FLOAT, Type.BOOL),
            BinOp(comparison, Type.DOUBLE, Type.BOOL),

            BinOp(comparison, Type.CHAR, Type.BOOL),
            BinOp(comparison, Type.STRING, Type.BOOL),
            BinOp(comparison, Type.BOOL),

            BinOp(listOf("+"), Type.CHAR, Type.STRING, Type.STRING),
            BinOp(listOf("+"), Type.CHAR, Type.CHAR, Type.STRING),

            BinOp(logic, Type.BOOL)
    )

    private val unaryOps = listOf (
            UnaryOp(sign, Type.BYTE),
            UnaryOp(sign, Type.SHORT),
            UnaryOp(sign, Type.INT),
            UnaryOp(sign, Type.LONG),
            UnaryOp(sign, Type.FLOAT),
            UnaryOp(sign, Type.DOUBLE),

            UnaryOp(listOf("!"), Type.BOOL)
    )

    fun checkBinary(op: String, x: Type, y: Type): Type {
        var bOps = binOps.filter { it.ops.contains(op) }

        if (x.isIntegral() && y.isIntegral()) {
            bOps = bOps.filter { it.x.isIntegral() }
            bOps = bOps.filter { it.y.isIntegral() }
        }

        if (x.isFP() && y.isFP()) {
            bOps = bOps.filter { it.x.isFP() }
            bOps = bOps.filter { it.y.isFP() }
        }

        bOps = bOps.filter {
            (it.x equivalent x && it.y equivalent y) || (it.y equivalent x && it.x equivalent y)
        }

        return bOps.firstOrNull()?.result ?: Type.ERROR
    }

    fun checkUnary(op: String, x: Type): Type {
        var uOps = unaryOps.filter { it.ops.contains(op) }

        if (x.isIntegral()) {
            uOps = uOps.filter { it.x.isIntegral() }
        }

        if (x.isFP()) {
            uOps = uOps.filter { it.x.isFP() }
        }

        return uOps.firstOrNull()?.result ?: Type.ERROR
    }
}
