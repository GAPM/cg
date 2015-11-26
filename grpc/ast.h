#ifndef GRPC_AST_H
#define GRPC_AST_H

#include <stdint.h>

enum type {
    STRING_LIT,
    INTEGER_LIT,
    FLOAT_LIT,
    CHAR_LIT,

    EXP_POW,
    EXP_ADD,
    EXP_SUB,
    EXP_MOD,
    EXP_MUL,
    EXP_DIV,
    EXP_UNEG,
    EXP_UPOS,
};

struct literal {
    enum type t;
    char *text;
};

struct literal *new_literal(enum type, char *);

struct expr {
    enum type t;
};

struct func_call {
    enum type t;

    char *name;
    struct expr args[];
};

struct atom {
    enum type t;
};

#endif // GRPC_AST_H
