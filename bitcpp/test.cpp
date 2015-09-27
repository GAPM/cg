#include <cassert>
#include <memory>

#include "bitmatrix.h"

#define MAXARRAY 100000000

int main() {
    std::unique_ptr<bitarray> ba (new bitarray(MAXARRAY));

    int i;
    std::size_t size = ba->size();

    for (i = 0; i < size; ++i) {
        if (i % 2 == 0) {
            ba->set(i, true);
        }
    }

    for (i = 0; i < size; ++i) {
        if (i % 2 == 0) {
            assert(ba->get(i));
        } else {
            assert(!ba->get(i));
        }
    }
}
