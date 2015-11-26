#ifndef GRP_BITARRAY_H
#define GRP_BITARRAY_H

#include <stdbool.h>
#include <stdint.h>
#include <stdlib.h>

struct bitarray {
    size_t size;
    uint8_t *array;
};

// ba_new allocates a new bitarray of size s and returns a pointer to it
struct bitarray *ba_new(size_t s);

// ba_get tests wether the bit at index i in bitarray ba is on or off
bool ba_get(struct bitarray *ba, size_t i);

// ba_set sets the bit at index i in bitarray ba to the value v
void ba_set(struct bitarray *ba, size_t i, bool v);

// ba_clone creates an exact copy of bitarray ba and returns a pointer to it
struct bitarray *ba_clone(struct bitarray *ba);

// ba_size returns the number of elements on the bitarray
size_t ba_size(struct bitarray *ba);

// ba_free deallocates the memory occupied by bitarray ba
void ba_free(struct bitarray *ba);

#endif // GRP_BITARRAY_H
