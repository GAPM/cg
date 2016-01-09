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
    uint8_t *array;
};

// ba_new allocates a new bitarray of size s and returns a pointer to it
bitarray ba_new(size_t s);

// ba_get tests wether the bit at index i in bitarray ba is on or off
bool ba_get(bitarray ba, size_t i);

// ba_set sets the bit at index i in bitarray ba to the value v
void ba_set(bitarray ba, size_t i, bool v);

// ba_copy creates an exact copy of bitarray ba and returns a pointer to it
bitarray ba_copy(bitarray ba);

// ba_size returns the number of elements on the bitarray
size_t ba_size(bitarray ba);

// ba_free deallocates the memory occupied by bitarray ba
void ba_free(bitarray ba);

#endif // GRP_BITARRAY_H
