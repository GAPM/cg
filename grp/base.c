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

#include "base.h"

void GRP_init(void) {
    setlocale(LC_ALL, "");
    fwide(stdin, -1);
    fwide(stdout, -1);
    fwide(stderr, -1);
}

char *GRP_newline() {
#ifdef _WIN32
    return "\r\n";
#elif __unix__
    return "\n";
#endif
}
