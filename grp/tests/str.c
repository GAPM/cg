#include <assert.h>
#include <wchar.h>

#include "grp.h"
#include "str.h"

int main(void) {
    init();

    str_t s = str_new(L"áéíóú");
    str_t o = str_new(L"ãẽĩõũ");
    str_t w = str_new(L" ");

    assert(str_length(s) == 5);
    assert(str_length(o) == 5);
    assert(str_length(w) == 1);

    str_t a = str_cat(s, w);
    str_t b = str_cat(a, o);

    assert(str_length(b) == 11);

    str_println(b); // Should appear
    printf("aeiou aeiou\n"); // Should not appear

    str_free(s);
    str_free(o);
    str_free(w);
    str_free(a);
    str_free(b);
}
