/*
 * Copyright 2016 Simón Oroño
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

package sron.grpc.symbol

object OpTable {
    private val numeric = listOf(
            Type.int8, Type.int16, Type.int32, Type.int64,
            Type.uint8, Type.uint16, Type.uint32, Type.uint64,
            Type.float, Type.double
    )

    private val binaryTab = mapOf(
            "+" to (numeric + Type.string + Type.char),
            "-" to numeric,
            "*" to numeric,
            "/" to numeric
    )

    private val unaryTab = mapOf(
            "!" to listOf(Type.bool),
            "-" to numeric,
            "+" to numeric
    )

    fun checkBinary(op: String, type: Type) = binaryTab[op]?.contains(type) ?: false
    fun checkUnary(op: String, type: Type) = unaryTab[op]?.contains(type) ?: false
}
