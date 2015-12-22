#include <assert.h>
#include <wchar.h>

#include "init.h"
#include "str.h"

int main(void) {
    init();
    struct str *s = str_new(L"Simón Oroño");
    str_println(s);

    assert(wcslen(s->string) == s->length);

    str_free(s);
}
