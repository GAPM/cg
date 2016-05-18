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

grammar CG;

ADD : '+' ;
ADD_ASSIGN : '+=' ;
AND : '&&' ;
AND_ASSIGN : '&&=' ;
BOOL : 'bool' ;
BREAK : 'break' ;
COMMA : ',' ;
CONTINUE : 'continue' ;
DIGRAPH : 'digraph' ;
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
GRAPH : 'graph' ;
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
NOT : '!' ;
NOT_EQUAL : '!=' ;
OR : '||' ;
OR_ASSIGN : '||=' ;
RBRACE : '}' ;
RETURN : 'return' ;
RPAREN : ')' ;
SEMI : ';' ;
STRING : 'string' ;
SUB : '-' ;
SUB_ASSIGN : '-=' ;
VAR : 'var' ;
VOID : 'void' ;
WHILE : 'while' ;
LBRACK : '[' ;
RBRACK : ']' ;
WS: [ \t\r\n] -> skip;

BoolLit: 'true' | 'false';

fragment Letter: [a-zA-Z_];
fragment DecimalDigit: [0-9];

Identifier: Letter (Letter | DecimalDigit)*;

fragment DecimalLit: DecimalDigit+;
IntLit: DecimalLit;

fragment Exponent: [eE] [+-]? DecimalLit;
FloatLit: DecimalLit '.' DecimalLit Exponent?
                 | DecimalLit Exponent
                 | '.' DecimalLit Exponent?
                 ;

fragment Escape: '\\' [tbnr"'\\];
fragment Char: ~[\\'"];

StringLit: '"' (Escape | Char)* '"';

type: 'int'
    | 'float'
    | 'string'
    | 'void'
    | 'bool'
    | 'graph'
    | 'digraph'
    ;

edge: '[' source=expr ',' target=expr ']';
graphLit: gtype=('graph'|'digraph') '(' num=expr ')' '{' (edge (',' edge)*)? '}';

arg: Identifier type;
argList: (arg (',' arg)*)?;

atom: IntLit            #Integer
    | FloatLit          #Float
    | BoolLit           #Boolean
    | StringLit         #String
    | Identifier        #VarName
    | graphLit          #Graph
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

glExpr: IntLit | FloatLit | BoolLit | StringLit ;
glVarDec: 'var' Identifier type ('=' glExpr)? ;
varDec: 'var' Identifier type ('=' expr)? ;

funcDef: 'func' Identifier '(' argList ')' type? '{' stmt* '}' ;

funcCall: Identifier '(' exprList ')' ;

assignment: expr op=('='|'+='|'-='|'*='|'/='|'%='|'&&='|'||=') expr ;

ifc: 'if' '(' expr ')' '{' stmt* '}' elifc* elsec? ;
elifc: 'elif' '(' expr ')' '{' stmt* '}' ;
elsec: 'else' '{' stmt* '}' ;

forc: 'for' '(' initial=assignment ';' cond=expr ';' mod=assignment ')' '{' stmt* '}' ;
whilec: 'while' '(' expr ')' '{' stmt* '}' ;

controlStmt: wr=('continue'|'break') ;

returnStmt: 'return' expr? ;

compoundStmt: ifc
            | forc
            | whilec
            ;

simpleStmt: varDec
          | assignment
          | expr
          | returnStmt
          | controlStmt
          ;

stmt: simpleStmt ';'
    | compoundStmt
    ;

init: (funcDef | glVarDec ';')+;
