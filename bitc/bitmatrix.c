
#include "bitmatrix.h"

bitmatrix *bm_new(size_t rows, size_t columns) {
    size_t size = rows * columns;

    bitmatrix *n = (bitmatrix*)calloc(1, sizeof(bitmatrix));

    n->rows = rows;
    n->columns = columns;
    n->array = ba_new(size);

    return n;
}

bool bm_get(bitmatrix *bm, size_t r, size_t c) {
    return ba_get(bm->array, r * bm->rows + c);
}

void bm_set(bitmatrix *bm, size_t r, size_t c, bool v) {
    ba_set(bm->array, r * bm->rows + c, v);
}

void bm_free(bitmatrix *bm) {
    ba_free(bm->array);
    free(bm);
}
