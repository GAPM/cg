#include <cassert>
#include <chrono>
#include <iostream>
#include <memory>
#include <string>
#include <vector>

#include "bitarray.h"

#define MAX 100000000

using namespace std;

void measure(void (*f)(), string name) {
    auto start = chrono::high_resolution_clock::now();

    f();

    auto end = chrono::high_resolution_clock::now();
    auto dur = chrono::duration_cast<chrono::milliseconds>(end - start);
    cout << name << ": " << dur.count() << " ms" << endl;
}

inline void testBitArray() {
    unique_ptr<bitarray> ba(new bitarray(MAX));

    for (size_t i = 0; i < ba->size(); ++i) {
        if (i % 2 == 0) {
            ba->set(i, true);
        }
    }

    for (size_t i = 0; i < ba->size(); ++i) {
        if (i % 2 == 0) {
            assert(ba->get(i));
        } else {
            assert(!ba->get(i));
        }
    }
}

inline void testRawArray() {
    unique_ptr<bool[]> ba(new bool[MAX]());

    for (size_t i = 0; i < MAX; ++i) {
        if (i % 2 == 0) {
            ba[i] = true;
        }
    }

    for (size_t i = 0; i < MAX; ++i) {
        if (i % 2 == 0) {
            assert(ba[i]);
        } else {
            assert(!ba[i]);
        }
    }
}

inline void testStdVector() {
    vector<bool> ba(MAX);

    for (size_t i = 0; i < ba.size(); ++i) {
        if (i % 2 == 0) {
            ba[i] = true;
        }
    }

    for (size_t i = 0; i < MAX; ++i) {
        if (i % 2 == 0) {
            assert(ba[i]);
        } else {
            assert(!ba[i]);
        }
    }
}

int main(void) {
    measure(testBitArray, "bitarray");
    measure(testRawArray, "raw array");
    measure(testStdVector, "std vector");
}
