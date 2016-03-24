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
            Type.string lowerOrEqual Type.long
        }

        assertFailsWith<UnsupportedOperationException> {
            Type.long lowerOrEqual Type.float
        }

        assertTrue(Type.byte lowerOrEqual Type.long)
        assertTrue(Type.short lowerOrEqual Type.long)
        assertTrue(Type.int lowerOrEqual Type.long)
        assertTrue(Type.long lowerOrEqual Type.long)

        assertTrue(Type.byte lowerOrEqual Type.int)
        assertTrue(Type.short lowerOrEqual Type.int)
        assertTrue(Type.int lowerOrEqual Type.int)
        assertFalse(Type.long lowerOrEqual Type.int)

        assertTrue(Type.byte lowerOrEqual Type.short)
        assertTrue(Type.short lowerOrEqual Type.short)
        assertFalse(Type.int lowerOrEqual Type.short)
        assertFalse(Type.long lowerOrEqual Type.short)

        assertTrue(Type.byte lowerOrEqual Type.byte)
        assertFalse(Type.short lowerOrEqual Type.byte)
        assertFalse(Type.int lowerOrEqual Type.byte)
        assertFalse(Type.long lowerOrEqual Type.byte)

        assertTrue(Type.float lowerOrEqual Type.double)
        assertTrue(Type.double lowerOrEqual Type.double)

        assertTrue(Type.float lowerOrEqual Type.float)
        assertFalse(Type.double lowerOrEqual Type.float)
    }
}
