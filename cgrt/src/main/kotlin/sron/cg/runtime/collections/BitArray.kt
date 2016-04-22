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

package sron.cg.runtime.collections

class BitArray(val size: Int) {
    private val array = IntArray((size + 32 - 1) / 32)

    operator fun get(idx: Int): Boolean {
        return (array[idx shr 5] and (1 shl (idx and 31))) != 0
    }

    operator fun set(idx: Int, value: Boolean) {
        if (value) {
            array[idx shr 5] = array[idx shr 5] or (1 shl (idx and 31))
        } else {
            array[idx shr 5] = array[idx shr 5] and (1 shl (idx and 31)).inv()
        }
    }

    fun reset() {
        for (i in array.indices) {
            array[i] = 0
        }
    }
}
