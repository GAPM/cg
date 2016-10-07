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

package sron.cg.lang

import org.junit.Test
import kotlin.test.*

class BitArrayTest {
    @Test
    fun simpleTest() {
        val ba = BitArray(5)

        assertTrue(ba.size == 5)

        for (v in ba) {
            assertFalse(v)
        }

        for (i in 0 until ba.size) {
            ba[i] = i % 2 == 0
        }

        for (i in 0 until ba.size) {
            if (i % 2 == 0) {
                assertTrue(ba[i])
            } else {
                assertFalse(ba[i])
            }
        }

        assertFailsWith<IndexOutOfBoundsException> {
            ba[5]
        }
    }
}