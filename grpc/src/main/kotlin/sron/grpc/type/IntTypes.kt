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

object IntTypes {
    private val byte = Byte.MIN_VALUE..Byte.MAX_VALUE
    private val short = Short.MIN_VALUE..Short.MAX_VALUE
    private val int = Int.MIN_VALUE..Int.MAX_VALUE
    private val long = Long.MIN_VALUE..Long.MAX_VALUE

    fun checkRange(value: Long, type: Type): Boolean = when (type) {
        Type.byte -> byte.contains(value)
        Type.short -> short.contains(value)
        Type.int -> int.contains(value)
        Type.long -> long.contains(value)
        else -> false
    }

    fun getType(value: Long) = if (byte.contains(value)) {
        Type.byte
    } else if (short.contains(value)) {
        Type.short
    } else if (int.contains(value)) {
        Type.int
    } else {
        Type.long
    }

    fun getConstType(value: Long) = if (int.contains(value)) {
        Type.int
    } else {
        Type.long
    }
}
