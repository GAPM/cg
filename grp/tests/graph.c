#include <assert.h>
#include <stdio.h>

#include "graph.h"
#include "grp.h"

int main(void) {
    init();

    graph_t gr = gr_new(5, 5, 4,
        label_new(0, str_new(L"1")),
        label_new(1, str_new(L"2")),
        label_new(2, str_new(L"3")),
        label_new(3, str_new(L"4")),
        label_new(4, str_new(L"5")),
        edge_new(0, 1, true),
        edge_new(2, 3, true),
        edge_new(4, 0, true),
        edge_new(0, 2, false));

    assert(gr_is_connected(gr, 0, 1) == true);
    assert(gr_is_connected(gr, 2, 3) == true);
    assert(gr_is_connected(gr, 3, 2) == true);
    assert(gr_is_connected(gr, 4, 0) == true);
    assert(gr_is_connected(gr, 0, 2) == false);

    assert(gr_is_connected_l(gr, str_new(L"1"), str_new(L"2")) == true);
    assert(gr_is_connected_l(gr, str_new(L"3"), str_new(L"4")) == true);
    assert(gr_is_connected_l(gr, str_new(L"4"), str_new(L"3")) == true);
    assert(gr_is_connected_l(gr, str_new(L"5"), str_new(L"1")) == true);
    assert(gr_is_connected_l(gr, str_new(L"1"), str_new(L"3")) == false);

    gr_free(gr);
}
