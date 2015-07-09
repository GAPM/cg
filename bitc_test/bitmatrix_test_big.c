#include <assert.h>

#include "bitmatrix.h"

#define MAX 5000

int main() {
    bitmatrix *bm = bm_new(MAX, MAX);

    int i;
    int j;
    for (i = 0; i < MAX; ++i) {
        for (j = 0; j < MAX; ++j) {
            if (i == j) {
                bm_set(bm, i, j, true);
            }
        }
    }

    for (i = 0; i < MAX; ++i) {
        for (j = 0; j < MAX; ++j) {
            if (i == j) {
                assert(bm_get(bm, i, j) == true);
            } else {
                assert(bm_get(bm, i, j) == false);
            }
        }
    }

    bm_free(bm);
}
