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

"("      { return LPAREN; }
")"      { return RPAREN; }
"{"      { return LBRACE; }
"}"      { return RBRACE; }
";"      { return SEMCLN; }
","      { return COMMA; }
"**"     { return OP_POW; }
"+"      { return OP_SUM; }
"-"      { return OP_SUB; }
"*"      { return OP_MUL; }
"/"      { return OP_DIV; }
"%"      { return OP_MOD; }
"bool"   { return R_BOOL; }
"double" { return R_DOUBLE; }
"int"    { return R_INT; }

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
