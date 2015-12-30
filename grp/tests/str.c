#include <assert.h>
#include <wchar.h>

#include "grp.h"
#include "str.h"

int main(void) {
    init();

    str x = str_new(L"áéíóú");
    str y = str_new(L"ãẽĩõũ");
    str w = str_new(L" ");

    assert(str_length(x) == 5);
    assert(str_length(y) == 5);
    assert(str_length(w) == 1);
    assert(str_length(x) == str_length(y));

    assert(!str_eq(x, y));
    assert(str_cmp(x, y) < 0);

    str a = str_cat(x, w);
    str b = str_cat(a, y);

    assert(str_length(b) == 11);

    str_println(b); // Should appear
    printf("aeiou aeiou\n"); // Should not appear

    str_free(x);
    str_free(y);
    str_free(w);
    str_free(a);
    str_free(b);
}
