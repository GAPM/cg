#include "graph.h"

#include <stdarg.h>

struct graph *gr_new(size_t num_nodes, size_t num_edges, ...) {
    int i;

    struct graph *n = calloc(1, sizeof(struct graph));
    if (n == NULL) {
        return NULL;
    }

    n->adj = bm_new(num_nodes, num_nodes);
    if (n->adj == NULL) {
        free(n);
        return NULL;
    }

    n->labels = calloc(num_nodes, sizeof(struct label));
    if (n->labels == NULL) {
        bm_free(n->adj);
        free(n);
        return NULL;
    }

    va_list args;
    va_start(args, num_edges);

    for (i = 0; i < num_nodes; ++i) {
        n->labels[i] = va_arg(args, struct label);
    }

    for (i = 0; i < num_edges; ++i) {
        struct edge edg = va_arg(args, struct edge);

        bm_set(n->adj, edg.s, edg.e, edg.v);
        bm_set(n->adj, edg.e, edg.s, edg.v);
    }

    va_end(args);

    return n;
}

bool gr_is_connected(struct graph *gr, size_t s, size_t e) {
    return bm_get(gr->adj, s, e);
}
