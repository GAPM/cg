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

abstract class AGraph {
    private final int size;
    final BitMatrix adj;

    AGraph(int size) {
        this.size = size;
        this.adj = new BitMatrix(size, size);
    }

    public int getSize() {
        return size;
    }

    public boolean hasNode(int i) {
        return i >= 0 || i < size;
    }

    public boolean hasEdge(int source, int target) {
        return hasNode(source) && hasNode(target) && adj.get(source, target);
    }

    public abstract void addEdge(int source, int target);

    public abstract void removeEdge(int source, int target);
}
