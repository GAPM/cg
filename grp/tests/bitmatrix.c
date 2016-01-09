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
#include "bitmatrix.h"

#define MAXMATRIX 100

int main() {
    init();
    bitmatrix bm = bm_new(MAXMATRIX, MAXMATRIX);

    size_t i;
    size_t j;

    for (i = 0; i < MAXMATRIX; ++i) {
        for (j = 0; j < MAXMATRIX; ++j) {
            if (i == j) {
                bm_set(bm, i, j, true);
            }
        }
    }

    for (i = 0; i < MAXMATRIX; ++i) {
        for (j = 0; j < MAXMATRIX; ++j) {
            if (i == j) {
                assert(bm_get(bm, i, j));
            } else {
                assert(!bm_get(bm, i, j));
            }
        }
    }

    bm_free(bm);
}
