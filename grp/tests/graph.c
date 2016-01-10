/*
 * Copyright 2016 Simón Oroño
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include <assert.h>
#include <stdio.h>

#include "base.h"
#include "graph.h"

int main(void) {
    init();

    graph_t gr = gr_new(5, 5, 4,
        label_new(0, str_new("1")),
        label_new(1, str_new("2")),
        label_new(2, str_new("3")),
        label_new(3, str_new("4")),
        label_new(4, str_new("5")),
        edge_new(0, 1, true),
        edge_new(2, 3, true),
        edge_new(4, 0, true),
        edge_new(0, 2, false));

    assert(gr_is_connected(gr, 0, 1) == true);
    assert(gr_is_connected(gr, 2, 3) == true);
    assert(gr_is_connected(gr, 3, 2) == true);
    assert(gr_is_connected(gr, 4, 0) == true);
    assert(gr_is_connected(gr, 0, 2) == false);

    assert(gr_is_connected_l(gr, str_new("1"), str_new("2")) == true);
    assert(gr_is_connected_l(gr, str_new("3"), str_new("4")) == true);
    assert(gr_is_connected_l(gr, str_new("4"), str_new("3")) == true);
    assert(gr_is_connected_l(gr, str_new("5"), str_new("1")) == true);
    assert(gr_is_connected_l(gr, str_new("1"), str_new("3")) == false);

    gr_free(gr);
}
