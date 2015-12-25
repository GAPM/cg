#include <assert.h>

#include "bitmatrix.h"
#include "grp.h"

#define MAXMATRIX 100

int main() {
    init();
    bitmatrix_t bm = bm_new(MAXMATRIX, MAXMATRIX);

    size_t i;
    size_t j;

    for (i = 0; i < MAXMATRIX; ++i) {
        for (j = 0; j < MAXMATRIX; ++j) {
            if (i == j) {
                bm_set(bm, i, j, true);
            }
        }
    }

    for (i = 0; i < MAXMATRIX; ++i) {
        for (j = 0; j < MAXMATRIX; ++j) {
            if (i == j) {
                assert(bm_get(bm, i, j));
            } else {
                assert(!bm_get(bm, i, j));
            }
        }
    }

    bm_free(bm);
}
