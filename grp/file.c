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

#include "file.h"

file f_open(str name, str mode) {
    file n = calloc(1, sizeof(struct file));
    if (n == NULL) {
        return NULL;
    }

    char *c_name = calloc(str_length(name), sizeof(wchar_t));
    if (c_name == NULL) {
        free(n);
        return NULL;
    }

    char *c_mode = calloc(str_length(mode), sizeof(wchar_t));
    if (c_mode == NULL) {
        free(n);
        free(c_name);
        return NULL;
    }

    wcstombs(c_name, name, str_length(name) * sizeof(wchar_t));
    wcstombs(c_mode, mode, str_length(mode) * sizeof(wchar_t));

    n->f = fopen(c_name, c_mode);
    n->name = str_new(name);
    n->c_name = c_name;

    if (n->f == NULL) {
        free(n);
        free(c_name);
        free(c_mode);
        return NULL;
    }

    fwide(n->f, 1);

    free(c_mode);

    return n;
}

bool f_is_open(file f) {
    if (f != NULL) {
        return ftell(f->f) >= 0;
    }
    return false;
}

void f_close(file f) {
    if (f != NULL) {
        fclose(f->f);
    }
}

void f_free(file f) {
    if (f != NULL) {
        str_free(f->name);
        free(f);
    }
}

void f_remove(str f) {
    char *c = calloc(str_length(f), sizeof(wchar_t));
    if (c == NULL) {
        return;
    }

    wcstombs(c, f, str_length(f) * sizeof(wchar_t));
    remove(c);
}
