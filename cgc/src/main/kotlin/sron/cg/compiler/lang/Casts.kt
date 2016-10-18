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

object Casts {
    private val validCasts: Map<Type, List<Type>>

    init {
        val tmp = mutableMapOf<Type, List<Type>>()
        tmp += AtomType.int to listOf(AtomType.float, AtomType.string)
        tmp += AtomType.float to listOf(AtomType.int, AtomType.string)
        tmp += AtomType.char to listOf(AtomType.string)
        tmp += AtomType.string to listOf(ArrayType(AtomType.char))
        validCasts = tmp
    }

    fun isValid(t1: Type, t2: Type): Boolean {
        val target = validCasts[t1]

        if (target != null) {
            return t2 in target
        }

        return false
    }
}
