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

package sron.cg.lang;

import sron.cg.lang.collections.BitArray;
import sron.cg.lang.collections.BitMatrix;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DiGraph {
    private int size;
    private BitMatrix adj;

    public DiGraph(int size) {
        this.size = size;
        adj = new BitMatrix(size, size);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof DiGraph
                && size == ((DiGraph) obj).size
                && adj.equals(((DiGraph) obj).adj);
    }

    public int getSize() {
        return size;
    }

    public boolean containsNode(int idx) {
        return idx >= 0 && idx < size;
    }

    public boolean containsEdge(int source, int target) {
        return source >= 0 && source < size &&
                target >= 0 && target < size &&
                adj.get(source, target);
    }

    public DiGraph addVertex(int n) {
        DiGraph ng = new DiGraph(size + n);

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (adj.get(i, j)) {
                    ng.adj.set(i, j, true);
                }
            }
        }

        return ng;
    }

    public void addEdge(int source, int target) {
        adj.set(source, target, true);
    }

    public void removeEdge(int source, int target) {
        adj.set(source, target, false);
    }

    public void removeAllEdges() {
        this.adj.reset();
    }

    public DiGraph negation() {
        DiGraph diGraph = new DiGraph(size);

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (adj.get(i, j)) {
                    diGraph.adj.set(i, j, false);
                } else {
                    diGraph.adj.set(i, j, true);
                }
            }
        }

        return diGraph;
    }

    public DiGraph edgeIntersection(DiGraph other) {
        DiGraph result = new DiGraph(size);

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (containsEdge(i, j) && other.containsEdge(i, j)) {
                    result.addEdge(i, j);
                }
            }
        }

        return result;
    }

    public DiGraph edgeUnion(DiGraph other) {
        DiGraph result = new DiGraph(size);

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (containsEdge(i, j) || other.containsEdge(i, j)) {
                    result.addEdge(i, j);
                }
            }
        }

        return result;
    }

    public DiGraph edgeDifference(DiGraph other) {
        DiGraph result = new DiGraph(size);

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (containsEdge(i, j) && !other.containsEdge(i, j)) {
                    result.addEdge(i, j);
                }
            }
        }

        return result;
    }

    public DiGraph shortestPath(int node) {
        BitArray visited = new BitArray(size);
        LinkedList<Integer> queue = new LinkedList<>();

        int parent[] = new int[size];
        for (int i = 0; i < size; i++) {
            parent[i] = -1;
        }

        queue.addLast(node);
        visited.set(node, true);

        while (!queue.isEmpty()) {
            int current = queue.removeFirst();

            for (int i = 0; i < size; i++) {
                if (!visited.get(i) && adj.get(current, i)) {
                    parent[i] = current;
                    queue.addLast(i);
                    visited.set(i, true);
                }
            }
        }

        DiGraph result = new DiGraph(size);
        for (int i = 0; i < size; i++) {
            if (parent[i] != -1) {
                result.addEdge(parent[i], i);
            }
        }
        return result;
    }

    public void removeLoops() {
        for (int i = 0; i < size; i++) {
            adj.set(i, i, false);
        }
    }

    @Override
    public String toString() {
        List<String> edges = new ArrayList<>();

        String res = "digraph [";
        res += Integer.toString(size);
        res += "] {";

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (containsEdge(i, j)) {
                    edges.add("[" + i + ", " + j + "]");
                }
            }
        }

        res += String.join(", ", edges);
        res += "}";

        return res;
    }
}
