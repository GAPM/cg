#ifndef GRAPH_H
#define GRAPH_H

#include <stdlib.h>
#include "bitarray.h"

struct node {
    size_t id;
    const char *label;
};

struct node_list {
    size_t size;
    struct node **list;
};

struct graph {
    size_t id;             // Current node ID
    struct node *nodes;    // Nodes descriptions
    struct bitarray *adjm; // Adjacency matrix
};

struct node *nd_new(size_t id, const char *label);
void nd_free(struct node *);
struct node_list *ndl_new(size_t n, ...);
void ndl_free(struct node_list *);
struct graph *gr_new(struct node *nodes);

#endif // GRAPH_H
