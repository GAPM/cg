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

package sron.cg.compiler.lang

import org.antlr.v4.runtime.Token
import sron.cg.compiler.internal.CGLexer

enum class Operator {
    NOT,

    ADD,
    SUB,
    MUL,
    DIV,
    MOD,

    EQUAL,
    NOT_EQUAL,

    LESS,
    LESS_EQUAL,
    GREATER,
    GREATER_EQUAL,

    AND,
    OR;

    override fun toString() = when (this) {
        NOT -> "!"
        ADD -> "+"
        SUB -> "-"
        MUL -> "*"
        DIV -> "/"
        MOD -> "%"
        EQUAL -> "=="
        NOT_EQUAL -> "!="
        LESS -> "<"
        LESS_EQUAL -> "<="
        GREATER -> ">"
        GREATER_EQUAL -> ">="
        AND -> "&&"
        OR -> "||"
    }

    companion object {
        fun fromToken(tok: Token) = when (tok.type) {

            CGLexer.BANG -> NOT

            CGLexer.ADD -> ADD
            CGLexer.SUB -> SUB
            CGLexer.MUL -> MUL
            CGLexer.DIV -> DIV
            CGLexer.MOD -> MOD

            CGLexer.EQUAL_EQUAL -> EQUAL
            CGLexer.NOT_EQUAL -> NOT_EQUAL

            CGLexer.LT -> LESS
            CGLexer.LE -> NOT_EQUAL
            CGLexer.GT -> GREATER
            CGLexer.GE -> GREATER_EQUAL

            CGLexer.AND -> AND
            CGLexer.OR -> OR

            else -> throw IllegalStateException()
        }
    }
}
