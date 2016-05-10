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

package sron.cg.runtime.graph;

import sron.cg.runtime.collections.BitMatrix;

public class DiGraph implements IGraph {
    private int size;
    private BitMatrix adj;

    public DiGraph(int size) {
        this.size = size;
        adj = new BitMatrix(size, size);
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

    @Override
    public void addEdge(int source, int target) {
        if (source < size && target < size) {
            adj.set(source, target, true);
        }
    }

    @Override
    public void removeEdge(int source, int target) {
        if (source < size && target < size) {
            adj.set(source, target, false);
        }
    }

    @Override
    public void removeAllEdges() {
        this.adj.reset();
    }
}