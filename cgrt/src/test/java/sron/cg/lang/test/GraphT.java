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

package sron.cg.lang.test;

import org.junit.Test;
import sron.cg.lang.Graph;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GraphT {
    @Test
    public void test() {
        Graph g = new Graph(5);

        assertTrue(g.containsVertex(1));
        assertFalse(g.containsVertex(5));

        g.addEdge(0, 3);
        g.addEdge(2, 4);
        g.addEdge(4, 0);

        g.removeEdge(0, 3);

        assertFalse(g.containsEdge(0, 3));
        assertTrue(g.containsEdge(2, 4));
        assertTrue(g.containsEdge(4, 2));
        assertTrue(g.containsEdge(4, 0));

        g.addEdge(0, 1);
        g.addEdge(1, 2);
        g.addEdge(2, 0);

        g = g.addVertex(2);

        assertTrue(g.getSize() == 7);
    }

    @Test
    public void equal() {
        Graph g1 = new Graph(3);
        Graph g2 = new Graph(3);

        g1.addEdge(1, 2);
        g2.addEdge(1, 2);

        assertTrue(g1.equals(g2));

        g2.addEdge(0, 1);

        assertFalse(g1.equals(g2));
    }

    @Test
    public void negation() {
        Graph g = new Graph(2);
        g = g.negation();

        assertTrue(g.containsEdge(0, 1));
        assertTrue(g.containsEdge(1, 0));
    }

    @Test
    public void string() {
        Graph g = new Graph(0);
        assertTrue(g.toString().equals("graph (0) {}"));

        g = g.addVertex(2);
        g.addEdge(0, 1);
        assertTrue(g.toString().equals("graph (2) {[1, 0]}"));
    }
}
