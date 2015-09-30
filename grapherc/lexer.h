
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

    token readId();
    token readNumber();
};

#endif //GRAPHERC_LEXER
