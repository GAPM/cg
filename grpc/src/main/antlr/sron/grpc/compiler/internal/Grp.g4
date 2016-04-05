/*
 * Copyright 2016 Simón Oroño & La Universidad del Zulia
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
ADD_ASSIGN : '+=' ;
AND : '&&' ;
AND_ASSIGN : '&&=' ;
BANG : '!' ;
BOOL : 'bool' ;
BREAK : 'break' ;
CHAR : 'char' ;
COMMA : ',' ;
CONTINUE : 'continue' ;
DIV : '/' ;
DIV_ASSIGN : '/=' ;
ELIF : 'elif' ;
ELSE : 'else' ;
EQUAL : '=' ;
EQUAL_EQUAL : '==' ;
FLOAT : 'float' ;
FOR : 'for' ;
FUNC : 'func' ;
GE : '>=' ;
GT : '>' ;
IF : 'if' ;
INT : 'int' ;
LBRACE : '{' ;
LE : '<=' ;
LPAREN : '(' ;
LT : '<' ;
ML_COMMENT : '/*' (.)*? '*/' -> skip ;
MOD : '%' ;
MOD_ASSIGN : '%=' ;
MUL : '*' ;
MUL_ASSIGN : '*=' ;
NOT_EQUAL : '!=' ;
OR : '||' ;
OR_ASSIGN : '||=' ;
RBRACE : '}' ;
RETURN : 'return' ;
RPAREN : ')' ;
SEMI : ';' ;
STRING : 'string' ;
SUB : '-' ;
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
IntLit: DecimalLit;

fragment Decimals: DecimalDigit DecimalDigit*;
fragment Exponent: [eE] [+-]? Decimals;
FloatLit: Decimals '.' Decimals Exponent?
                 | Decimals Exponent
                 | '.' Decimals Exponent?
                 ;

fragment Escape: '\\' [tbnr"'\\];
fragment Char: ~[\\'"];
CharLit: '\'' (Escape | Char) '\'';

StringLit: '"' (Escape | Char)* '"';

type: 'int'
    | 'float'
    | 'char'
    | 'string'
    | 'void'
    | 'bool'
    ;

arg: Identifier type;
argList: (arg (',' arg)*)?;

atom: IntLit            #Integer
    | FloatLit          #Float
    | BoolLit           #Boolean
    | CharLit           #Character
    | StringLit         #String
    | Identifier        #VarName
    | funcCall          #FunctionCall
    | type '(' expr ')' #Cast
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
exprList: (expr (',' expr)*)? ;

glExpr: IntLit | FloatLit | BoolLit | CharLit | StringLit ;
glVarDec: 'var' Identifier type ('=' glExpr)? ;
varDec: 'var' Identifier type ('=' expr)? ;

funcDef: 'func' Identifier '(' argList ')' type? '{' stmt* '}' ;

funcCall: Identifier '(' exprList ')' ;

assignment: expr op=('='|'+='|'*='|'/='|'%='|'&&='|'||=') expr ;

ifc: 'if' '(' expr ')' '{' stmt* '}' elifc* elsec? ;
elifc: 'elif' '(' expr ')' '{' stmt* '}' ;
elsec: 'else' '{' stmt* '}' ;

forc: 'for' '(' start=assignment? ';' cond=expr? ';' mod=assignment? ')' '{' loopStmt* '}' ;
whilec: 'while' '(' expr ')' '{' (stmt | controlStmt)* '}' ;

controlStmt: 'continue' ';' #Continue
           | 'break' ';'    #Break
           ;

returnStmt: 'return' expr? ;

compoundStmt: ifc
            | forc
            | whilec
            ;

simpleStmt: varDec
          | assignment
          | expr
          | returnStmt
          ;

stmt: simpleStmt ';'
    | compoundStmt
    ;

loopStmt: (stmt | controlStmt) ';' ;

init: (funcDef | glVarDec ';')*;
