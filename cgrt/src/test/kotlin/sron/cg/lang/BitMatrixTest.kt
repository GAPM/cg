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

class BitMatrixTest {
    @Test
    fun simpleTest() {
        val bm = BitMatrix(5, 5)

        assertTrue(bm.rows == 5)
        assertTrue(bm.columns == 5)

        /* Every value must be false when starting */
        for (ba in bm) {
            for (v in ba) {
                assertFalse(v)
            }
        }

        for (i in 0 until bm.rows) {
            for (j in 0 until bm.columns) {
                if (i == j) {
                    bm[i, j] = true
                }
            }
        }

        for (i in 0 until bm.rows) {
            for (j in 0 until bm.columns) {
                if (i == j) {
                    assertTrue(bm[i, j])
                } else {
                    assertFalse(bm[i, j])
                }
            }
        }

        assertFailsWith<IndexOutOfBoundsException> {
            bm[6]
        }
    }
}