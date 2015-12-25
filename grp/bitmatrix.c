#include "bitmatrix.h"

bitmatrix_t bm_new(size_t r, size_t c) {
    bitmatrix_t bm = calloc(1, sizeof(struct bitmatrix_t));
    if (bm == NULL) {
        return NULL;
    }

    bm->rows = r;
    bm->columns = c;
    bm->matrix = ba_new(r * c);
    if (bm->matrix == NULL) {
        free(bm);
        return NULL;
    }

    return bm;
}

bool bm_get(bitmatrix_t bm, size_t r, size_t c) {
    return ba_get(bm->matrix, r * bm->rows + c);
}

void bm_set(bitmatrix_t bm, size_t r, size_t c, bool v) {
    ba_set(bm->matrix, r * bm->rows + c, v);
}

bitmatrix_t bm_copy(bitmatrix_t bm) {
    bitmatrix_t n = calloc(1, sizeof(struct bitmatrix_t));
    if (bm == NULL) {
        return NULL;
    }

    n->rows = bm->rows;
    n->columns = bm->columns;
    n->matrix = ba_copy(bm->matrix);
    if (n->matrix == NULL) {
        free(n);
        return NULL;
    }

    return n;
}

void bm_free(bitmatrix_t bm) {
    ba_free(bm->matrix);
    free(bm);
}
