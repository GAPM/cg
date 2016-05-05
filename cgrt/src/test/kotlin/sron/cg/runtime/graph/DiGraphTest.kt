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

package sron.cg.runtime.graph

import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DiGraphTest {
    @Test
    fun test() {
        val g = DiGraph(5)

        assertTrue(g.containsVertex(1))
        assertFalse(g.containsVertex(5))

        g.addEdge(0, 3)
        g.addEdge(2, 4)
        g.addEdge(4, 0)

        g.removeEdge(0, 3)

        assertFalse(g.containsEdge(0, 3))
        assertTrue(g.containsEdge(2, 4))
        assertFalse(g.containsEdge(4, 2))
        assertTrue(g.containsEdge(4, 0))

        g.removeAllEdges()

        assertFalse(g.containsEdge(2, 4))
        assertFalse(g.containsEdge(4, 0))
    }
}
