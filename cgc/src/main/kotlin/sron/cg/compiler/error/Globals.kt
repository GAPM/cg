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

package sron.cg.compiler.error

import sron.cg.compiler.ast.*
import sron.cg.compiler.symbol.Function
import sron.cg.compiler.symbol.Variable

class ExpressionForbidden(vd: VarDec) : Error {
    override val msg =
            """
            |${vd.location}:
            |  initialization expression forbidden for global variables
            """.trimMargin()
}

class FunctionRedefinition(fd: FuncDef, existing: Function) : Error {
    override val msg =
            """
            |${fd.location}:
            |  redefinition of function `${fd.id}´ with signature ${fd.signature}
            |  already defined at ${existing.location}
            """.trimMargin()
}

class GlobalVarMissingType(vd: VarDec) : Error {
    override val msg =
            """
            |${vd.location}:
            |  definition of global variable `${vd.id}´ lacks type
            """.trimMargin()
}

class ParameterRedefinition(param: Parameter, variable: Variable) : Error {
    override val msg =
            """
            |${param.location}:
            |  redefinition of parameter `${param.id}´
            |  already defined at ${variable.location}
            """.trimMargin()
}

class VariableRedeclaration(vd: VarDec, existing: Variable) : Error {
    override val msg =
            """
            |${vd.location}:
            |  redeclaration of variable `${vd.id}´ in the same scope
            |  already declared at ${existing.location}
            """.trimMargin()
}
