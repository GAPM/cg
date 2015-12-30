#include "graph.h"

#include <stdarg.h>
#include <stdlib.h>
#include <string.h>

label label_new(size_t id, str l) {
    label n = calloc(1, sizeof(struct label));

    n->id = id;
    n->label = l;

    return n;
}

void label_free(label l) {
    str_free(l->label);
    free(l);
}

edge_t edge_new(size_t startID, size_t endID, bool v) {
    edge_t n = calloc(1, sizeof(struct edge_t));

    n->s = startID;
    n->e = endID;
    n->v = v;

    return n;
}

void edge_free(edge_t e) { free(e); }

graph_t gr_new(size_t num_nodes, size_t nlabels, size_t num_edges, ...) {
    int i;

    graph_t n = calloc(1, sizeof(struct graph_t));
    if (n == NULL) {
        return NULL;
    }

    n->nlabels = nlabels;

    n->adj = bm_new(num_nodes, num_nodes);
    if (n->adj == NULL) {
        free(n);
        return NULL;
    }

    n->labels = calloc(num_nodes, sizeof(label));
    if (n->labels == NULL) {
        bm_free(n->adj);
        free(n);
        return NULL;
    }

    va_list args;
    va_start(args, num_edges);

    for (i = 0; i < nlabels; ++i) {
        n->labels[i] = va_arg(args, label);
    }

    for (i = 0; i < num_edges; ++i) {
        edge_t e = va_arg(args, edge_t);

        bm_set(n->adj, e->s, e->e, e->v);
        bm_set(n->adj, e->e, e->s, e->v);

        edge_free(e);
    }

    va_end(args);

    return n;
}

bool gr_is_connected(graph_t gr, size_t s, size_t e) {
    return bm_get(gr->adj, s, e);
}

bool gr_is_connected_l(graph_t gr, str s, str e) {
    size_t i;
    size_t ls = -1;
    size_t le = -1;

    for (i = 0; i < gr->nlabels; ++i) {
        if (str_eq(s, gr->labels[i]->label)) {
            ls = gr->labels[i]->id;
        }
    }
    if (ls == -1) {
        return false;
    }

    for (i = 0; i < gr->nlabels; ++i) {
        if (str_eq(e, gr->labels[i]->label)) {
            le = gr->labels[i]->id;
        }
    }
    if (le == -1) {
        return false;
    }

    str_free(s);
    str_free(e);

    return gr_is_connected(gr, ls, le);
}

void gr_free(graph_t gr) {
    size_t i;
    for (i = 0; i < gr->nlabels; ++i) {
        label_free(gr->labels[i]);
    }

    free(gr->labels);
    bm_free(gr->adj);
    free(gr);
}
