#ifndef BITARRAY_H
#define BITARRAY_H

#include <cmath>
#include <memory>

#define __MSK__ 0b10000000

class bitarray {
public:
    bitarray(std::size_t);
    ~bitarray();

    bool get(std::size_t);
    void set(std::size_t, bool);

    std::size_t size();
private:
    std::size_t _size;
    std::unique_ptr<unsigned char[]> array;
};

#endif
