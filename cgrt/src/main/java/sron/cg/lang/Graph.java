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
import java.util.Queue;

public class Graph implements IGraph {
    private int size;
    private BitMatrix adj;

    public Graph(int size) {
        this.size = size;
        adj = new BitMatrix(size, size);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Graph
                && size == ((Graph) obj).size
                && adj.equals(((Graph) obj).adj);
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public boolean containsVertex(int idx) {
        return idx < size;
    }

    @Override
    public boolean containsEdge(int source, int target) {
        return source < size && target < size && adj.get(source, target);
    }

    @Override
    public Graph addVertex(int n) {
        Graph ng = new Graph(size + n);

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (adj.get(i, j)) {
                    ng.adj.set(i, j, true);
                }
            }
        }

        return ng;
    }

    @Override
    public void addEdge(int source, int target) {
        if (source < size && target < size) {
            adj.set(source, target, true);
            adj.set(target, source, true);
        }
    }

    @Override
    public void removeEdge(int source, int target) {
        if (source < size && target < size) {
            adj.set(source, target, false);
            adj.set(target, source, false);
        }
    }

    @Override
    public void removeAllEdges() {
        this.adj.reset();
    }

    @Override
    public Graph negation() {
        Graph graph = new Graph(size);

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (adj.get(i, j)) {
                    graph.adj.set(i, j, false);
                } else {
                    graph.adj.set(i, j, true);
                }
            }
        }

        return graph;
    }

    @Override
    public Graph edgeIntersection(IGraph other) {
        assert other instanceof Graph;
        Graph result = new Graph(size);

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (containsEdge(i, j) && other.containsEdge(i, j)) {
                    result.addEdge(i, j);
                }
            }
        }

        return result;
    }

    @Override
    public Graph edgeUnion(IGraph other) {
        assert other instanceof Graph;
        Graph result = new Graph(size);

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (containsEdge(i, j) || other.containsEdge(i, j)) {
                    result.addEdge(i, j);
                }
            }
        }

        return result;
    }

    @Override
    public Graph edgeDifference(IGraph other) {
        assert other instanceof Graph;
        Graph result = new Graph(size);

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (containsEdge(i, j) && !other.containsEdge(i, j)) {
                    result.addEdge(i, j);
                }
            }
        }

        return result;
    }

    @Override
    public Graph shortestPath(int node) {
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

        Graph result = new Graph(size);
        for (int i = 0; i < size; i++) {
            if (parent[i] != -1) {
                result.addEdge(parent[i], i);
            }
        }
        return result;
    }

    @Override
    public void removeLoops() {
        for (int i = 0; i < size; i++) {
            adj.set(i, i, false);
        }
    }

    @Override
    public String toString() {
        List<String> edges = new ArrayList<>();

        String res = "graph [";
        res += Integer.toString(size);
        res += "] {";

        for (int i = 0; i < size; i++) {
            for (int j = 0; j <= i; j++) {
                if (adj.get(i, j)) {
                    edges.add("[" + j + ", " + i + "]");
                }
            }
        }

        res += String.join(", ", edges);
        res += "}";

        return res;
    }
}
