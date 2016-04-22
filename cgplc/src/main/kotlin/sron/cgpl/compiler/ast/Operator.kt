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

package sron.cgpl.compiler.ast

enum class Operator {
    ADD,
    SUB,
    MUL,
    DIV,
    MOD,

    AND,
    OR,

    NOT,
    MINUS,
    PLUS,

    LESS,
    LESS_EQUAL,
    HIGHER,
    HIGHER_EQUAL,

    EQUAL,
    NOT_EQUAL
}

fun Operator.sign(): String = when (this) {
    Operator.ADD -> "+"
    Operator.SUB -> "-"
    Operator.MUL -> "*"
    Operator.DIV -> "/"
    Operator.MOD -> "%"
    Operator.AND -> "&&"
    Operator.OR -> "||"
    Operator.NOT -> "!"
    Operator.MINUS -> "-"
    Operator.PLUS -> "+"
    Operator.LESS -> "<"
    Operator.LESS_EQUAL -> "<="
    Operator.HIGHER -> ">"
    Operator.HIGHER_EQUAL -> ">="
    Operator.EQUAL -> "=="
    Operator.NOT_EQUAL -> "!="
}
