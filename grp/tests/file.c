#include <assert.h>

#include "base.h"
#include "file.h"

int main() {
    init();

    file fw = f_open(L"test.txt", L"w");

    assert(f_is_open(fw));

    f_close(fw);
    f_free(fw);
}
