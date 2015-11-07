#include "graph.h"

#include <stdarg.h>

struct node *nd_new(size_t id, const char *label) {
    struct node *n = calloc(1, sizeof(struct node));
    n->id = id;
    n->label = label;
    return n;
}

void nd_free(struct node *nd) { free(nd); }

struct node_list *ndl_new(size_t n, ...) {
    size_t i;

    struct node_list *ndl = calloc(1, sizeof(struct node_list));
    if (ndl == NULL) {
        return NULL;
    }

    ndl->list = calloc(n, sizeof(struct node *));
    if (ndl->list == NULL) {
        free(ndl);
        return NULL;
    }

    ndl->size = n;

    va_list args;
    va_start(args, n);

    struct node *tmp;
    for (i = 0; i < n; ++i) {
        tmp = va_arg(args, struct node *);
        ndl->list[i] = tmp;
    }

    va_end(args);
    return ndl;
}

void ndl_free(struct node_list *ndl) {
    size_t i;

    for (i = 0; i < ndl->size; ++i) {
        free(ndl->list[i]);
    }

    free(ndl->list);
    free(ndl);
}
