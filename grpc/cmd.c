#include "cmd.h"

#include <ctype.h>
#include <stdlib.h>

bool is_positional(char *arg) {
    char first = arg[0];
    return first != '-';
}

int count_pos_args(int argc, char **argv) {
    int i = 0;
    int c = 0;

    for (i = 1; i < argc; ++i) {
        if (is_positional(argv[i])) {
            c += 1;
        }
    }

    return c;
}

char *get_pos_arg(int argc, char **argv, int n) {
    int i = 0;
    int c = 0;

    for (i = 0; i < argc; ++i) {
        if (is_positional(argv[i])) {
            if (c == n) {
                return argv[i];
            } else {
                c += 1;
            }
        }
    }

    return NULL;
}
