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

import org.junit.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TrieTest {
    @Test
    fun test() {
        val trie = Trie()

        trie["simon"] = 4;
        trie["simba"] = 5;
        trie["algo"] = 6;
        trie["albumina"] = 7;

        assertTrue(trie["simon"] == 4)
        assertTrue(trie["simba"] == 5)
        assertTrue(trie["algo"] == 6)
        assertTrue(trie["albumina"] == 7)

        assertFailsWith<IllegalArgumentException> {
            trie[""] = 34
        }

        assertTrue(trie.hasKey("simon"))
        assertTrue(trie.hasKey("albumina"))
        assertFalse(trie.hasKey("house"))
        assertFalse(trie.hasKey("love"))

        val keys = trie.keys()
        assertTrue(keys.contains("simon"))
        assertTrue(keys.contains("simba"))
        assertTrue(keys.contains("algo"))
        assertTrue(keys.contains("albumina"))

        val values = trie.values()
        assertTrue(values.contains(4))
        assertTrue(values.contains(5))
        assertTrue(values.contains(6))
        assertTrue(values.contains(7))

        val pairs = trie.pairs()
        for ((key, value) in pairs) {
            assertTrue(trie[key] == value)
        }
    }
}
