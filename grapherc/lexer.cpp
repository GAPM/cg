#include "lexer.h"

#include <sstream>
#include <iostream>

lexer::lexer(std::string file_name) {
    this->input = std::ifstream(file_name);
}

lexer::~lexer() {
    this->input.close();
}

token lexer::next() {
    char last;

    this->input >> std::noskipws >> last;

    if (this->input.eof() || this->input.peek() ==
            std::char_traits<char>::eof()) {
        return { tok_eof, std::string() };
    }

    while(isspace(last)) {
        this->input >> std::noskipws >> last;
    }

    switch(last) {
    case '+':
        return { tok_add, "+" };
    case '-':
        return { tok_sub, "-" };
    case '*':
        return { tok_mul, "*" };
    case '/':
        return { tok_div, "/" };
    }
    this->input.putback(last);

    if (std::isalpha(last)) {
        return this->readId();
    } else if (std::isdigit(last) || last == '.') {
        return this->readNumber();
    }
}

token lexer::readId() {
    std::stringstream str;
    char last;
    this->input >> std::noskipws >> last;

    while(std::isalpha(last)) {
        str.put(last);
        this->input >> std::noskipws >> last;
    }
    this->input.putback(last);

    return { tok_id, str.str() };
}

token lexer::readNumber() {
    std::stringstream str;
    char last;
    this->input >> std::noskipws >> last;

    while (std::isdigit(last)) {
        str.put(last);
        this->input >> std::noskipws >> last;
    }

    if (last == '.') {
        str.put(last);
        this->input >> std::noskipws >> last;

        if (!std::isdigit(last)) {
            this->input.putback(last);
            return { tok_err, "bad character" };
        } else {
            while (std::isdigit(last)) {
                str.put(last);
                this->input >> std::noskipws >> last;
            }

            this->input.putback(last);
            return { tok_double, str.str() };
        }
    } else {
        this->input.putback(last);
        return { tok_integer, str.str() };
    }
}
