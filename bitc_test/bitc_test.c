#include <assert.h>

#include "bitmatrix.h"

#define MAXARRAY 25000000
#define MAXMATRIX 5000

int main() {
    bitarray *ba = ba_new(MAXARRAY);
    bitmatrix *bm = bm_new(MAXMATRIX, MAXMATRIX);

    int i;
    int j;

    for (i = 0; i < MAXARRAY; ++i) {
        if (i % 2 == 0) {
            ba_set(ba, i, true);
        }
    }

    for (i = 0; i < MAXARRAY; ++i) {
        if (i % 2 == 0) {
            assert(ba_get(ba, i) == true);
        } else {
            assert(ba_get(ba, i) == false);
        }
    }

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
                assert(bm_get(bm, i, j) == true);
            } else {
                assert(bm_get(bm, i, j) == false);
            }
        }
    }

    ba_free(ba);
    bm_free(bm);
}
