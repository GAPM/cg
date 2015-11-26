#include <assert.h>
#include <stdio.h>

#include "graph.h"

int main(void) {
    struct graph *g = gr_new(5, 4,
        (struct label){0, "1"},
        (struct label){1, "2"},
        (struct label){2, "3"},
        (struct label){3, "4"},
        (struct label){4, "5"},
        (struct edge){0, 1, true},
        (struct edge){2, 3, true},
        (struct edge){4, 0, true},
        (struct edge){0, 2, false});

    assert(gr_is_connected(g, 0, 1) == true);
    assert(gr_is_connected(g, 2, 3) == true);
    assert(gr_is_connected(g, 4, 0) == true);
    assert(gr_is_connected(g, 0, 2) == false);

    printf("%p\n", g);
}
