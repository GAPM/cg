#include "bitmatrix.h"

struct bitmatrix *bm_new(size_t r, size_t c) {
    struct bitmatrix *bm = calloc(1, sizeof(struct bitmatrix));
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

bool bm_get(struct bitmatrix *bm, size_t r, size_t c) {
    return ba_get(bm->matrix, r * bm->rows + c);
}

void bm_set(struct bitmatrix *bm, size_t r, size_t c, bool v) {
    ba_set(bm->matrix, r * bm->rows + c, v);
}

void bm_free(struct bitmatrix *bm) {
    ba_free(bm->matrix);
    free(bm);
}
