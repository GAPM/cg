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

    T_INT,
    T_DOUBLE,
    T_BOOL,
    T_GRAPH,
};

struct arg {
    enum type t;
    char *name;
};

struct arg *new_arg(enum type, char *);
void free_arg(struct arg *);

struct arg_list {
    struct arg *ag;
    struct arg_list *ls;
};

struct arg_list *new_arg_list(struct arg_list *, struct arg *);
void free_arg_list(struct arg_list *);

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

struct func_call {
    enum type t;
    char *name;
    struct expr args[];
};

#endif // GRPC_AST_H
