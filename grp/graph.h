#ifndef GRP_GRAPH_H
#define GRP_GRAPH_H

#include <stdbool.h>

#include "bitmatrix.h"

struct label {
    size_t id;
    const char *label; //TODO this must be a wchar_t* or struct str*
};

struct edge {
    size_t s; // start node ID
    size_t e; // end node ID
    bool v;   // are they connected?
};

struct graph {
    size_t nlabels;
    struct label *labels;
    struct bitmatrix *adj; // Adjacency matrix
};

struct graph *gr_new(size_t, size_t, size_t, ...);
bool gr_is_connected(struct graph *, size_t, size_t);
bool gr_is_connected_l(struct graph *, char *, char *);
void gr_free(struct graph *);

#endif // GRP_GRAPH_H
