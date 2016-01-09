/*
 * Copyright 2016 Simón Oroño
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
ELIF : 'elif' ;
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
ML_COMMENT : '/*' (.)*? '*/' -> skip ;
MOD : '%' ;
MUL : '*' ;
NOT_EQUAL : '!=' ;
OR : '||' ;
RBRACE : '}' ;
RETURN : 'return' ;
RPAREN : ')' ;
SEMI : ';' ;
STRING : 'string' ;
SUB : '-' ;
UINT : 'uint' ;
UINT16 : 'uint16' ;
UINT32 : 'uint32' ;
UINT64 : 'uint64' ;
UINT8 : 'uint8' ;
VAR : 'var' ;
VOID : 'void' ;
WHILE : 'while' ;
WS: [ \t\r\n] -> skip;

BoolLit: 'true' | 'false';

fragment Letter: [a-zA-Z_];
fragment DecimalDigit: [0-9];
fragment OctalDigit: [0-7];
fragment HexDigit: [0-9a-fA-F];

Identifier: Letter (Letter | DecimalDigit)*;

fragment DecimalLit: [0-9] DecimalDigit*;
fragment OctalLit: '0' [oO] OctalDigit+;
fragment HexLit: '0' [xX] HexDigit+;
IntLit: DecimalLit | OctalLit | HexLit;
UIntLit: DecimalLit 'u';

fragment Decimals: DecimalDigit DecimalDigit*;
fragment Exponent: [eE] [+-]? Decimals;
DoubleLit: Decimals '.' Decimals Exponent?
                 | Decimals Exponent
                 | '.' Decimals Exponent?
                 ;
FloatLit: (DoubleLit | DecimalLit) 'f';

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
   ;

arg: Identifier typ;
argList: (arg (',' arg)*)?;

atom: IntLit            #Integer
    | UIntLit           #UInteger
    | FloatLit          #Float
    | DoubleLit         #Double
    | BoolLit           #Boolean
    | CharLit           #Character
    | StringLit         #StringAtom
    | Identifier        #VarName
    | fcall             #FunctionCall
    | typ '(' expr ')'  #Cast
    ;

expr: atom                                #Atomic
    | <assoc=right> op=('-'|'+'|'!') expr #Unary
    | '(' expr ')'                        #Assoc
    | expr op=('*'|'/'|'%') expr          #MulDivMod
    | expr op=('+'|'-') expr              #AddSub
    | expr op=('>'|'<'|'>='|'<=') expr    #Comparison
    | expr op=('=='|'!=') expr            #Equality
    | expr '&&' expr                      #LogicAnd
    | expr '||' expr                      #LogicOr
    ;
exprList: (expr (',' expr)*)?;

vdec: 'var' Identifier typ ('=' expr)?;

fdef: typ Identifier '(' argList ')' '{' stmt* '}';

fcall: Identifier '(' exprList ')';

assign: expr op=('='|'+='|'*='|'/='|'%='|'&&='|'||=') expr;

ifc: 'if' '(' expr ')' '{' stmt* '}' elifc* elsec?;
elifc: 'elif' '(' expr ')' '{' stmt* '}';
elsec: 'else' '{' stmt* '}';

forc: 'for' '(' start=assign? ';' cond=expr? ';' mod=assign? ')' '{' stmt* '}';
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
