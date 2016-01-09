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

#ifndef GRP_STR_H
#define GRP_STR_H

#include <stdbool.h>
#include <stdlib.h>
#include <wchar.h>

#include "base.h"

typedef wchar_t *str;

str str_new(wchar_t *);
str str_cat(str, str);
int str_cmp(str, str);
bool str_eq(str, str);
size_t str_length(str);
void str_print(str);
void str_println(str);
void str_free(str);

#endif // GRP_STR_H
