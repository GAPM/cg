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

package sron.grpc.compiler

import sron.grpc.symbol.Location
import sron.grpc.symbol.SymType
import sron.grpc.type.Type

inline private fun <T> v(block: () -> T): T {
    return block()
}

abstract class Error(private val last: Location) {
    abstract val msg: String

    fun message() = v {
        "$last: $msg"
    }
}

class Redeclaration(last: Location, first: Location, name: String, symType: SymType) : Error(last) {
    override val msg = v {
        val t = when (symType) {
            SymType.FUNC -> "function"
            SymType.VAR -> "variable"
        }

        "redeclaration of $t `$name`. Previously declared at $first"
    }
}

class VoidVar(location: Location, name: String, t: String) : Error(location) {
    override val msg = "$t $name declared with type `void`"
}

class NoEntryPoint(location: Location) : Error(location) {
    override val msg = "No main method defined"
}

class ControlNotInLoop(location: Location, word: String) : Error(location) {
    override val msg = "`$word` not inside a loop"
}

class NonEmptyReturn(location: Location, fName: String) : Error(location) {
    override val msg = "non-empty return in function `$fName`"
}

class EmptyReturn(location: Location, fName: String) : Error(location) {
    override val msg = "empty return in non-void function `$fName`"
}

class NotAllPathsReturn(location: Location, fName: String) : Error(location) {
    override val msg = "in function $fName: not all paths have a return"
}

class NotFound(location: Location, name: String, typ: SymType) : Error(location) {
    override val msg = v {
        val t = if (typ == SymType.FUNC) "function" else "variable"
        "$t $name not found in current scope"
    }
}

class BadUnaryOp(location: Location, op: String, typ: Type) : Error(location) {
    override val msg = "invalid unary operation: $op $typ"
}

class BadBinaryOp(location: Location, op: String, typ1: Type, typ2: Type) : Error(location) {
    override val msg = "invalid binary operation: $typ1 $op $typ2"
}

class Argument(location: Location, expr: String, etype: Type, atype: Type, fName: String) : Error(location) {
    override val msg = "can not use $expr (type $etype) as type $atype in argument to $fName"
}

class ArgumentNumber(location: Location, type: Char, fName: String) : Error(location) {
    override val msg = when (type) {
        '+' -> "too many arguments in call to $fName"
        else -> "not enough arguments in call to $fName"
    }
}

class NonAssignable(location: Location, exp: String) : Error(location) {
    override val msg = "can not assign to $exp"
}

class BadAssignment(location: Location, exp: String, type1: Type, type2: Type) : Error(location) {
    override val msg = "can not use $exp (type $type2) as type $type1 in assignment"
}

class NonBoolCondition(location: Location, exp: String, type: Type) : Error(location) {
    override val msg = "can not use $exp (type $type) as type bool in condition"
}
