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

#include "bitmatrix.h"

bitmatrix bm_new(size_t r, size_t c) {
    bitmatrix bm = calloc(1, sizeof(struct bitmatrix));
    if (bm == NULL) {
        return NULL;
    }

    bm->rows = r;
    bm->columns = c;
    bm->matrix = ba_new(r * c);
    if (bm->matrix == NULL) {
        free(bm);
        return NULL;
    }

    return bm;
}

bool bm_get(bitmatrix bm, size_t r, size_t c) {
    return ba_get(bm->matrix, r * bm->rows + c);
}

void bm_set(bitmatrix bm, size_t r, size_t c, bool v) {
    ba_set(bm->matrix, r * bm->rows + c, v);
}

bitmatrix bm_copy(bitmatrix bm) {
    bitmatrix n = calloc(1, sizeof(struct bitmatrix));
    if (n == NULL) {
        return NULL;
    }

    n->rows = bm->rows;
    n->columns = bm->columns;
    n->matrix = ba_copy(bm->matrix);
    if (n->matrix == NULL) {
        free(n);
        return NULL;
    }

    return n;
}

void bm_free(bitmatrix bm) {
    ba_free(bm->matrix);
    free(bm);
}
