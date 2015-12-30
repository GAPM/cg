#ifndef GRP_BITMATRIX_H
#define GRP_BITMATRIX_H

#include <stdbool.h>
#include <stdlib.h>

#include "bitarray.h"

typedef struct bitmatrix *bitmatrix;
struct bitmatrix {
    size_t rows;
    size_t columns;
    bitarray matrix;
};

bitmatrix bm_new(size_t, size_t);
bool bm_get(bitmatrix, size_t, size_t);
void bm_set(bitmatrix, size_t, size_t, bool v);
bitmatrix bm_copy(bitmatrix);
void bm_free(bitmatrix);

#endif // GRP_BITMATRIX_H
