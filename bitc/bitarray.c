
#include "bitarray.h"

#include <limits.h>
#include <math.h>
#include <string.h>

/*
 * i >> 3 == i / 8
 * i & 7  == i % 8
 */

bitarray *ba_new(size_t s) {
    size_t bytes = ceil(s / CHAR_BIT);

    bitarray *n = (bitarray *)calloc(1, sizeof(bitarray));
    if (n == NULL) {
        return NULL;
    }

    n->size = s;
    n->array = (unsigned char *)calloc(bytes, 1);
    if (n->array == NULL) {
        free(n);
        return NULL;
    }

    return n;
}

bool ba_get(bitarray *ba, size_t i) {
    return ba->array[i / CHAR_BIT] & (1 << (i % CHAR_BIT));
}

void ba_set(bitarray *ba, size_t i, bool v) {
    if (v) {
        ba->array[i / CHAR_BIT] |= (1 << (i % CHAR_BIT));
    } else {
        ba->array[i / CHAR_BIT] &= ~(1 << (i % CHAR_BIT));
    }
}

bitarray *ba_clone(bitarray *ba) {
    bitarray *n = ba_new(ba->size);

    if (n == NULL) {
        return NULL;
    }

    memcpy(n->array, ba->array, ceil(ba->size / 8.0));
    return n;
}

void ba_free(bitarray *ba) {
    free(ba->array);
    free(ba);
}
