
#ifndef BITARRAY_H
#define BITARRAY_H

#include <math.h>
#include <stdbool.h>
#include <stdlib.h>

#define MSK 0b10000000

typedef struct {
    size_t size;
    unsigned char *array;
} bitarray;

bitarray *ba_new(size_t);
bool ba_get(bitarray*, size_t);
void ba_set(bitarray*, size_t, bool);
void ba_free(bitarray*);

#endif //BITARRAY_H
