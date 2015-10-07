#ifndef BITARRAY_H
#define BITARRAY_H

#include <memory>

typedef unsigned char byte_t;

class bitarray {
public:
  bitarray(std::size_t);
  ~bitarray();

  bool get(std::size_t);
  void set(std::size_t, bool);

  std::size_t size();

private:
  std::size_t _size;
  std::unique_ptr<byte_t[]> array;
};

#endif
