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
import sron.cg.runtime.collections.Trie

class DiGraph(private vararg val labels: String) : IGraph {
    private val adj: BitMatrix
    private val labelsMap = Trie()

    init {
        var id = 0;
        adj = BitMatrix(labels.size, labels.size)

        for (label in labels) {
            labelsMap[label] = id++
        }
    }

    override fun containsVertex(label: String) = labelsMap.hasKey(label)

    override fun containsEdge(source: String, target: String): Boolean {
        val sourceId = labelsMap[source] ?: -1
        val targetId = labelsMap[target] ?: -1

        if (sourceId != -1 && targetId != -1) {
            return adj[sourceId, targetId]
        }

        return false
    }

    override fun addVertex(label: String): DiGraph {
        val new = DiGraph(*labels, label)

        for (i in 0..this.labels.size) {
            for (j in 0..this.labels.size) {
                if (adj[i, j]) {
                    new.adj[i, j] = true
                }
            }
        }

        return new
    }

    override fun addEdge(source: String, target: String) {
        val sourceId = labelsMap[source] ?: -1
        val targetId = labelsMap[target] ?: -1

        if (sourceId != -1 && targetId != -1) {
            adj[sourceId, targetId] = true
        }
    }

    override fun removeEdge(source: String, target: String) {
        val sourceId = labelsMap[source] ?: -1
        val targetId = labelsMap[target] ?: -1

        if (sourceId != -1 && targetId != -1) {
            adj[sourceId, targetId] = false
        }
    }

    override fun removeAllEdges() = adj.reset()
}
