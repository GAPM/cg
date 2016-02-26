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

package sron.grp.collections

import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BitArrayTest {
    @Test
    fun test() {
        val ba = BitArray(100);
        val s = ba.size

        assertTrue(s == 100)

        for (i in 0..s - 1) {
            if (i >= 50) {
                ba[i] = true
            }
        }

        for (i in 0..s - 1) {
            if (i >= 50) {
                assertTrue(ba[i])
            } else {
                assertFalse(ba[i])
            }
        }

        assertFalse(ba[101])
    }
}
