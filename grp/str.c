#include "str.h"

struct str *str_new(const wchar_t *s) {
    struct str *n = calloc(1, sizeof(struct str));
    if (n == NULL) {
        return NULL;
    }

    size_t length = wcslen(s);
    n->length = length;
    n->string = calloc(length, sizeof(wchar_t));
    wcscpy(n->string, s);
    return n;
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
