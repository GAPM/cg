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

import sron.cg.runtime.collections.BitMatrix

class DiGraph(val size: Int) : IGraph {
    private val adj = BitMatrix(size, size)

    override fun containsVertex(idx: Int) = idx < size

    override fun containsEdge(source: Int, target: Int): Boolean {
        if (source < size && target < size) {
            return adj[source, target]
        }
        return false
    }

    override fun addVertex(n: Int): DiGraph {
        val new = DiGraph(size + n)

        for (i in 0..this.size) {
            for (j in 0..this.size) {
                if (adj[i, j]) {
                    new.adj[i, j] = true
                }
            }
        }

        return new
    }

    override fun addEdge(source: Int, target: Int) {
        if (source < size && target < size) {
            adj[source, target] = true
        }
    }

    override fun removeEdge(source: Int, target: Int) {
        if (source < size && target < size) {
            adj[source, target] = false
        }
    }

    override fun removeAllEdges() = adj.reset()
}
