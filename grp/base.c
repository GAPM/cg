#include "base.h"

void init(void) {
    setlocale(LC_ALL, "");
    fwide(stdin, 1);
    fwide(stdout, 1);
    fwide(stderr, 1);
}

wchar_t *newline() {
#ifdef _WIN32
    return str_new(L"\r\n");
#elif __unix__
    return str_new(L"\n");
#endif
}
