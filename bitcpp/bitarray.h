#ifndef BITARRAY_H
#define BITARRAY_H

#include <cmath>

#define MSK 0b10000000

typedef unsigned char byte;

class bitarray {
public:
    bitarray(std::size_t);
    ~bitarray();

    bool get(std::size_t);
    void set(std::size_t, bool);

    std::size_t size();
private:
    std::size_t _size;
    byte *array;
};

#endif
