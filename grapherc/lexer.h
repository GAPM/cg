#ifndef GRAPHERC_LEXER
#define GRAPHERC_LEXER

#include <fstream>
#include <string>

#include "token.h"

class lexer {
public:
  lexer(std::string file_name);
  ~lexer();
  token next();

private:
  std::ifstream input;
  token read_dot_or_number();
  token read_id_or_reserved();
  token read_string_literal();
};

#endif // GRAPHERC_LEXER
