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

package grpc.lib.symbol

object CastTable {
    private val numeric = listOf(
            Type.int8, Type.int16, Type.int32, Type.int64,
            Type.uint8, Type.uint16, Type.uint32, Type.uint64,
            Type.float, Type.double
    )

    private val tab = mapOf(
            Type.int8 to numeric,
            Type.int16 to numeric,
            Type.int32 to numeric,
            Type.int64 to numeric,
            Type.uint8 to numeric,
            Type.uint16 to numeric,
            Type.uint32 to numeric,
            Type.uint64 to numeric,
            Type.float to numeric,
            Type.double to numeric,
            Type.bool to numeric,
            Type.char to listOf(Type.string),
            Type.string to listOf()
    )

    fun check(type1: Type, type2: Type): Boolean =
            tab[type1]?.contains(type2) ?: false
}
