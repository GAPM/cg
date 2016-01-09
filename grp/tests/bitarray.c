/*
 * Copyright 2016 Simón Oroño
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

#include <assert.h>

#include "base.h"
#include "bitarray.h"

#define MAXARRAY 10000

int main() {
    init();
    bitarray ba = ba_new(MAXARRAY);

    int i;

    for (i = 0; i < MAXARRAY; ++i) {
        if (i % 2 == 0) {
            ba_set(ba, i, true);
        }
    }

    for (i = 0; i < MAXARRAY; ++i) {
        if (i % 2 == 0) {
            assert(ba_get(ba, i));
        } else {
            assert(!ba_get(ba, i));
        }
    }

    ba_free(ba);
}
