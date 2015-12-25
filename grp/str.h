#ifndef GRP_STR_H
#define GRP_STR_H

#include <stdbool.h>
#include <stdlib.h>
#include <wchar.h>

typedef wchar_t *str_t;

str_t str_new(wchar_t *);
str_t str_cat(str_t, str_t);
bool str_eq(str_t, str_t);
size_t str_length(str_t);
void str_print(str_t);
void str_println(str_t);
void str_free(str_t);

#endif // GRP_STR_H
