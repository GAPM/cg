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
#include <wchar.h>

#include "base.h"
#include "str.h"

int main(void) {
    init();

    str x = str_new("áéíóú");
    str y = str_new("ãẽĩõũ");
    str w = str_new(" ");

    assert(str_length(x) == 5);
    assert(str_length(y) == 5);
    assert(str_length(w) == 1);
    assert(str_length(x) == str_length(y));

    assert(!str_eq(x, y));
    assert(str_cmp(x, y) < 0);

    str a = str_cat(x, w);
    str b = str_cat(a, y);

    assert(str_length(b) == 11);

    // When running the test outside `make test` or `ctest`
    str_println(b); // Should appear

    str_free(x);
    str_free(y);
    str_free(w);
    str_free(a);
    str_free(b);
}
