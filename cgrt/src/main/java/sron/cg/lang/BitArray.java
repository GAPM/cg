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

public class BitArray implements Iterable<Boolean> {
    private static int BOX_SIZE = 32;
    private int[] array;
    private int size;

    public BitArray(int size) {
        this.size = size;
        array = new int[(int) Math.ceil(size / (double) BOX_SIZE)];
    }

    public int getSize() {
        return size;
    }

    private void boundCheck(int i) {
        if (i < 0 || i >= size) {
            throw new IndexOutOfBoundsException();
        }
    }

    public boolean get(int i) {
        boundCheck(i);
        return (array[i / BOX_SIZE] & (1 << (i % BOX_SIZE))) != 0;
    }

    public void set(int i, boolean v) {
        boundCheck(i);
        if (v) {
            array[i / BOX_SIZE] |= (1 << (i % BOX_SIZE));
        } else {
            array[i / BOX_SIZE] &= ~(1 << (i % BOX_SIZE));
        }
    }

    @Override
    public Iterator<Boolean> iterator() {
        return new BitArrayIterator();
    }

    private class BitArrayIterator implements Iterator<Boolean> {
        private int current = 0;

        @Override
        public boolean hasNext() {
            return current != size;
        }

        @Override
        public Boolean next() {
            return get(current++);
        }
    }
}
