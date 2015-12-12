grammar Grp;

INT8 : 'int8' ;
INT16 : 'int16' ;
INT32 : 'int32' ;
INT64 : 'int64' ;
FLOAT : 'float' ;
DOUBLE : 'double' ;
UINT8 : 'uint8' ;
UINT16 : 'uint16' ;
UINT32 : 'uint32' ;
UINT64 : 'uint64' ;
CHAR : 'char' ;
STRING : 'string' ;
VOID : 'void' ;
BOOL : 'bool' ;
COMMA : ',' ;
SUB : '-' ;
ADD : '+' ;
LPAREN : '(' ;
RPAREN : ')' ;
POW : '**' ;
MUL : '*' ;
DIV : '/' ;
MOD : '%' ;
LBRACE : '{' ;
RBRACE : '}' ;
EQUAL : '=' ;
SEMI : ';' ;
VAR : 'var' ;
RETURN : 'return' ;
CONTINUE : 'continue' ;
BREAK : 'break' ;

WS: [ \t\r\n] -> skip;

fragment Letter: [a-zA-Z_];
fragment DecimalDigit: [0-9];
fragment OctalDigit: [0-7];
fragment HexDigit: [0-9a-fA-F];

Identifier: Letter (Letter | DecimalDigit)*;

BoolLit: 'true' | 'false';
fragment DecimalLit: [0-9] DecimalDigit*;
fragment OctalLit: '0' [oO] OctalDigit+;
fragment HexLit: '0' [xX] HexDigit+;
IntLit: DecimalLit | OctalLit | HexLit;

fragment Decimals: DecimalDigit DecimalDigit*;
fragment Exponent: [eE] [+-]? Decimals;
FloatLit: Decimals '.' Decimals Exponent?
                 | Decimals Exponent
                 | '.' Decimals Exponent?
                 ;

CharLit: '\'' . '\'';
StringLit: '"' (.)*? '"';

type: 'int8'
    | 'int16'
    | 'int32'
    | 'int64'
    | 'float'
    | 'double'
    | 'uint8'
    | 'uint16'
    | 'uint32'
    | 'uint64'
    | 'char'
    | 'string'
    | 'void'
    | 'bool'
    ;

arg: type Identifier;
argList: (arg (',' arg)*)?;

atom: IntLit
    | FloatLit
    | BoolLit
    | Identifier
    | fcall
    | type '(' expr ')'
    ;

expr: atom
    | '-' expr
    | '+' expr
    | '(' expr ')'
    | <assoc=right> expr '**' expr
    | expr ('*'|'/'|'%') expr
    | expr ('+'|'-') expr
    ;
exprList: (expr (',' expr)*)?;

vdec: 'var' Identifier type ('=' expr)?;

fdef: type Identifier '(' argList ')' '{' stmt* '}';

fcall: Identifier '(' exprList ')';

assign: expr '=' expr;

ifc: 'if' '(' expr ')' '{' stmt* '}' elsec?;
elsec: 'else' '{' stmt* '}';

forc: 'for' assign 'to' expr ('step' expr)? '{' stmt* '}';
whilec: 'while' '(' expr ')' '{' stmt* '}';

controlStmt: 'return' expr? #Return
           | 'continue'     #Continue
           | 'break'        #Break
           ;

compoundStmt: ifc
            | forc
            | whilec
            ;

simpleStmt: vdec
          | assign
          | controlStmt
          | expr
          ;

stmt: simpleStmt ';'
    | compoundStmt
    ;

init: (fdef | vdec ';')*
    ;
