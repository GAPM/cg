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

package sron.cg.lang.collections;

import java.util.Arrays;

public class BitArray {
    static int BOX_SIZE = 32;
    private int[] array;
    private int size;

    public BitArray(int size) {
        this.size = size;
        array = new int[(int)Math.ceil(size / (double)BOX_SIZE)];
    }

    static BitArray forMatrix(int size) {
        BitArray ba = new BitArray(0);
        ba.size = size;
        ba.array = new int[size];
        return ba;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BitArray) {
            if (size != ((BitArray) obj).size) {
                return false;
            }

            for (int i = 0; i < array.length; i++) {
                if (array[i] != ((BitArray) obj).array[i]) {
                    return false;
                }
            }

            return true;
        }

        return false;
    }

    public boolean get(int idx) {
        return (array[idx / 32] & (1 << (idx % 32))) != 0;
    }

    public void set(int idx, boolean value) {
        if (value) {
            array[idx / 32] |= (1 << (idx % 32));
        } else {
            array[idx / 32] &= ~(1 << (idx % 32));
        }
    }

    public BitArray copy() {
        BitArray r = new BitArray(0);
        r.size = size;
        r.array = Arrays.copyOf(array, array.length);
        return r;
    }

    public void reset() {
        for (int i = 0; i < array.length; i++) {
            array[i] = 0;
        }
    }

    public int getSize() {
        return size;
    }
}
