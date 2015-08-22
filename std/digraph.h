
#ifndef DIGRAPH_H
#define DIGRAPH_H

#include <string>
#include <unordered_set>

#include "base.h"
#include "bitmatrix.h"

class digraph {
public:
    digraph(vertex_set, edge_set);
    ~digraph();
private:
    long count;
    bitmatrix *bm;

    vertex_set V;
    edge_set E;
};

#endif //DIGRAPH_H
