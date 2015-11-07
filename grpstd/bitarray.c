
#include "bitarray.h"

#include <limits.h>
#include <math.h>
#include <string.h>

/*
 * i >> 3 == i / 8
 * i & 7  == i % 8
 */

struct bitarray *ba_new(size_t s) {
    size_t bytes = ceil(s / CHAR_BIT);

    struct bitarray *n = calloc(1, sizeof(struct bitarray));
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

bool ba_get(struct bitarray *ba, size_t i) {
    return ba->array[i / CHAR_BIT] & (1 << (i % CHAR_BIT));
}

void ba_set(struct bitarray *ba, size_t i, bool v) {
    if (v) {
        ba->array[i / CHAR_BIT] |= (1 << (i % CHAR_BIT));
    } else {
        ba->array[i / CHAR_BIT] &= ~(1 << (i % CHAR_BIT));
    }
}

struct bitarray *ba_clone(struct bitarray *ba) {
    struct bitarray *n = ba_new(ba->size);

    if (n == NULL) {
        return NULL;
    }

    memcpy(n->array, ba->array, ceil(ba->size / 8.0));
    return n;
}

size_t ba_size(struct bitarray *ba) {
    if (ba != NULL) {
        return ba->size;
    }
    return 0;
}

void ba_free(struct bitarray *ba) {
    free(ba->array);
    free(ba);
}
