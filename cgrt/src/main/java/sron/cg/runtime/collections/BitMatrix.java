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

package sron.cg.runtime.collections;

public class BitMatrix {
    private int rows;
    private int columns;
    private BitArray array;

    public BitMatrix(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        array = new BitArray(rows * columns);
    }

    public boolean get(int r, int c) {
        return array.get(r * rows + c);
    }

    public void set(int r, int c, boolean value) {
        array.set(r * rows + c, value);
    }

    public void reset() {
        array.reset();
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }
}
