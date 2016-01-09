#include <assert.h>

#include "base.h"

int main() {
    init();

    str nl = newline();

#ifdef _WIN32
    assert(str_eq(nl, L"\r\n"));
#elif __unix__
    assert(str_eq(nl, L"\n"));
#endif

    str_free(nl);
}
