#include "ast.h"

#include <stdlib.h>

#define RET_IF_NULL(x) if (x == NULL) { return NULL; }

enum type *new_type(enum type ty) {
    enum type *n = calloc(1, sizeof(enum type));
    RET_IF_NULL(n);

    *n = ty;
    return n;
}

void free_type(enum type *ty) {
    if (ty != NULL) {
        free(ty);
    }
}

struct arg *new_arg(enum type *ty, char *name) {
    struct arg *n = calloc(1, sizeof(struct arg));
    RET_IF_NULL(n);

    n->ty = ty;
    n->name = name;
    return n;
}

void free_arg(struct arg *ag) {
    if (ag != NULL) {
        free_type(ag->ty);
        free(ag);
    }
}

void free_arg(struct arg *);

struct arg_list *new_arg_list(struct arg_list *ls, struct arg *ag) {
    struct arg_list *n = calloc(1, sizeof(struct arg_list));
    RET_IF_NULL(n);

    n->ag = ag;
    n->ls = ls;
    return n;
}

void free_arg_list(struct arg_list *al) {
    if (al != NULL) {
        free_arg_list(al->ls);
        free_arg(al->ag);
        free(al);
    }
}

struct atom *new_atom(enum type t, char *text, struct fcall *fc) {
    struct atom *n = calloc(1, sizeof(struct atom));
    RET_IF_NULL(n);

    n->t = t;
    n->text = text;
    n->fc = fc;
    return n;
}

void free_atom(struct atom *at) {
    if (at != NULL) {
        free_fcall(at->fc);
        free(at);
    }
}

struct expr *new_expr(enum type t, struct expr * l, struct expr * r, struct atom *at) {
    struct expr *n = calloc(1, sizeof(struct expr));
    RET_IF_NULL(n);

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

struct expr_list *new_expr_list(struct expr_list *ls, struct expr *ex) {
    struct expr_list *n = calloc(1, sizeof(struct expr_list));
    RET_IF_NULL(n);

    n->ls = ls;
    n->ex = ex;
    return n;
}

void free_expr_list(struct expr_list *ls) {
    if (ls != NULL) {
        free_expr_list(ls->ls);
        free(ls);
    }
}

struct stmt_list *new_stmt_list(struct stmt_list *sl, struct stmt *st) {
    struct stmt_list *n = calloc(1, sizeof(struct stmt_list));
    RET_IF_NULL(n);

    n->sl = sl;
    n->st = st;
    return n;
}

void free_stmt_list(struct stmt_list *sl) {
    if (sl != NULL) {
        free_stmt_list(sl->sl);
        free_stmt(sl->st);
    }
}

struct fdef *new_fdef(enum type *ty, char *name, struct arg_list *al, struct stmt_list *sl) {
    struct fdef *n = calloc(1, sizeof(struct fdef));
    RET_IF_NULL(n);

    n->ty = ty;
    n->name = name;
    n->al = al;
    n->sl = sl;
    return n;
}

void free_fdef(struct fdef *fd) {
    if (fd != NULL) {
        free_type(fd->ty);
        free_arg_list(fd->al);
        free_stmt_list(fd->sl);
        free(fd);
    }
}

struct fcall *new_fcall(char *name, struct expr_list *el) {
    struct fcall *n = calloc(1, sizeof(struct fcall));
    RET_IF_NULL(n);

    n->name = name;
    n->el = el;
    return n;
}

void free_fcall(struct fcall *fc) {
    if (fc != NULL) {
        free_expr_list(fc->el);
        free(fc);
    }
}

struct stmt *new_stmt(enum type t, struct fdef * fd, struct expr * ex) {
    struct stmt *n = calloc(1, sizeof(struct stmt));
    RET_IF_NULL(n);

    n->t = t;
    n->fd = fd;
    n->ex = ex;
    return n;
}

void free_stmt(struct stmt *st) {
    if (st != NULL) {
        free_fdef(st->fd);
        free_expr(st->ex);
        free(st);
    }
}
