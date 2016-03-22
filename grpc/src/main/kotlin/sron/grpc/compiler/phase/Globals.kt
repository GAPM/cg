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

package sron.grpc.compiler.phase

import sron.grpc.compiler.internal.GrpParser.*
import sron.grpc.compiler.toGrpType
import sron.grpc.symbol.Function
import sron.grpc.symbol.Location
import sron.grpc.symbol.SymType
import sron.grpc.symbol.Variable
import sron.grpc.type.Type

/**
 * [Globals] is the compiler phase where all global symbols (functions and
 * variables) are stored in the symbol table.
 */
class Globals : Phase() {
    private var insideSimpleStmt = false

    /**
     * Reports a global function or variable redeclaration
     *
     * @param last The location of the redeclaration
     * @param first The location of the previously declared symbol
     * @param name The name of the symbol
     * @param symType  Whether it is a function or a variable
     */
    fun redeclarationError(last: Location, first: Location, name: String,
                           symType: SymType) {
        val t = when (symType) {
            SymType.VAR -> "variable"
            SymType.FUNC -> "function"
        }

        val m = "redeclaration of $t `$name`. Previously declared at $first"
        addError(last, m)
    }

    /**
     * Reports that a variable or argument is being declared with type void
     *
     * @param location The location of the declaration
     * @param name The name of the variable or argument
     * @param t "variable" or "argument"
     */
    fun voidVarError(location: Location, name: String, t: String) =
            addError(location, "$t $name declared with type `void`")

    /**
     * Marks whenever the phase enters in a simple statement. Variables being
     * declared inside a simple statement are not global.
     */
    override fun enterSimpleStmt(ctx: SimpleStmtContext) {
        super.enterSimpleStmt(ctx)
        insideSimpleStmt = true
    }

    /**
     * Marks whenever the phase leaves a simple statement. Variables being
     * declared inside a simple statement are not global.
     */
    override fun exitSimpleStmt(ctx: SimpleStmtContext) {
        super.exitSimpleStmt(ctx)
        insideSimpleStmt = false
    }

    /**
     * Inserts a function and its arguments into the symbol table.
     */
    override fun exitFuncDef(ctx: FuncDefContext) {
        super.exitFuncDef(ctx)

        val name = ctx.Identifier().text
        val type = ctx.type()?.toGrpType() ?: Type.void
        val location = Location(ctx.Identifier())

        val ar = ctx.argList().arg()

        scope.push(name)
        val scopeStr = scopeUID()
        scope.pop()

        val args = Array(ar.size) {
            val a = ar[it]
            val argName = a.Identifier().text
            val argType = a.type().toGrpType()
            val argLoc = Location(a.Identifier())

            if (argType == Type.void) {
                voidVarError(argLoc, argName, "argument")
            }

            Variable(argName, argType, scopeStr, argLoc)
        }

        val function = Function(name, scopeUID(), type, location, *args)

        val qry = symTab.getSymbol(name, SymType.FUNC)
        when (qry) {
            null -> {
                symTab.addSymbol(function)
                for (v in args) {
                    symTab.addSymbol(v)
                }
            }
            else -> {
                redeclarationError(location, qry.location, name, SymType.FUNC)
            }
        }
    }

    /**
     * Inserts a global variable into the symbol table.
     */
    override fun exitVarDec(ctx: VarDecContext) {
        super.exitVarDec(ctx)

        if (!insideSimpleStmt) {
            val name = ctx.Identifier().text
            val type = ctx.type().toGrpType()
            val location = Location(ctx.Identifier())

            if (type == Type.void) {
                voidVarError(location, name, "variable")
                return
            }

            val scope = scopeUID()
            val variable = Variable(name, type, scope, location)

            val qry = symTab.getSymbol(name, scope, SymType.VAR)
            when (qry) {
                null -> symTab.addSymbol(variable)
                else ->
                    redeclarationError(location, qry.location, name, SymType.VAR)
            }
        }
    }
}
