#include <cassert>
#include <memory>

#include "bitmatrix.h"

#define MAXARRAY 100000000
#define MAXMATRIX 10000

int main() {
    std::unique_ptr<bitarray> ba (new bitarray(MAXARRAY));
    std::unique_ptr<bitmatrix> bm (new bitmatrix(MAXMATRIX, MAXMATRIX));

    int i;
    int j;

    for (i = 0; i < MAXARRAY; ++i) {
        if (i % 2 == 0) {
            ba->set(i, true);
        }
    }

    for (i = 0; i < MAXARRAY; ++i) {
        if (i % 2 == 0) {
            assert(ba->get(i));
        } else {
            assert(!ba->get(i));
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
                assert(bm->get(i, j));
            } else {
                assert(!bm->get(i, j));
            }
        }
    }
}
