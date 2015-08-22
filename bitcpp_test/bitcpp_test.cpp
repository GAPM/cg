#include <cassert>
#include "bitmatrix.h"

#define MAXARRAY 25000000
#define MAXMATRIX 5000

int main() {
    bitarray *ba = new bitarray(MAXARRAY);
    bitmatrix *bm = new bitmatrix(MAXMATRIX, MAXMATRIX);

    int i;
    int j;

    for (i = 0; i < MAXARRAY; ++i) {
        if (i % 2 == 0) {
            ba->set(i, true);
        }
    }

    for (i = 0; i < MAXARRAY; ++i) {
        if (i % 2 == 0) {
            assert(ba->get(i) == true);
        } else {
            assert(ba->get(i) == false);
        }
    }

    for (i = 0; i < MAXMATRIX; ++i) {
        for (j = 0; j < MAXMATRIX; ++j) {
            if (i == j) {
                bm->set(i, j, true);
            }
        }
    }

    for (i = 0; i < MAXMATRIX; ++i) {
        for (j = 0; j < MAXMATRIX; ++j) {
            if (i == j) {
                assert(bm->get(i, j) == true);
            } else {
                assert(bm->get(i, j) == false);
            }
        }
    }

    delete ba;
    delete bm;
}
