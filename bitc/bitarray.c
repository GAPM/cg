
#include "bitarray.h"

/*
 * Operaciones con enteros transformadas en operaciones de bits:
 *
 *     n >> 3 == n / 8
 *     n & 7 == n % 8
 */

bitarray *ba_new(size_t size) {
    size_t bytes = ceil(size / 8.0);

    bitarray *n = (bitarray*)calloc(1, sizeof(bitarray));

    n->size = size;
    n->array = (unsigned char*)calloc(bytes, sizeof(unsigned char));

    return n;
}

bool ba_get(bitarray *ba, size_t i) {
    return ba->array[i >> 3] & (MSK >> (i & 7));
}

void ba_set(bitarray *ba, size_t i, bool v) {
    if (v) {
        ba->array[i >> 3] |= (MSK >> (i & 7));
    } else {
        ba->array[i >> 3] &= ~(MSK >> (i & 7));
    }
}

bitarray *ba_clone(bitarray *ba) {
    bitarray *n = ba_new(ba->size);
    memcpy(n->array, ba->array, ceil(ba->size / 8.0));
    return n;
}

void ba_free(bitarray *ba) {
    free(ba->array);
    free(ba);
}
