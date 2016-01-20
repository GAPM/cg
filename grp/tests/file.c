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

#include "base.h"
#include "file.h"

int main() {
    init();

    file fw = f_open("test.txt", "w");

    assert(f_is_open(fw));

    f_write(fw, "ãẽĩõũ");
    f_write(fw, newline());
    f_write(fw, "Dude");
    f_write(fw, newline());

    f_close(fw);
    f_free(fw);

    file fr = f_open("test.txt", "r");

    str x = f_readline(fr);
    str y = f_readline(fr);

    str_println(x);
    str_println(y);

    str_free(x);
    str_free(y);

    f_close(fr);
    f_free(fr);

    f_remove("test.txt");
}
