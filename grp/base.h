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

#ifndef GRP_GRP_H
#define GRP_GRP_H

#include <locale.h>
#include <stdio.h>
#include <wchar.h>

/*
 * Routine that must be executed at the beginning of every Grp program.
 *
 * It does:
 *     - Initialize the localle to the system's localle.
 *     - Sets the orientation of the default streams to wide.
 */
void GRP_init(void);
char *GRP_newline();

#endif // GRP_GRP_H
