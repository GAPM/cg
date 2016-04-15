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

package sron.cgpl.compiler

import sron.cgpl.symbol.Location
import sron.cgpl.symbol.SymType
import sron.cgpl.type.Type

object Error {
    private fun e(location: Location, msg: String): String {
        return "$location: $msg"
    }

    fun redeclaration(last: Location, first: Location, name: String, symType: SymType): String {
        val t = when (symType) {
            SymType.FUNC -> "function"
            SymType.VAR -> "variable"
        }

        return e(last, "redeclaration of $t `$name`. Previously declared at $first")
    }

    fun voidVar(location: Location, name: String, t: String): String {
        return e(location, "$t `$name`g declared with type `void`")
    }

    fun noEntryPoint(location: Location): String {
        return e(location, "No main method defined")
    }

    fun nonEmptyReturn(location: Location, fName: String): String {
        return e(location, "non-empty return in void function `$fName`")
    }

    fun emptyReturn(location: Location, fName: String): String {
        return e(location, "empty return in non-void function `$fName`")
    }

    fun notAllPathsReturn(location: Location, fName: String): String {
        return e(location, "in function `$fName`: not all paths have a return")
    }

    fun notFound(location: Location, name: String, typ: SymType): String {
        val t = if (typ == SymType.FUNC) "function" else "variable"
        return e(location, "$t `$name` not found in current scope")
    }

    fun badUnaryOp(location: Location, op: String, typ: Type): String {
        return e(location, "invalid unary operation: $op $typ")
    }

    fun badBinaryOp(location: Location, op: String, typ1: Type, typ2: Type): String {
        return e(location, "invalid binary operation: $typ1 $op $typ2")
    }

    fun argument(location: Location, expr: String, etype: Type, atype: Type, fName: String): String {
        return e(location, "can not use $expr (type $etype) as type $atype in argument to `$fName`")
    }

    fun argumentNumber(location: Location, type: Char, fName: String): String {
        val msg = when (type) {
            '+' -> "too many arguments in call to `$fName`"
            else -> "not enough arguments in call to `$fName`"
        }

        return e(location, msg)
    }

    fun nonAssignable(location: Location, exp: String): String {
        return e(location, "can not assign to $exp")
    }

    fun badAssignment(location: Location, exp: String, type1: Type, type2: Type): String {
        return e(location, "can not use $exp (type $type2) as type $type1 in assignment")
    }

    fun nonBoolCondition(location: Location, exp: String, type: Type): String {
        return e(location, "can not use $exp (type $type) as type bool in condition")
    }

    fun outOfRange(location: Location, exp: String): String {
        return e(location, "value $exp is out of range")
    }

    fun castError(location: Location, target: Type, current: Type): String {
        return e(location, "invalid cast from `$current` to `$target`")
    }
}
