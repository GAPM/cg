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

package sron.cg.compiler

import sron.cg.compiler.ast.Operator
import sron.cg.compiler.ast.sign
import sron.cg.symbol.Location
import sron.cg.symbol.SymType
import sron.cg.type.Type

object Error {
    private fun e(location: Location, msg: String): String {
        return "$location: $msg"
    }

    fun argument(location: Location, etype: Type, atype: Type, fName: String): String {
        return e(location, "can not use expression of type $etype as type $atype in argument to `$fName`")
    }

    fun argumentNumber(location: Location, type: Char, fName: String): String {
        val msg = when (type) {
            '+' -> "too many arguments in call to `$fName`"
            else -> "not enough arguments in call to `$fName`"
        }

        return e(location, msg)
    }

    fun badAssignment(location: Location, lhs: Type, rhs: Type): String {
        return e(location, "can't use expression of type $lhs as type $rhs in assignment")
    }

    fun badBinaryOp(location: Location, op: Operator, typ1: Type, typ2: Type): String {
        return e(location, "invalid binary operation: $typ1 ${op.sign()} $typ2")
    }

    fun badUnaryOp(location: Location, op: Operator, typ: Type): String {
        return e(location, "invalid unary operation: ${op.sign()} $typ")
    }

    fun badReturn(location: Location, type: Type, funcType: Type, fName: String): String {
        return e(location, "can't use type $type as type $funcType in return at function $fName")
    }

    fun callingEntryPoint(location: Location): String {
        return e(location, "the entry point is not callable")
    }

    fun cast(location: Location, target: Type, current: Type): String {
        return e(location, "invalid cast from `$current` to `$target`")
    }

    fun emptyReturn(location: Location, fName: String): String {
        return e(location, "empty return in non-void function `$fName`")
    }

    fun noEntryPoint(location: Location): String {
        return e(location, "No main method defined")
    }

    fun nonAssignable(location: Location): String {
        return e(location, "non-assignable expression")
    }

    fun nonBoolCondition(location: Location, type: Type): String {
        return e(location, "can not use type $type as type bool in condition")
    }

    fun nonEmptyReturn(location: Location, fName: String): String {
        return e(location, "non-empty return in void function `$fName`")
    }

    fun nonIntegerNode(location: Location): String {
        return e(location, "node indexes must be integers")
    }

    fun nonIntegerSize(location: Location): String {
        return e(location, "graph size must be an integer")
    }

    fun notAllPathsReturn(location: Location, fName: String): String {
        return e(location, "in function `$fName`: not all paths have a return")
    }

    fun notFound(location: Location, name: String, typ: SymType): String {
        val t = if (typ == SymType.FUNC) "function" else "variable"
        return e(location, "$t `$name` not found in current scope")
    }

    fun redeclaration(last: Location, first: Location, name: String, symType: SymType): String {
        val t = when (symType) {
            SymType.FUNC -> "function"
            SymType.VAR -> "variable"
        }

        return e(last, "redeclaration of $t `$name`. Previously declared at $first")
    }

    fun outOfRange(location: Location, exp: String): String {
        return e(location, "value $exp is out of range")
    }

    fun voidVar(location: Location, name: String): String {
        return e(location, "variable `$name` declared with type `void`")
    }
}
