#include <cassert>
#include <chrono>
#include <iostream>
#include <memory>
#include <vector>

#include "bitarray.h"

#define MAX 100000000

using namespace std;

void testBitArray() {
    unique_ptr<bitarray> ba(new bitarray(MAX));

    auto start = chrono::high_resolution_clock::now();

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

    auto end = chrono::high_resolution_clock::now();
    auto dur = chrono::duration_cast<chrono::milliseconds>(end - start);
    cout << "bitarray: " << dur.count() << " ms" << endl;
}

void testRawArray() {
    unique_ptr<bool[]> ba(new bool[MAX]());

    auto start = chrono::high_resolution_clock::now();

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

    auto end = chrono::high_resolution_clock::now();
    auto dur = chrono::duration_cast<chrono::milliseconds>(end - start);
    cout << "raw array: " << dur.count() << " ms" << endl;
}

void testStdVector() {
    vector<bool> ba(MAX);

    auto start = chrono::high_resolution_clock::now();

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

    auto end = chrono::high_resolution_clock::now();
    auto dur = chrono::duration_cast<chrono::milliseconds>(end - start);
    cout << "std vector: " << dur.count() << " ms" << endl;
}

int main(void) {
    testBitArray();
    testRawArray();
    testStdVector();
}
