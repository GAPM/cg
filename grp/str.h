#ifndef GRP_STR_H
#define GRP_STR_H

#include <stdio.h>
#include <stdlib.h>
#include <wchar.h>

struct str {
    wchar_t *string;
    size_t length;
};

struct str *str_new(const wchar_t *);
struct str *str_cat(struct str *, struct str *);
size_t str_length(struct str *);
void str_print(struct str *);
void str_println(struct str *);
void str_free(struct str *);

#endif // GRP_STR_H
