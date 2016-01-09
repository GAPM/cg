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

#include "bitarray.h"

#include <limits.h>
#include <math.h>
#include <string.h>

/*
 * i >> 3 == i / 8
 * i & 7  == i % 8
 */

bitarray ba_new(size_t s) {
    size_t bytes = ceil(s / 8.0);

    bitarray n = calloc(1, sizeof(struct bitarray));
    if (n == NULL) {
        return NULL;
    }

    n->size = s;
    n->array = calloc(bytes, 1);
    if (n->array == NULL) {
        free(n);
        return NULL;
    }

    return n;
}

bool ba_get(bitarray ba, size_t i) {
    return ba->array[i >> 3] & (1 << (i & 7));
}

void ba_set(bitarray ba, size_t i, bool v) {
    if (v) {
        ba->array[i >> 3] |= (1 << (i & 7));
    } else {
        ba->array[i >> 3] &= ~(1 << (i & 7));
    }
}

bitarray ba_copy(bitarray ba) {
    bitarray n = ba_new(ba->size);

    if (n == NULL) {
        return NULL;
    }

    memcpy(n->array, ba->array, ceil(ba->size / 8.0));
    return n;
}

size_t ba_size(bitarray ba) {
    if (ba != NULL) {
        return ba->size;
    }
    return 0;
}

void ba_free(bitarray ba) {
    free(ba->array);
    free(ba);
}
