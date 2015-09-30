#include <iostream>
#include <locale>

using namespace std;

#include "lexer.h"

int main(void) {
    lexer l ("test.txt");

    token t = l.next();

    while (t.type != token_type::tok_eof) {
        cout << token_type_string[t.type] << " " << t.text << endl;
        t = l.next();
    }
}
