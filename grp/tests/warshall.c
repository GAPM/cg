#include "bitmatrix.h"

#define MAX 100

int main(void) {
    bitmatrix_t bm = bm_new(MAX, MAX);
    bitmatrix_t result = bm_new(MAX, MAX);

    size_t i, j, k;

    for (k = 0; k < MAX; ++k) {
        for (i = 0; i < MAX; ++i) {
            for (j = 0; j < MAX; ++j) {
                bm_set(result, i, j, bm_get(bm, i, k) && bm_get(bm, k, j));
            }
        }
    }

    bm_free(bm);
    bm_free(result);
}
