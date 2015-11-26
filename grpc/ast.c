#include "ast.h"

#include <stdlib.h>

struct literal *new_literal(enum type t, char *text) {
    struct literal *new = calloc(1, sizeof(struct literal));

    if (new == NULL) {
        return NULL;
    }

    new->t = t;
    new->text = text;
    return new;
}
