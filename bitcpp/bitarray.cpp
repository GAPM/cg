#include "bitarray.h"

#include <climits>
#include <cmath>

bitarray::bitarray(std::size_t size) {
  std::size_t bytes = ceil(size / CHAR_BIT);
  this->_size = size;
  this->array = std::unique_ptr<byte_t[]>(new byte_t[bytes]());
}

bitarray::~bitarray() {}

bool bitarray::get(std::size_t i) {
  return this->array[i / CHAR_BIT] & (1 << (i % CHAR_BIT));
}

void bitarray::set(std::size_t i, bool v) {
  if (v) {
    this->array[i / CHAR_BIT] |= (1 << (i % CHAR_BIT));
  } else {
    this->array[i / CHAR_BIT] &= ~(1 << (i % CHAR_BIT));
  }
}

std::size_t bitarray::size() { return this->_size; }
