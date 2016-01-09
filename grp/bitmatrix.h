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

#ifndef GRP_BITMATRIX_H
#define GRP_BITMATRIX_H

#include <stdbool.h>
#include <stdlib.h>

#include "bitarray.h"

typedef struct bitmatrix *bitmatrix;
struct bitmatrix {
    size_t rows;
    size_t columns;
    bitarray matrix;
};

bitmatrix bm_new(size_t, size_t);
bool bm_get(bitmatrix, size_t, size_t);
void bm_set(bitmatrix, size_t, size_t, bool v);
bitmatrix bm_copy(bitmatrix);
void bm_free(bitmatrix);

#endif // GRP_BITMATRIX_H
