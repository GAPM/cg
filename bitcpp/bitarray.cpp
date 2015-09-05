#include "bitarray.h"

bitarray::bitarray(std::size_t size) {
    std::size_t bytes = ceil(size / 8.0);
    this->_size = bytes;
    this->array = new unsigned char[bytes]();
}

bitarray::~bitarray() {
    delete[] this->array;
}

bool bitarray::get(std::size_t i) {
    return this->array[i >> 3] & (MSK >> (i & 7));
}

void bitarray::set(std::size_t i, bool v) {
    if (v) {
        this->array[i >> 3] |= (MSK >> (i & 7));
    } else {
        this->array[i >> 3] &= ~(MSK >> (i & 7));
    }
}

std::size_t bitarray::size() {
    return this->_size;
}
