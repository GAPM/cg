#include "ast.h"

#include <stdlib.h>

struct arg *new_arg(enum type t, char *name) {
    struct arg *n = calloc(1, sizeof(struct arg));
    if (n == NULL) {
        return n;
    }

    n->name = name;
    return n;
}

void free_arg(struct arg *ag) {
    if (ag != NULL) {
        free(ag);
    }
}

void free_arg(struct arg *);

struct arg_list *new_arg_list(struct arg_list *ls, struct arg *ag) {
    struct arg_list *n = calloc(1, sizeof(struct arg_list));

    if (n == NULL) {
        return NULL;
    }

    n->ag = ag;
    n->ls = ls;
    return n;
}

void free_arg_list(struct arg_list *al) {
    if (al != NULL) {
        free_arg_list(al->ls);
    }
    free(al);
}

struct atom *new_atom(enum type t, char *text) {
    struct atom *n = calloc(1, sizeof(struct atom));

    if (n == NULL) {
        return NULL;
    }

    n->t = t;
    n->text = text;
    return n;
}

void free_atom(struct atom *at) {
    if (at != NULL) {
        free(at);
    }
}

struct expr *new_expr(enum type t, struct expr * l, struct expr * r, struct atom *at) {
    struct expr *n = calloc(1, sizeof(struct expr));

    if (n == NULL) {
        return NULL;
    }

    n->t = t;
    n->left = l;
    n->right = r;
    n->at = at;
    return n;
}

void free_expr(struct expr *ex) {
    if (ex != NULL) {
        free_expr(ex->left);
        free_expr(ex->right);
        free_atom(ex->at);
        free(ex);
    }
}
