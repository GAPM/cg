#ifndef GRPC_AST_H
#define GRPC_AST_H

#include <stdint.h>

enum type {
    AT_STRING,
    AT_INTEGER,
    AT_FLOAT,
    AT_CHAR,
    AT_VAR,
    AT_FCALL,

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

    STMT_FDEF,
    STMT_EXP,
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

struct atom {
    enum type t;
    char *text;
    struct fcall *fc;
};

struct atom *new_atom(enum type, char *, struct fcall *);
void free_atom(struct atom *);

struct expr {
    enum type t;
    struct expr *left;
    struct expr *right;
    struct atom *at;
};

struct expr *new_expr(enum type, struct expr *, struct expr *, struct atom *);
void free_expr(struct expr *);

struct expr_list {
    struct expr *ex;
    struct expr_list *ls;
};

struct expr_list *new_expr_list(struct expr_list *, struct expr *);
void free_expr_list(struct expr_list *);

struct stmt_list {
    struct stmt *st;
    struct stmt_list *sl;
};

struct stmt_list *new_stmt_list(struct stmt_list *, struct stmt *);
void free_stmt_list(struct stmt_list *);

struct fdef {
    enum type *ty;
    char *name;
    struct arg_list *al;
    struct stmt_list *sl;
};

struct fdef *new_fdef(enum type *, char *, struct arg_list *, struct stmt_list *);
void fdef_free(struct fdef *);

struct fcall {
    char *name;
    struct expr_list *el;
};

struct fcall *new_fcall(char *, struct expr_list *);
void free_fcall(struct fcall *);

struct stmt {
    enum type t;
    struct fdef *fd;
    struct expr *ex;
};

struct stmt *new_stmt(enum type, struct fdef *, struct expr *);
void free_stmt(struct stmt *);

#endif // GRPC_AST_H
