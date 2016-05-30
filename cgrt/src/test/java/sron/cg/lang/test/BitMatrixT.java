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

package sron.cg.lang.test;

import org.junit.Test;
import sron.cg.lang.collections.BitMatrix;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BitMatrixT {
    @Test
    public void test() {
        int max = 100;
        BitMatrix bm = new BitMatrix(max, max);

        assertTrue(bm.getRows() == max);
        assertTrue(bm.getColumns() == max);

        for (int i = 0; i < max; i++) {
            for (int j = 0; j < max; j++) {
                if (i > j) {
                    bm.set(i, j, true);
                } else {
                    bm.set(i, j, false);
                }
            }
        }

        for (int i = 0; i < max; i++) {
            for (int j = 0; j < max; j++) {
                if (i > j) {
                    assertTrue(bm.get(i, j));
                } else {
                    assertFalse(bm.get(i, j));
                }
            }
        }
    }

    @Test
    public void reset() {
        BitMatrix bm1 = new BitMatrix(3, 3);
        BitMatrix bm2 = new BitMatrix(3, 3);

        bm1.set(1, 1, true);
        bm2.set(1, 1, true);

        assertTrue(bm1.equals(bm2));

        bm2.reset();

        assertFalse(bm1.equals(bm2));
    }
}
