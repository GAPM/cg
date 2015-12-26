#ifndef GRP_GRAPH_H
#define GRP_GRAPH_H

#include <stdbool.h>

#include "bitmatrix.h"
#include "str.h"

typedef struct label_t *label_t;
struct label_t {
    size_t id;
    str_t label;
};

typedef struct edge_t *edge_t;
struct edge_t {
    size_t s; // start node ID
    size_t e; // end node ID
    bool v;   // are they connected?
};

typedef struct graph_t *graph_t;
struct graph_t {
    size_t nlabels;
    label_t *labels;
    bitmatrix_t adj; // Adjacency matrix
};

label_t label_new(size_t, str_t);
void label_free(label_t);

edge_t edge_new(size_t, size_t, bool);
void edge_free(edge_t);

graph_t gr_new(size_t, size_t, size_t, ...);
bool gr_is_connected(graph_t, size_t, size_t);
bool gr_is_connected_l(graph_t, str_t, str_t);
void gr_free(graph_t);

#endif // GRP_GRAPH_H
