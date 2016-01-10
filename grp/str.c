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

#include "str.h"

str str_new(char *s) {
    size_t new_length = strlen(s) + 1;
    str n = calloc(new_length, sizeof(char));
    strcpy(n, s);
    return n;
}

str str_cat(str s1, str s2) {
    size_t new_length = strlen(s1) + strlen(s2) + 1;
    str n = calloc(new_length, sizeof(char));
    strcat(n, s1);
    strcat(n, s2);
    return n;
}

int str_cmp(str s1, str s2) { return strcoll(s1, s2); }

bool str_eq(str s1, str s2) {
    if (s1 != NULL && s2 != NULL) {
        return strcoll(s1, s2) == 0;
    }
    return false;
}

size_t str_length(str s) {
    size_t r = 0;
    mbstate_t mb = {0};
    const char *end = s + strlen(s);

    while (s < end) {
        int next = mbrlen(s, end - s, &mb);
        if (next == -1) {
            break;
        }
        s += next;
        ++r;
    }

    return r;
}

void str_print(str s) {
    if (s != NULL) {
        printf("%s", s);
    }
}

void str_println(str s) {
    if (s != NULL) {
        str n = str_cat(s, newline());
        str_print(n);
        str_free(n);
    }
}

void str_free(str s) {
    if (s != NULL) {
        free(s);
    }
}
