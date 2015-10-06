
#include "token.h"

namespace {
const std::string token_type_string[] = {
  "tok_eof",
  "tok_err",

  "tok_integer",
  "tok_double",
  "tok_id",
  "tok_str",

  "tok_dot",

  "tok_add",
  "tok_sub",
  "tok_mul",
  "tok_div"
};
}

token::operator std::string() {
  return "<" + token_type_string[this->type] + ", " + this->text + ">";
}
