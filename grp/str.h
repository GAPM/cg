#ifndef GRP_STR_H
#define GRP_STR_H

#include <stdbool.h>
#include <stdlib.h>
#include <wchar.h>

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
