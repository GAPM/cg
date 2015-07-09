#include <assert.h>

#include "bitmatrix.h"

#define MAX 25000000

int main() {
    bitarray *ba = ba_new(MAX);

    int i;
    for (i = 0; i < MAX; ++i) {
        if (i % 2 == 0) {
            ba_set(ba, i, true);
        }
    }

    for (i = 0; i < MAX; ++i) {
        if (i % 2 == 0) {
            assert(ba_get(ba, i) == true);
        } else {
            assert(ba_get(ba, i) == false);
        }
    }

    ba_free(ba);
}
