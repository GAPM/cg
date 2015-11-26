#ifndef GRP_BITMATRIX_H
#define GRP_BITMATRIX_H

#include <stdbool.h>
#include <stdlib.h>

#include "bitarray.h"

struct bitmatrix {
    size_t rows;
    size_t columns;
    struct bitarray *matrix;
};

struct bitmatrix *bm_new(size_t, size_t);
bool bm_get(struct bitmatrix *, size_t, size_t);
void bm_set(struct bitmatrix *, size_t, size_t, bool v);
void bm_free(struct bitmatrix *);

#endif // GRP_BITMATRIX_H
