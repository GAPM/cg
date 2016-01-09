#ifndef GRP_GRP_H
#define GRP_GRP_H

#include <locale.h>
#include <stdio.h>
#include <wchar.h>

#include "str.h"

// Routine that must be executed at the beginning of every Grp program.
//
// It does:
//     - Initialize the localle to the system's localle.
//     - Sets the orientation of the default streams to wide.
void init(void);
wchar_t *newline();

#endif // GRP_GRP_H
