#include "bitarray.h"

#include <limits.h>
#include <math.h>
#include <string.h>

/*
 * i >> 3 == i / 8
 * i & 7  == i % 8
 */

bitarray_t ba_new(size_t s) {
    size_t bytes = ceil(s / 8.0);

    bitarray_t n = calloc(1, sizeof(struct bitarray_t));
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

bool ba_get(bitarray_t ba, size_t i) {
    return ba->array[i >> 3] & (1 << (i & 7));
}

void ba_set(bitarray_t ba, size_t i, bool v) {
    if (v) {
        ba->array[i >> 3] |= (1 << (i & 7));
    } else {
        ba->array[i >> 3] &= ~(1 << (i & 7));
    }
}

bitarray_t ba_copy(bitarray_t ba) {
    bitarray_t n = ba_new(ba->size);

    if (n == NULL) {
        return NULL;
    }

    memcpy(n->array, ba->array, ceil(ba->size / 8.0));
    return n;
}

size_t ba_size(bitarray_t ba) {
    if (ba != NULL) {
        return ba->size;
    }
    return 0;
}

void ba_free(bitarray_t ba) {
    free(ba->array);
    free(ba);
}
