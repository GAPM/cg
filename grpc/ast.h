#ifndef GRPC_AST_H
#define GRPC_AST_H

#include <stdint.h>

enum type {
    AT_STRING,
    AT_INTEGER,
    AT_FLOAT,
    AT_CHAR,
    AT_VAR,

    EXP_POW,
    EXP_ADD,
    EXP_SUB,
    EXP_MOD,
    EXP_MUL,
    EXP_DIV,
    EXP_UNEG,
    EXP_UPOS,
    EXP_ASSOC,
    EXP_AT,

    TYPE_INT,
    TYPE_DOUBLE,
    TYPE_BOOL,
    TYPE_GRAPH, // Most important TODO in the entire codebase
};

enum type *new_type(enum type);
void free_type(enum type *);

struct arg {
    enum type *ty;
    char *name;
};

struct arg *new_arg(enum type *, char *);
void free_arg(struct arg *);

struct arg_list {
    struct arg *ag;
    struct arg_list *ls;
};

struct arg_list *new_arg_list(struct arg_list *, struct arg *);
void free_arg_list(struct arg_list *);

struct expr_list {
    struct expr *ex;
    struct expr_list *ls;
};

struct expr_list *new_expr_list(struct expr_list *, struct expr *);
void free_expr_list(struct expr_list *);

struct atom {
    enum type t;
    char *text;
};

struct atom *new_atom(enum type, char *);
void free_atom(struct atom *);

struct expr {
    enum type t;
    struct expr *left;
    struct expr *right;
    struct atom *at;
};

struct expr *new_expr(enum type, struct expr *, struct expr *, struct atom *);
void free_expr(struct expr *);

#endif // GRPC_AST_H
