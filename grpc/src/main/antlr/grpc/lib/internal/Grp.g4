grammar Grp;

ADD : '+' ;
AND : '&&' ;
BANG : '!' ;
BOOL : 'bool' ;
BREAK : 'break' ;
CHAR : 'char' ;
COMMA : ',' ;
CONTINUE : 'continue' ;
DIV : '/' ;
DOUBLE : 'double' ;
ELSE : 'else' ;
EQUAL : '=' ;
EQUAL_EQUAL : '==' ;
FLOAT : 'float' ;
FOR : 'for' ;
GE : '>=' ;
GT : '>' ;
IF : 'if' ;
INT : 'int' ;
INT16 : 'int16' ;
INT32 : 'int32' ;
INT64 : 'int64' ;
INT8 : 'int8' ;
LBRACE : '{' ;
LE : '<=' ;
LPAREN : '(' ;
LT : '<' ;
MOD : '%' ;
MUL : '*' ;
NOT_EQUAL : '!=' ;
OR : '||' ;
RBRACE : '}' ;
RETURN : 'return' ;
RPAREN : ')' ;
SEMI : ';' ;
STEP : 'step' ;
STRING : 'string' ;
SUB : '-' ;
POW : '**' ;
TO : 'to' ;
UINT : 'uint' ;
UINT16 : 'uint16' ;
UINT32 : 'uint32' ;
UINT64 : 'uint64' ;
UINT8 : 'uint8' ;
VAR : 'var' ;
VOID : 'void' ;
WHILE : 'while' ;
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

typ: 'int'
   | 'int8'
   | 'int16'
   | 'int32'
   | 'int64'
   | 'float'
   | 'double'
   | 'uint'
   | 'uint8'
   | 'uint16'
   | 'uint32'
   | 'uint64'
   | 'char'
   | 'string'
   | 'void'
   | 'bool'
   | 'string'
   | 'char'
   ;

arg: typ Identifier;
argList: (arg (',' arg)*)?;

atom: IntLit            #Integer
    | FloatLit          #FloatingPoint
    | BoolLit           #Boolean
    | CharLit           #Character
    | StringLit         #StringAtom
    | Identifier        #VarName
    | fcall             #FunctionCall
    | typ '(' expr ')'  #Cast
    ;

expr: atom                               #Atomic
    | <assoc=right>op=('-'|'+'|'!') expr #Unary
    | '(' expr ')'                       #Assoc
    | <assoc=right> expr '**' expr       #Pow
    | expr op=('*'|'/'|'%') expr         #MulDivMod
    | expr op=('+'|'-') expr             #AddSub
    | expr op=('>'|'<'|'>='|'<=') expr   #Comparison
    | expr op=('=='|'!=') expr           #Equality
    | expr '&&' expr                     #LogicAnd
    | expr '||' expr                     #LogicOr
    ;
exprList: (expr (',' expr)*)?;

vdec: 'var' Identifier typ ('=' expr)?;

fdef: typ Identifier '(' argList ')' '{' stmt* '}';

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

init: (fdef | vdec ';')*;
