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
import sron.cg.lang.collections.BitArray;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BitArrayT {
    @Test
    public void test() {
        int max = 100;
        BitArray ba = new BitArray(max);

        assertTrue(ba.getSize() == max);

        for (int i = 0; i < max; i++) {
            if (i % 2 == 0) {
                ba.set(i, true);
            } else {
                ba.set(i, false);
            }
        }

        for (int i = 0; i < max; i++) {
            if (i % 2 == 0) {
                assertTrue(ba.get(i));
            } else {
                assertFalse(ba.get(i));
            }
        }

        //Test the excess
        for (int i = 100; i < 128; i++) {
            assertFalse(ba.get(i));
        }

        try {
            ba.get(128);
        } catch (Exception e) {
            assertTrue(e instanceof IndexOutOfBoundsException);
        }
    }

    @Test
    public void equality() {
        BitArray ba1 = new BitArray(10);
        BitArray ba2 = new BitArray(9);
        BitArray ba3 = new BitArray(10);

        ba1.set(3, true);
        ba2.set(3, true);
        ba3.set(3, true);

        assertFalse(ba1.equals(ba2));
        assertTrue(ba1.equals(ba3));

        ba3.reset();

        assertFalse(ba1.equals(ba3));
    }
}
