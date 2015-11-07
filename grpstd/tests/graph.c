#include <assert.h>
#include <stdio.h>
#include <string.h>
#include "graph.h"

size_t next_id(size_t *i) { return (*i)++; }

int main(void) {
    size_t i = 0;
    struct node_list *ndl =
        ndl_new(5, nd_new(next_id(&i), "A"), nd_new(next_id(&i), "B"),
                nd_new(next_id(&i), "C"), nd_new(next_id(&i), "D"),
                nd_new(next_id(&i), "E"));

    assert(ndl->list[0]->id == 0 && strcmp(ndl->list[0]->label, "A") == 0);
    assert(ndl->list[1]->id == 1 && strcmp(ndl->list[1]->label, "B") == 0);
    assert(ndl->list[2]->id == 2 && strcmp(ndl->list[2]->label, "C") == 0);
    assert(ndl->list[3]->id == 3 && strcmp(ndl->list[3]->label, "D") == 0);
    assert(ndl->list[4]->id == 4 && strcmp(ndl->list[4]->label, "E") == 0);

    ndl_free(ndl);
}
