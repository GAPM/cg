#include "lexer.h"

#include <locale>
#include <sstream>

namespace {
const char eof = std::char_traits<char>::eof();
}

lexer::lexer(std::string file_name) { this->input = std::ifstream(file_name); }

lexer::~lexer() { this->input.close(); }

token lexer::next() {
  char top;

  while (input.peek() != eof) {
    top = input.peek();
    if (std::isspace(top)) {
      input.get();
      continue;
    } else if (top == '.' | std::isdigit(top)) {
      return read_dot_or_number();
    } else if (top == '\"') {
      return read_string_literal();
    } else {
      input.get();
    }
  }

  return {tok_eof, ""};
}

/*
 * Reads either a dot or a literal for an integer or a real number.
 *
 *   The states in this FSM are:
 *    >0  start
 *    (1) read zero from 0
 *     2  read 'x' or 'X' from 1
 *    (3) read hex digit from 2 or 3
 *    (4) read digit from 0, 1 or 4
 *    (5) read dot from 0, 1 or 4
 *    (6) read digit from 5, 6, 7 or 8
 *     7  read 'e' or 'E' from 1, 4 or 6
 *     8  read '-' or '+' from 7
 *    (9) read digit from 7, 8 or 9
 */
token lexer::read_dot_or_number() {
  int state = 0;
  char last;
  std::stringstream str;

  while (true) {
    input >> std::noskipws >> last;
    switch (state) {
    case 0:
      if (last == '0') {
        state = 1;
      } else if (last == '.') {
        state = 5;
      } else if (std::isdigit(last)) {
        state = 4;
      }
      break;
    case 1:
      if (last == 'x' || last == 'X') {
        state = 2;
      } else if (std::isdigit(last)) {
        state = 4;
      } else if (last == '.') {
        state = 5;
      } else if (last == 'e' || last == 'E') {
        state = 7;
      } else {
        input.putback(last);
        return {tok_integer, str.str()};
      }
      break;
    case 2:
      if (std::isxdigit(last)) {
        state = 3;
      } else {
        input.putback(last);
        return {tok_err, "bad character"};
      }
      break;
    case 3:
      if (!std::isxdigit(last)) {
        input.putback(last);
        return {tok_integer, str.str()};
      }
      break;
    case 4:
      if (std::isdigit(last)) {
      } else if (last == '.') {
        state = 5;
      } else if (last == 'e' || last == 'E') {
        state = 7;
      } else {
        input.putback(last);
        return {tok_integer, str.str()};
      }
      break;
    case 5:
      if (std::isdigit(last)) {
        state = 6;
      } else {
        std::string lexeme = str.str();
        input.putback(last);

        if (lexeme.compare(".") != 0) {
          input.putback('.');
          return {tok_integer, lexeme};
        } else {
          return {tok_dot, "."};
        }
      }
      break;
    case 6:
      if (std::isdigit(last)) {
      } else if (last == 'e' || last == 'E') {
        state = 7;
      } else {
        input.putback(last);
        return {tok_double, str.str()};
      }
      break;
    case 7:
      if (last == '-' || last == '+') {
        state = 8;
      } else if (std::isdigit(last)) {
        state = 9;
      } else {
        input.putback(last);
        return {tok_err, "bad character"};
      }
      break;
    case 8:
      if (std::isdigit(last)) {
        state = 9;
      } else {
        input.putback(last);
        return {tok_err, str.str()};
      }
      break;
    case 9:
      if (std::isdigit(last)) {
      } else {
        input.putback(last);
        return {tok_double, str.str()};
      }
    default:
      break;
    }

    str.put(last);
  }
}

/*
 * Reads either an identifier or a reserved word
 *
 *   The states in this FSM are:
 *    >0  start
 *    (1) Read '_' or alpha from 0 or '_', alpha or digit from 1
 */
token lexer::read_id_or_reserved() {
  int state = 0;
  char last;
  std::stringstream str;

  while (true) {
    input >> std::noskipws >> last;

    switch (state) {
    case 0:
      if (last == '_' || std::isalpha(last)) {
        state = 1;
      }
      break;
    case 1:
      if (last == '_' || true) {

      }
    }

    str.put(last);
  }
}

/*
 * Reads a string literal
 *
 *   The states in this FSM are:
 *    >0  start
 *     1  read '\"' from 0 or any char from 1
 *    (2) read '\"' from 1
 */
token lexer::read_string_literal() {
  int state = 0;
  char last;
  std::stringstream str;

  while (true) {
    input >> std::noskipws >> last;

    switch (state) {
    case 0:
      if (last == '\"') {
        state = 1;
      }
      break;
    case 1:
      if (last == eof) {
        return {tok_err, "bad character"};
      } else if (last == '\"') {
        state = 2;
      }
      break;
    case 2:
      return {tok_str, str.str()};
    }

    str.put(last);
  }
}
