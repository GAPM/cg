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
        val g = DiGraph("1", "2", "3", "4", "5")

        assertTrue(g.containsVertex("2"))
        assertFalse(g.containsVertex("6"))

        g.addEdge("1", "4")
        g.addEdge("3", "5")
        g.addEdge("5", "1")

        g.removeEdge("1", "4")

        assertFalse(g.containsEdge("1", "4"))
        assertTrue(g.containsEdge("3", "5"))
        assertFalse(g.containsEdge("5", "3"))
        assertTrue(g.containsEdge("5", "1"))

        g.removeAllEdges()

        assertFalse(g.containsEdge("3", "5"))
        assertFalse(g.containsEdge("5", "1"))
    }
}
