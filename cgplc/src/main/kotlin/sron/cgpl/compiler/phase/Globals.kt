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

package sron.cgpl.compiler.phase

import sron.cgpl.compiler.NoEntryPoint
import sron.cgpl.compiler.Redeclaration
import sron.cgpl.compiler.VoidVar
import sron.cgpl.compiler.internal.GrpParser.*
import sron.cgpl.symbol.Function
import sron.cgpl.symbol.Location
import sron.cgpl.symbol.SymType
import sron.cgpl.symbol.Variable
import sron.cgpl.type.Type
import sron.cgpl.type.toGrpType

/**
 * [Globals] is the compiler phase where all global symbols (functions and
 * variables) are stored in the symbol table.
 */
class Globals : Phase() {

    /**
     * Inserts a function and its arguments into the symbol table.
     */
    override fun exitFuncDef(ctx: FuncDefContext) {
        val scopeStr = scopeUID() // scope was updated from `Scoper`
        super.exitFuncDef(ctx) // now we let `Scoper` update the scope

        val name = ctx.Identifier().text
        val type = ctx.type()?.toGrpType() ?: Type.void
        val location = Location(ctx.Identifier())

        val ar = ctx.argList().arg()
        val args = Array(ar.size) {
            val a = ar[it]
            val argName = a.Identifier().text
            val argType = a.type().toGrpType()
            val argLoc = Location(a.Identifier())

            if (argType == Type.void) {
                error(VoidVar(argLoc, argName, "argument"))
            }

            Variable(argName, argType, scopeStr, argLoc)
        }

        val function = Function(name, scopeUID(), type, location, *args)

        val qry = symTab.getSymbol(name, SymType.FUNC)
        when (qry) {
            null -> {
                symTab.addSymbol(function)
                args.forEach { symTab.addSymbol(it) }
            }
            else -> {
                error(Redeclaration(location, qry.location, name, SymType.FUNC))
            }
        }
    }

    /**
     * Inserts a global variable into the symbol table.
     */
    override fun exitGlVarDec(ctx: GlVarDecContext) {
        super.exitGlVarDec(ctx)

        val name = ctx.Identifier().text
        val type = ctx.type().toGrpType()
        val location = Location(ctx.Identifier())

        if (type == Type.void) {
            error(VoidVar(location, name, "variable"))
            return
        }

        val scope = scopeUID()
        val variable = Variable(name, type, scope, location)

        val qry = symTab.getSymbol(name, scope, SymType.VAR)
        when (qry) {
            null -> symTab.addSymbol(variable)
            else ->
                error(Redeclaration(location, qry.location, name, SymType.FUNC))
        }
    }

    /**
     * After adding all the functions to the symbol table, check if a main
     * function was declared.
     */
    override fun exitInit(ctx: InitContext) {
        super.exitInit(ctx)
        val location = Location(0)

        val qry = symTab.getSymbol("main", scopeUID(), SymType.FUNC)
        when (qry) {
            null -> error(NoEntryPoint(location))
            else -> {
                val function = qry as Function
                if (function.type != Type.int) {
                    error(NoEntryPoint(location))
                }
            }
        }
    }
}
