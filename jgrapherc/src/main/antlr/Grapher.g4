grammar Grapher;

INCLUDE : 'include' ;
LPAREN : '(' ;
RPAREN : ')' ;
SEMI : ';' ;
INT8 : 'int8' ;
INT16 : 'int16' ;
INT32 : 'int32' ;
INT64 : 'int64' ;
FLOAT : 'float' ;
DOUBLE : 'double' ;
UINT32 : 'uint32' ;
UINT64 : 'uint64' ;
CHAR : 'char' ;
STRING : 'string' ;
GRAPH : 'graph' ;
WGRAPH : 'wgraph' ;
DIGRAPH : 'digraph' ;
WDIGRAPH : 'wdigraph' ;
VOID : 'void' ;
BOOL : 'bool' ;
LBRACK : '[' ;
RBRACK : ']' ;
MAP : 'map' ;
MUL : '*' ;
VAR : 'var' ;
DOT : '.' ;
COMMA : ',' ;
ADD : '+' ;
SUB : '-' ;
POW : '**' ;
DIV : '/' ;
MOD : '%' ;
BITAND : '&' ;
BITXOR : '^' ;
BITOR : '|' ;
RETURN : 'return' ;
BREAK : 'break' ;
CONTINUE : 'continue' ;
WS: [ \t\r\n] -> skip;

fragment Letter: [a-zA-Z_];
fragment DecimalDigit: [0-9];
fragment OctalDigit: [0-7];
fragment HexDigit: [0-9a-fA-F];

Identifier: Letter (Letter | DecimalDigit)*;

fragment DecimalLit: [1-9] DecimalDigit*;
fragment OctalLit: '0' OctalDigit*;
fragment HexLit: '0' [xX] HexDigit*;
IntLit: DecimalLit | OctalLit | HexLit;

fragment Decimals: DecimalDigit DecimalDigit*;
fragment Exponent: [eE] [+-]? Decimals;
FloatLit: Decimals '.' Decimals Exponent?
                 | Decimals Exponent
                 | '.' Decimals Exponent?
                 ;

CharLit: '\'' . '\'';
StringLit: '"' (.)*? '"';

singleInclude: 'include' StringLit;
multiInclude: 'include' '(' StringLit+ ')';
includes: (singleInclude | multiInclude) ';';

primitiveType: 'int8'
             | 'int16'
             | 'int32'
             | 'int64'
             | 'float'
             | 'double'
             | 'uint32'
             | 'uint64'
             | 'char'
             | 'string'
             | 'graph'
             | 'wgraph'
             | 'digraph'
             | 'wdigraph'
             | 'void'
             | 'bool'
             ;
arrayType: '[' IntLit ']' type;
mapType: 'map' '[' type ']' type;
pointerType: '*' type;
type: primitiveType
    | arrayType
    | pointerType
    | mapType
    | Identifier
    ;

decl: 'var' Identifier type;

selector: '.' Identifier #Property
        | '.' funcCall #Method
        ;
index: '[' IntLit ']';

literal: StringLit
       | IntLit
       | FloatLit
       | CharLit
       ;

paramsList: (expr_stmt (',' expr_stmt)*)?;
funcCall: Identifier '(' paramsList ')';

atom: Identifier #Identifier
    | literal #LiteralAtom
    | '(' arith_expr ')' #Assoc
    ;

atom_expr: atom selector #AtomSelector
         | atom index #AtomIndex
         | atom #AtomSmall
         ;

factor_expr: '+' atom_expr #Possitive
           | '-' atom_expr #Negative
           | atom_expr #AtomExpr
           ;

pow_expr: pow_expr '**' factor_expr
        | factor_expr
        ;

term_expr: term_expr '*' pow_expr #Mul
         | term_expr '/' pow_expr #Div
         | term_expr '%' pow_expr #Mod
         | pow_expr #GL
         ;

arith_expr: arith_expr '+' term_expr #Sum
          | arith_expr '-' term_expr #Sub
          | term_expr #FL
          ;

and_expr: and_expr '&' arith_expr
        | arith_expr
        ;

xor_expr: xor_expr '^' and_expr
        | and_expr
        ;

expr_stmt: expr_stmt '|' xor_expr
         | xor_expr
         ;

return_stmt: 'return' expr_stmt?;
break_stmt: 'break';
continue_stmt: 'continue';

flow_stmt: break_stmt
         | continue_stmt
         | return_stmt
         ;

simple_stmt: expr_stmt
           | flow_stmt
           ;

