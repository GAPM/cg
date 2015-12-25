#ifndef GRP_BITARRAY_H
#define GRP_BITARRAY_H

#include <stdbool.h>
#include <stdint.h>
#include <stdlib.h>

typedef struct bitarray_t *bitarray_t;
struct bitarray_t {
    size_t size;
    uint8_t *array;
};

// ba_new allocates a new bitarray of size s and returns a pointer to it
bitarray_t ba_new(size_t s);

// ba_get tests wether the bit at index i in bitarray ba is on or off
bool ba_get(bitarray_t ba, size_t i);

// ba_set sets the bit at index i in bitarray ba to the value v
void ba_set(bitarray_t ba, size_t i, bool v);

// ba_copy creates an exact copy of bitarray ba and returns a pointer to it
bitarray_t ba_copy(bitarray_t ba);

// ba_size returns the number of elements on the bitarray
size_t ba_size(bitarray_t ba);

// ba_free deallocates the memory occupied by bitarray ba
void ba_free(bitarray_t ba);

#endif // GRP_BITARRAY_H
