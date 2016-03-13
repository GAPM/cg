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

package sron.grp

import org.junit.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Dummy test created to be executed before any other test.
 *
 * Reason: IntelliJ IDEA seems to take longer in execute the first of all test,
 * so this test is executed first in order to hide false results from other
 * tests.
 */
class AAAAAStart {
    @Test
    fun test() {
        assertTrue(true)
        assertFalse(false)
        assertFailsWith(NotImplementedError::class) {
            throw NotImplementedError("Error")
        }
    }
}
