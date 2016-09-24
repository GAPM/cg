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

/* Naming purpose only */
ADD : '+' ;
ADD_ASSIGN : '+=' ;
AND : '&&' ;
ASSERT : 'assert' ;
BANG : '!' ;
BOOL : 'bool' ;
BREAK : 'break' ;
CHAR : 'char' ;
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
LBRACK : '[' ;
LE : '<=' ;
LPAREN : '(' ;
LT : '<' ;
MOD : '%' ;
MOD_ASSIGN : '%=' ;
MUL : '*' ;
MUL_ASSIGN : '*=' ;
NOT_EQUAL : '!=' ;
OR : '||' ;
PRINT : 'print' ;
RBRACE : '}' ;
RBRACK : ']' ;
RETURN : 'return' ;
RPAREN : ')' ;
SEMI : ';' ;
STRING : 'string' ;
SUB : '-' ;
SUB_ASSIGN : '-=' ;
VAR : 'var' ;
VOID : 'void' ;
WHILE : 'while' ;

primitiveType: 'int'
             | 'float'
             | 'bool'
             | 'char'
             | 'string'
             | 'graph'
             | 'digraph'
             | 'void'
             ;
type: primitiveType
    | '[' type ']'
    ;

edge: '[' source=expr ',' target=expr ']';
graphLit: gtype=('graph'|'digraph') '[' num=expr ']' ('{' (edge (',' edge)*)? '}')?;

param: IDENTIFIER type;
paramList: (param (',' param)*)?;

lit: INT_LIT    #IntLit
   | FLOAT_LIT  #FloatLit
   | BOOL_LIT   #BoolLit
   | CHAR_LIT   #CharLit
   | STRING_LIT #StringLit
   ;

atom: lit               #Literal
    | IDENTIFIER        #VarName
    | graphLit          #Graph
    | funcCall          #FunctionCall
    | type '(' expr ')' #Cast
    | arrayLit          #Array
    ;

expr: atom                                #Atomic
    | array=expr '[' subscript=expr ']'   #ArrayAccess
    | <assoc=right> op=('-'|'+'|'!') expr #Unary
    | '(' expr ')'                        #Assoc
    | expr op=('*'|'/'|'%') expr          #MulDivMod
    | expr op=('+'|'-') expr              #AddSub
    | expr op=('>'|'<'|'>='|'<=') expr    #Comparison
    | expr op=('=='|'!=') expr            #Equality
    | expr op='&&' expr                   #LogicAnd
    | expr op='||' expr                   #LogicOr
    ;

exprList: (expr (',' expr)*)? ;
arrayLit: '{' exprList '}';

varDec: 'var' IDENTIFIER type? ('=' expr)? ;

funcDef: 'func' IDENTIFIER '(' paramList ')' type? '{' stmt* '}' ;

funcCall: IDENTIFIER '(' exprList ')' ;

assignmentStmt: lhs=expr op=('='|'+='|'-='|'*='|'/='|'%=') rhs=expr ;

ifc: 'if' '(' expr ')' '{' stmt* '}' elifc* elsec? ;
elifc: 'elif' '(' expr ')' '{' stmt* '}' ;
elsec: 'else' '{' stmt* '}' ;

forc: 'for' '(' initial=assignmentStmt ';' cond=expr ';' mod=assignmentStmt ')' '{' stmt* '}' ;
whilec: 'while' '(' expr ')' '{' stmt* '}' ;

controlStmt: wr=('continue'|'break') ;

returnStmt: 'return' expr? ;

printStmt: 'print' expr;

assertStmt: 'assert' expr;

compoundStmt: ifc
            | forc
            | whilec
            ;

simpleStmt: expr
          | assignmentStmt
          | varDec
          | returnStmt
          | controlStmt
          | printStmt
          | assertStmt
          ;

stmt: simpleStmt ';'
    | compoundStmt
    ;

unit: (funcDef | varDec ';')+;

/* Non-visible */
WHITESPACE: [ \t\r\n] -> skip;
COMMENT : '//' (.)*? '\n' -> skip;
ML_COMMENT : '/*' (.)*? '*/' -> skip ;

/* Literals */
BOOL_LIT: 'true' | 'false';

fragment LETTER: [a-zA-Z_];
fragment DIGIT: [0-9];

IDENTIFIER: LETTER (LETTER | DIGIT)*;

INT_LIT: DIGIT+;

fragment EXPONENT: [eE] [+-]? INT_LIT;
FLOAT_LIT: INT_LIT? '.' INT_LIT EXPONENT?;

fragment ESC: '\\' [tbnr"'\\];
fragment CHR: ~[\\'"];

CHAR_LIT: '\'' (ESC|CHR) '\'';
STRING_LIT: '"' (ESC|CHR)*? '"';
