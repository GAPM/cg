
#ifndef BITMATRIX_H
#define BITMATRIX_H

#include "bitarray.h"

typedef struct {
    size_t rows;
    size_t columns;
    bitarray *array;
} bitmatrix;

bitmatrix *bm_new(size_t, size_t);
bool bm_get(bitmatrix*, size_t, size_t);
void bm_set(bitmatrix*, size_t, size_t, bool);
bitmatrix *bm_clone(bitmatrix*);
void bm_free(bitmatrix*);

#endif //BITMATRIX_H
