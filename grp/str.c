#include "str.h"

str_t str_new(wchar_t *s) {
    size_t new_length = wcslen(s) + 1;
    str_t n = calloc(new_length, sizeof(wchar_t));
    wcscpy(n, s);
    return n;
}

str_t str_cat(str_t s1, str_t s2) {
    size_t new_length = wcslen(s1) + wcslen(s2) + 1;
    str_t n = calloc(new_length, sizeof(wchar_t));
    wcscat(n, s1);
    wcscat(n, s2);
    return n;
}

int str_cmp(str_t s1, str_t s2) {
    return wcscoll(s1, s2);
}

bool str_eq(str_t s1, str_t s2) {
    if (s1 != NULL && s2 != NULL) {
        return wcscoll(s1, s2) == 0;
    }
    return false;
}

size_t str_length(str_t s) {
    return wcslen(s);
}

void str_print(str_t s) {
    if (s != NULL) {
        wprintf(L"%ls", s);
    }
}

void str_println(str_t s) {
    if (s != NULL) {
        str_print(s);
        wprintf(L"\n");
    }
}

void str_free(str_t s) {
    if (s != NULL) {
        free(s);
    }
}
