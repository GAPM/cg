#include <assert.h>
#include <wchar.h>

#include "grp.h"
#include "str.h"

int main(void) {
    init();

    struct str *s = str_new(L"Simón");
    struct str *o = str_new(L"Oroño");
    struct str *w = str_new(L" ");

    assert(str_length(s) == 5);
    assert(str_length(o) == 5);
    assert(str_length(w) == 1);

    struct str *a = str_cat(s, w);
    struct str *b = str_cat(a, o);

    assert(str_length(b) == 11);

    str_println(b);

    str_free(s);
    str_free(o);
    str_free(w);
    str_free(a);
    str_free(b);
}
