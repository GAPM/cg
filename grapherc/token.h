#ifndef GRAPHERC_TOKEN
#define GRAPHERC_TOKEN

#include <string>

enum token_type {
    tok_eof,
    tok_err,
    tok_integer,
    tok_double,
    tok_id,

    tok_add,
    tok_sub,
    tok_mul,
    tok_div
};

struct token {
    token_type type;
    std::string text;
};

extern const std::string token_type_string[];

#endif // GRAPHERC_TOKEN
