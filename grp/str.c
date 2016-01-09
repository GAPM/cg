#include "str.h"

str str_new(wchar_t *s) {
    size_t new_length = wcslen(s) + 1;
    str n = calloc(new_length, sizeof(wchar_t));
    wcscpy(n, s);
    return n;
}

str str_cat(str s1, str s2) {
    size_t new_length = wcslen(s1) + wcslen(s2) + 1;
    str n = calloc(new_length, sizeof(wchar_t));
    wcscat(n, s1);
    wcscat(n, s2);
    return n;
}

int str_cmp(str s1, str s2) {
    return wcscoll(s1, s2);
}

bool str_eq(str s1, str s2) {
    if (s1 != NULL && s2 != NULL) {
        return wcscoll(s1, s2) == 0;
    }
    return false;
}

size_t str_length(str s) {
    return wcslen(s);
}

void str_print(str s) {
    if (s != NULL) {
        wprintf(L"%ls", s);
    }
}

void str_println(str s) {
    if (s != NULL) {
        str nl = newline();
        str_print(s);
        wprintf(nl);
        str_free(nl);
    }
}

void str_free(str s) {
    if (s != NULL) {
        free(s);
    }
}
