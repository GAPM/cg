package sron.cg.lang

import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class EdgeTest {
    @Test
    fun edgeTest() {
        val e1 = Edge(0, 1)
        val e2 = Edge(1, 3)
        val e3 = Edge(0, 1)

        assertTrue(e1.source == 0)
        assertTrue(e2.source == 1)
        assertTrue(e3.source == 0)

        assertTrue(e1.target == 1)
        assertTrue(e2.target == 3)
        assertTrue(e3.target == 1)

        assertTrue(e1 == e1)
        assertFalse(e1 == e2)
        assertTrue(e1 == e3)

        assertTrue(e1.hashCode() == e1.hashCode())
        assertFalse(e1.hashCode() == e2.hashCode())
        assertTrue(e1.hashCode() == e3.hashCode())
    }
}
