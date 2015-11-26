%{
#include <stdint.h>
#include <stdio.h>

#include "ast.h"
#include "helper.h"

extern int yylineno;
extern void yyerror(char*);
extern int yylex();
%}

%union {
    char *t;
    struct arg_list *al;
    struct literal *lt;
}

%token FUNC
%token DEC
%token BODY
%token POW

%token<t> ID
%token<t> INT
%token<t> FLOAT
%token<t> CHAR;
%token<t> STRING;

%token ERR

%type<al> arg_list;
%type<lt> literal;

%start init

%nonassoc UNEG UPOS
%right POW
%left '+' '-'
%left '*' '/' '%'

%%

arg_list:              { $$ = NULL; }
         | ID arg_list { $$ = new_arg_list($1, $2); }
         ;

expr: expr '+' expr
    | expr '-' expr
    | expr '*' expr
    | expr '/' expr
    | expr '%' expr
    | expr POW expr
    | '(' expr ')'
    | '-' expr %prec UNEG
    | '+' expr %prec UPOS
    | literal
    ;

literal: STRING { $$ = new_literal(STRING_LIT, $1); }
       | INT    { $$ = new_literal(INTEGER_LIT, $1); }
       | FLOAT  { $$ = new_literal(FLOAT_LIT, $1); }
       | CHAR   { $$ = new_literal(CHAR_LIT, $1); }
       ;

fdef: FUNC ID '(' arg_list ')' '{' inst_list '}'
    ;

inst: fdef
   | expr ';';

inst_list:
        | inst inst_list
        ;

init: inst_list {  };
