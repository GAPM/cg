#include <assert.h>
#include <stdio.h>

#include "graph.h"
#include "grp.h"

int main(void) {
    init();
    struct graph *gr = gr_new(5, 5, 4,
        (struct label){0, "1"},
        (struct label){1, "2"},
        (struct label){2, "3"},
        (struct label){3, "4"},
        (struct label){4, "5"},
        (struct edge){0, 1, true},
        (struct edge){2, 3, true},
        (struct edge){4, 0, true},
        (struct edge){0, 2, false});

    assert(gr_is_connected(gr, 0, 1) == true);
    assert(gr_is_connected(gr, 2, 3) == true);
    assert(gr_is_connected(gr, 3, 2) == true);
    assert(gr_is_connected(gr, 4, 0) == true);
    assert(gr_is_connected(gr, 0, 2) == false);

    assert(gr_is_connected_l(gr, "1", "2") == true);
    assert(gr_is_connected_l(gr, "3", "4") == true);
    assert(gr_is_connected_l(gr, "4", "3") == true);
    assert(gr_is_connected_l(gr, "5", "1") == true);
    assert(gr_is_connected_l(gr, "1", "3") == false);

    gr_free(gr);
}
