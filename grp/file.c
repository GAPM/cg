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

    n->f = fopen(name, mode);
    if (n->f == NULL) {
        free(n);
        return NULL;
    }

    n->name = str_new(name);
    if (n->name == NULL) {
        fclose(n->f);
        free(n);
        return NULL;
    }

    fwide(n->f, -1);

    return n;
}

bool f_is_open(file f) {
    if (f != NULL) {
        return ftell(f->f) >= 0;
    }
    return false;
}

void f_write(file f, str s) {
    if (f != NULL && s != NULL) {
        if (f_is_open(f)) {
            fprintf(f->f, "%s", s);
        }
    }
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
    if (f == NULL) {
        return;
    }

    remove(f);
}
