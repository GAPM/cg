#include "str.h"

struct str *str_new(const wchar_t *s) {
    struct str *n = calloc(1, sizeof(struct str));
    if (n == NULL) {
        return NULL;
    }

    size_t length = wcslen(s);
    n->length = length;
    n->string = calloc(length + 1, sizeof(wchar_t));
    wcscpy(n->string, s);
    return n;
}

struct str *str_cat(struct str *s1, struct str *s2) {
    size_t new_length = s1->length + s2->length + 1;
    wchar_t *new_wstr = calloc(new_length, sizeof(wint_t));

    wcscat(new_wstr, s1->string);
    wcscat(new_wstr, s2->string);

    struct str *n = str_new(new_wstr);
    free(new_wstr);

    return n;
}

size_t str_length(struct str *s) {
    if (s != NULL) {
        return s->length;
    }
    return -1;
}

void str_print(struct str *s) {
    if (s != NULL) {
        wprintf(L"%ls", s->string);
    }
}

void str_println(struct str *s) {
    if (s != NULL) {
        str_print(s);
        wprintf(L"\n");
    }
}

void str_free(struct str *s) {
    if (s != NULL) {
        free(s->string);
        free(s);
    }
}
