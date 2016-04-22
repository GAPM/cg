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

class Graph(val nodes: Int, vararg labels: String) {
    val adj: BitMatrix
    val labelsMap = mutableMapOf<Int, String>()
    private var labelled = false

    init {
        adj = BitMatrix(nodes, nodes)

        if (labels.size != 0) {
            if (labels.size != nodes) {
                throw IllegalArgumentException("Not enough labels for $nodes node(s)")
            }

            labelled = true
            var id = 0

            for (label in labels) {
                labelsMap[id++] = label
            }
        }
    }

    fun hasNode(label: String): Boolean {
        if (!labelled) {
            throw UnsupportedOperationException("Can't find label in unlabelled graph")
        }

        val v = labelsMap.values.find { it == label }
        return v != null
    }

    fun hasNode(id: Int): Boolean {
        return id < nodes
    }
}
