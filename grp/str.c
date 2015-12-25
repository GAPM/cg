#include "str.h"

str_t str_new(wchar_t *s) {
    size_t new_length = wcslen(s) + 1;
    str_t n = calloc(new_length, sizeof(wchar_t));
    wcscpy(n, s);
    return n;
}

str_t str_cat(str_t str1, str_t str2) {
    size_t new_length = wcslen(str1) + wcslen(str2) + 1;
    str_t n = calloc(new_length, sizeof(wchar_t));
    wcscat(n, str1);
    wcscat(n, str2);
    return n;
}

bool str_eq(str_t str1, str_t str2) {
    if (str1 != NULL && str2 != NULL) {
        return wcscmp(str1, str2) == 0;
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
