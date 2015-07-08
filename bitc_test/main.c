#include <assert.h>

#include "bitmatrix.h"

int main() {
    bitarray *ba = ba_new(10);

    int i;
    for (i = 0; i < 10; ++i) {
        if (i % 2 == 0) {
            ba_set(ba, i, true);
        }
    }

    for (i = 0; i < 10; ++i) {
        if (i % 2 == 0) {
            assert(ba_get(ba, i) == true);
        } else {
            assert(ba_get(ba, i) == false);
        }
    }

    bitmatrix *bm = bm_new(4, 4);

    int j;
    for (i = 0; i < 4; ++i) {
        for (j = 0; j < 4; ++j) {
            if (i == j) {
                bm_set(bm, i, j, true);
            }
        }
    }

    for (i = 0; i < 4; ++i) {
        for (j = 0; j < 4; ++j) {
            if (i == j) {
                assert(bm_get(bm, i, j) == true);
            } else {
                assert(bm_get(bm, i, j) == false);
            }
        }
    }

    ba_free(ba);
    bm_free(bm);
}
