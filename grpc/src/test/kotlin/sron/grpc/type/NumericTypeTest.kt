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

import org.junit.Test
import sron.grpc.type.Type
import sron.grpc.type.lowerOrEqual
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NumericTypeTest {
    @Test
    fun test() {
        assertFailsWith<UnsupportedOperationException> {
            Type.STRING lowerOrEqual Type.LONG
        }

        assertFailsWith<UnsupportedOperationException> {
            Type.LONG lowerOrEqual Type.FLOAT
        }

        assertTrue(Type.BYTE lowerOrEqual Type.LONG)
        assertTrue(Type.SHORT lowerOrEqual Type.LONG)
        assertTrue(Type.INT lowerOrEqual Type.LONG)
        assertTrue(Type.LONG lowerOrEqual Type.LONG)

        assertTrue(Type.BYTE lowerOrEqual Type.INT)
        assertTrue(Type.SHORT lowerOrEqual Type.INT)
        assertTrue(Type.INT lowerOrEqual Type.INT)
        assertFalse(Type.LONG lowerOrEqual Type.INT)

        assertTrue(Type.BYTE lowerOrEqual Type.SHORT)
        assertTrue(Type.SHORT lowerOrEqual Type.SHORT)
        assertFalse(Type.INT lowerOrEqual Type.SHORT)
        assertFalse(Type.LONG lowerOrEqual Type.SHORT)

        assertTrue(Type.BYTE lowerOrEqual Type.BYTE)
        assertFalse(Type.SHORT lowerOrEqual Type.BYTE)
        assertFalse(Type.INT lowerOrEqual Type.BYTE)
        assertFalse(Type.LONG lowerOrEqual Type.BYTE)

        assertTrue(Type.FLOAT lowerOrEqual Type.DOUBLE)
        assertTrue(Type.DOUBLE lowerOrEqual Type.DOUBLE)

        assertTrue(Type.FLOAT lowerOrEqual Type.FLOAT)
        assertFalse(Type.DOUBLE lowerOrEqual Type.FLOAT)
    }
}
