%option nodefault
%option nounistd
%option never-interactive
%option noyywrap
%option yylineno

%{
#include <string.h>

#include "parser.h"
%}

%%

"**"   { return POW; }
"func" { return FUNC; }
"dec"  { return DEC; }
"body" { return BODY; }

\'.\' {
    yylval.t = yytext;
    return CHAR;
}

\"(\\.|[^"])*\" {
    yylval.t = yytext;
    return STRING;
}

[a-zA-Z_][a-zA-Z0-9_]+ {
    yylval.t = yytext;
    return ID;
}

[0-9]+ {
    yylval.t = yytext;
    return INT;
}

[0-9]*\.?[0-9]+((e|E)(-|\+)[0-9]+)? {
    yylval.t = yytext;
    return FLOAT;
}

[ \t\n\r] { /* skip whitespace */ }

. { return yytext[0]; }

%%

void yyerror(const char *msg) {
    fprintf(stderr, "%d: %s\n", yylineno, msg);
}
