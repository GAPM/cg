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

import java.util.Iterator;

public class BitMatrix implements Iterable<BitArray> {
    private int rows;
    private int columns;
    private BitArray[] array;

    public BitMatrix(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        array = new BitArray[rows];
        for (int i = 0; i < rows; ++i) {
            array[i] = new BitArray(columns);
        }
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    private void boundCheck(int r, int c) {
        if (r < 0 || r >= rows || c < 0 || c >= columns) {
            throw new IndexOutOfBoundsException();
        }
    }

    public BitArray get(int i) {
        boundCheck(i, 0);
        return array[i];
    }

    public boolean get(int r, int c) {
        boundCheck(r, c);
        return array[r].get(c);
    }

    public void set(int r, int c, boolean v) {
        boundCheck(r, c);
        array[r].set(c, v);
    }

    @Override
    public Iterator<BitArray> iterator() {
        return new BitMatrixIterator(this);
    }

    private class BitMatrixIterator implements Iterator<BitArray> {
        private BitMatrix bitMatrix;
        private int current = 0;

        public BitMatrixIterator(BitMatrix bitMatrix) {
            this.bitMatrix = bitMatrix;
        }

        @Override
        public boolean hasNext() {
            return current != bitMatrix.rows;
        }

        @Override
        public BitArray next() {
            return get(current++);
        }
    }
}
