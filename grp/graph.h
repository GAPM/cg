#ifndef GRP_GRAPH_H
#define GRP_GRAPH_H

#include <stdbool.h>
#include <stdlib.h>

#include "bitmatrix.h"

struct label {
    size_t id;
    const char *label;
};

struct edge {
    size_t s;
    size_t e;
    bool v;
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
