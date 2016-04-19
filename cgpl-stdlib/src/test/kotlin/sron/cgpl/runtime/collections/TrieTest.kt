package sron.cgpl.runtime.collections

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
    }
}
