#include "grp.h"

void init(void) {
    setlocale(LC_ALL, "");
    fwide(stdin, 1);
    fwide(stdout, 1);
    fwide(stderr, 1);
}
