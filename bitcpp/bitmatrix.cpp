#include "bitmatrix.h"

bitmatrix::bitmatrix(std::size_t r, std::size_t c) {
    this->_rows = r;
    this->_columns = c;
    this->array = new bitarray(r * c);
}

bitmatrix::~bitmatrix() {
    delete this->array;
}

bool bitmatrix::get(std::size_t r, std::size_t c) {
    return this->array->get(r * this->rows() + c);
}

void bitmatrix::set(std::size_t r, std::size_t c, bool v) {
    this->array->set(r * this->rows() + c, v);
}


std::size_t bitmatrix::rows() {
    return this->_rows;
}

std::size_t bitmatrix::columns() {
    return this->_columns;
}
