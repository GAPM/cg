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

#ifndef GRP_BITARRAY_H
#define GRP_BITARRAY_H

#include <stdbool.h>
#include <stdint.h>
#include <stdlib.h>

typedef struct bitarray *bitarray;
struct bitarray {
    size_t size;
    int *array;
};

bitarray GRP_ba_new(size_t s);
bool GRP_ba_get(bitarray ba, size_t i);
void GRP_ba_set(bitarray ba, size_t i, bool v);
bitarray GRP_ba_copy(bitarray ba);
size_t GRP_ba_size(bitarray ba);
void GRP_ba_free(bitarray ba);

#endif // GRP_BITARRAY_H
