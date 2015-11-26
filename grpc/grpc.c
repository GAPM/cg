#include <stdio.h>
#include <stdlib.h>

#include "cmd.h"

extern int yyparse();
extern FILE *yyin;

void print_error(const char *str) { fprintf(stderr, "%s\n", str); }

void fatal_error(const char *str) {
    print_error(str);
    exit(-1);
}

int main(int argc, char **argv) {
    int n_pos_args = count_pos_args(argc, argv);

    if (n_pos_args == 0) {
        fatal_error("Not enough input files");
    }

    if (n_pos_args > 1) {
        fatal_error("Only one-file compilation is supported");
    }

    yyin = fopen(get_pos_arg(argc, argv, 0), "r");

    if (yyin) {
        yyparse();
    }
}
