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

static const size_t INT_BITS = sizeof(int) * CHAR_BIT;
static const size_t MODULE_MASK = INT_BITS - 1;

bitarray GRP_ba_new(size_t s) {
    size_t real_size = ceil(s / ((double)INT_BITS));

    bitarray n = calloc(1, sizeof(struct bitarray));
    if (n == NULL) {
        return NULL;
    }

    n->size = s;
    n->array = calloc(real_size, sizeof(int));
    if (n->array == NULL) {
        free(n);
        return NULL;
    }

    return n;
}

bool GRP_ba_get(bitarray ba, size_t i) {
    return ba->array[i / INT_BITS] & (1 << (i & MODULE_MASK));
}

void GRP_ba_set(bitarray ba, size_t i, bool v) {
    if (v) {
        ba->array[i / INT_BITS] |= (1 << (i & MODULE_MASK));
    } else {
        ba->array[i / INT_BITS] &= ~(1 << (i & MODULE_MASK));
    }
}

bitarray GRP_ba_copy(bitarray ba) {
    bitarray n = GRP_ba_new(ba->size);

    if (n == NULL) {
        return NULL;
    }

    memcpy(n->array, ba->array, ceil(ba->size / INT_BITS));
    return n;
}

size_t GRP_ba_size(bitarray ba) {
    if (ba != NULL) {
        return ba->size;
    }
    return 0;
}

void GRP_ba_free(bitarray ba) {
    free(ba->array);
    free(ba);
}
