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

interface IGraph {
    fun containsVertex(idx: Int): Boolean
    fun containsEdge(source: Int, target: Int): Boolean

    fun addVertex(n: Int): IGraph
    fun addEdge(source: Int, target: Int)

    fun removeEdge(source: Int, target: Int)
    fun removeAllEdges()
}
