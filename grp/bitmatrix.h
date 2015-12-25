#ifndef GRP_BITMATRIX_H
#define GRP_BITMATRIX_H

#include <stdbool.h>
#include <stdlib.h>

#include "bitarray.h"

typedef struct bitmatrix_t *bitmatrix_t;
struct bitmatrix_t {
    size_t rows;
    size_t columns;
    bitarray_t matrix;
};

bitmatrix_t bm_new(size_t, size_t);
bool bm_get(bitmatrix_t, size_t, size_t);
void bm_set(bitmatrix_t, size_t, size_t, bool v);
bitmatrix_t bm_copy(bitmatrix_t);
void bm_free(bitmatrix_t);

#endif // GRP_BITMATRIX_H
