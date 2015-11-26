#include "helper.h"

#include <stdlib.h>

struct arg_list *new_arg_list(char *id, struct arg_list *next) {
    struct arg_list *new = calloc(1, sizeof(struct arg_list));

    if (new == NULL) {
        return NULL;
    }

    new->id = id;
    new->next = next;
    return new;
}

void free_arg_list(struct arg_list *al) {
    if (al != NULL) {
        free_arg_list(al->next);
    }
    free(al);
}
