#include <assert.h>

#include "bitarray.h"

#define MAXARRAY 10000

int main() {
    struct bitarray *ba = ba_new(MAXARRAY);

    int i;

    for (i = 0; i < MAXARRAY; ++i) {
        if (i % 2 == 0) {
            ba_set(ba, i, true);
        }
    }

    for (i = 0; i < MAXARRAY; ++i) {
        if (i % 2 == 0) {
            assert(ba_get(ba, i));
        } else {
            assert(!ba_get(ba, i));
        }
    }

    ba_free(ba);
}
