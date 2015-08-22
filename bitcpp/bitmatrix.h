#ifndef BITMATRIX_H
#define BITMATRIX_H

#include "bitarray.h"

class bitmatrix {
public:
    bitmatrix(std::size_t, std::size_t);
    ~bitmatrix();

    bool get(std::size_t, std::size_t);
    void set(std::size_t, std::size_t, bool);

    std::size_t rows();
    std::size_t columns();
private:
    std::size_t _rows;
    std::size_t _columns;
    bitarray *array;
};

#endif
