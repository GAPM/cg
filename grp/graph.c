#include "graph.h"

#include <stdarg.h>
#include <stdlib.h>
#include <string.h>

struct graph *gr_new(size_t num_nodes, size_t nlabels, size_t num_edges, ...) {
    int i;

    struct graph *n = calloc(1, sizeof(struct graph));
    if (n == NULL) {
        return NULL;
    }

    n->nlabels = nlabels;

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

    for (i = 0; i < nlabels; ++i) {
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

bool gr_is_connected_l(struct graph *gr, char *s, char *e) {
    size_t i;
    size_t ls = -1;
    size_t le = -1;

    for (i = 0; i < gr->nlabels; ++i) {
        if (strcmp(s, gr->labels[i].label) == 0) {
            ls = gr->labels[i].id;
        }
    }
    if (ls == -1) {
        return false;
    }

    for (i = 0; i < gr->nlabels; ++i) {
        if (strcmp(e, gr->labels[i].label) == 0) {
            le = gr->labels[i].id;
        }
    }
    if (le == -1) {
        return false;
    }

    return gr_is_connected(gr, ls, le);
}

void gr_free(struct graph *gr) {
    free(gr->labels);
    bm_free(gr->adj);
    free(gr);
}
