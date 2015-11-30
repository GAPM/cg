%{
#include <stdint.h>
#include <stdio.h>

#include "ast.h"

extern int yylineno;
extern void yyerror(char*);
extern int yylex();

struct stmt_list *result;
%}

%union {
    char *t;
    struct arg *ag;
    struct arg_list *al;
    struct atom *at;
    struct expr *ex;
    struct expr_list *el;
    struct stmt *st;
    struct stmt_list *sl;
    struct fdef *fd;
    struct fcall *fc;
    enum type *ty;
}

%token R_POW
%token R_BOOL
%token R_DOUBLE
%token R_INT

%token<t> ID
%token<t> INT
%token<t> FLOAT
%token<t> CHAR;
%token<t> STRING;

%token ERR

%type<ag> arg;
%type<al> arg_list;
%type<at> atom;
%type<ex> expr;
%type<el> expr_list;
%type<ty> type;
%type<st> stmt;
%type<sl> stmt_list;
%type<fd> fdef;
%type<fc> fcall;

%start init

%left '+' '-'
%left '*' '/' '%'
%right R_POW
%left UNEG UPOS

%%

type: R_BOOL   { $$ = new_type(TYPE_BOOL); }
    | R_DOUBLE { $$ = new_type(TYPE_DOUBLE); }
    | R_INT    { $$ = new_type(TYPE_INT); }
    ;

arg: type ID { $$ = new_arg($1, $2); }
   ;

arg_list:                   { $$ = NULL; }
         | arg              { $$ = new_arg_list(NULL, $1); }
         | arg_list ',' arg { $$ = new_arg_list($1, $3); }
         ;

expr: expr '+' expr       { $$ = new_expr(EXP_ADD, $1, $3, NULL); }
    | expr '-' expr       { $$ = new_expr(EXP_SUB, $1, $3, NULL); }
    | expr '*' expr       { $$ = new_expr(EXP_MUL, $1, $3, NULL); }
    | expr '/' expr       { $$ = new_expr(EXP_DIV, $1, $3, NULL); }
    | expr '%' expr       { $$ = new_expr(EXP_MOD, $1, $3, NULL); }
    | expr R_POW expr     { $$ = new_expr(EXP_POW, $1, $3, NULL); }
    | '(' expr ')'        { $$ = new_expr(EXP_ASSOC, $2, NULL, NULL); }
    | '-' expr %prec UNEG { $$ = new_expr(EXP_UNEG, $2, NULL, NULL); }
    | '+' expr %prec UPOS { $$ = new_expr(EXP_UPOS, $2, NULL, NULL); }
    | atom                { $$ = new_expr(EXP_AT, NULL, NULL, $1); }
    ;

expr_list:                    { $$ = NULL; }
         | expr               { $$ = new_expr_list(NULL, $1); }
         | expr_list ',' expr { $$ = new_expr_list($1, $3); }
         ;

atom: STRING { $$ = new_atom(AT_STRING, $1, NULL); }
    | INT    { $$ = new_atom(AT_INTEGER, $1, NULL); }
    | FLOAT  { $$ = new_atom(AT_FLOAT, $1, NULL); }
    | CHAR   { $$ = new_atom(AT_CHAR, $1, NULL); }
    | ID     { $$ = new_atom(AT_VAR, $1, NULL); }
    | fcall  { $$ = new_atom(AT_FCALL, NULL, $1); }
    ;

fdef: type ID '(' arg_list ')' '{' stmt_list '}' { $$ = new_fdef($1, $2, $4, $7); }
    ;

fcall: ID '(' expr_list ')' { $$ = new_fcall($1, $3); }
     ;

stmt: fdef     { $$ = new_stmt(STMT_FDEF, $1, NULL); }
    | expr ';' { $$ = new_stmt(STMT_EXP, NULL, $1); }
    ;

stmt_list: stmt           { $$ = new_stmt_list(NULL, $1); }
         | stmt_list stmt { $$ = new_stmt_list($1, $2); }
         ;

init: stmt_list { result = $1; }
