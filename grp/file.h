#ifndef GRP_FILE_H
#define GRP_FILE_H

#include <stdio.h>

#include "str.h"

typedef struct file *file;
struct file {
    FILE *f;
    str name;
    char *c_name;
};

file f_open(str, str);
bool f_is_open(file);
void f_close(file);
void f_free(file);
void f_remove(str);

#endif // GRP_FILE_H
