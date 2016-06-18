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

package sron.cg.lang.rt;

import sron.cg.lang.DiGraph;
import sron.cg.lang.Graph;

public class RT {
    public static Graph gAddNodes(Graph g, int n) {
        return g.addVertex(n);
    }

    public static DiGraph dgAddNodes(DiGraph g, int n) {
        return g.addVertex(n);
    }

    public static void gAddEdge(Graph g, int a, int b) {
        g.addEdge(a, b);
    }

    public static void dgAddEdge(DiGraph g, int a, int b) {
        g.addEdge(a, b);
    }
}
