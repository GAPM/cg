#include <iostream>
#include <locale>
#include <sstream>

#include "lexer.h"

using namespace std;

int main(void) {
  lexer l("test.txt");
  token t;

  while ((t = l.next()).type != token_type::tok_eof) {
    cout << string(t) << endl;
  }
}
