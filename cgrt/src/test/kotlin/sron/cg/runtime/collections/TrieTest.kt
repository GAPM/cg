package sron.cg.runtime.collections

import org.junit.Test
import kotlin.test.assert
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
        assertTrue(pairs.contains("simon" to 4))
        assertTrue(pairs.contains("simba" to 5))
        assertTrue(pairs.contains("algo" to 6))
        assertTrue(pairs.contains("albumina" to 7))
    }
}