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
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GraphTest {
    @Test
    fun graphTest() {
        val g = Graph(3)

        assertTrue(g.size == 3)

        for (i in 0 until g.size) {
            for (j in 0 until g.size) {
                assertFalse(g.hasEdge(i, j))
            }
        }
    }

    @Test
    fun graphTestWithEdges() {
        val edges = listOf(
                Edge(0, 1),
                Edge(1, 2),
                Edge(2, 0)
        ).toTypedArray()

        val g = Graph(3, *edges)
        assertTrue(g.size == 3)

        assertTrue(g.hasEdge(0, 1))
        assertTrue(g.hasEdge(1, 0))

        assertTrue(g.hasEdge(1, 2))
        assertTrue(g.hasEdge(2, 1))

        assertTrue(g.hasEdge(2, 0))
        assertTrue(g.hasEdge(0, 2))

        g.removeEdge(0, 2)

        assertFalse(g.hasEdge(2, 0))
        assertFalse(g.hasEdge(0, 2))
    }
}
