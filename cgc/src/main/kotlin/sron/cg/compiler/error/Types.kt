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
import sron.cg.compiler.lang.Type
import sron.cg.compiler.symbol.Signature.Companion.signature
import sron.cg.compiler.symbol.Variable

class ArrayLiteralTypeMismatch(ac: ArrayLit) : Error {
    override val msg =
            """
            |${ac.location}:
            |  can not infer the type of the array literal
            """.trimMargin()
}

class AssignmentTypeMismatch(node: Node, lht: Type, rht: Type) : Error {
    override val msg =
            """
            |${node.location}:
            |  unexpected value of type $rht in assignment to type $lht
            """.trimMargin()
}

class CanNotInferType(vd: VarDec) : Error {
    override val msg =
            """
            |${vd.location}:
            |  can not infer type for variable ${vd.id}
            """.trimMargin()
}

class DifferentKindDec(vd: VarDec, existing: Variable) : Error {
    override val msg =
            """
            |${vd.location}:
            |  variable `${vd.id}´ redeclared with different kind
            |  already declared at ${existing.location}
            """.trimMargin()
}

class FunctionNotFound(fc: FunctionCall) : Error {
    override val msg =
            """
            |${fc.location}:
            |  function ${fc.id} with signature ${fc.args.signature()} not found
            """.trimMargin()
}

class InvalidBinaryExpr(be: BinaryExpr) : Error {
    override val msg =
            """
            |${be.location}:
            |  invalid binary operation (${be.op}) over operands of types ${be.lhs.type} and ${be.rhs.type}
            """.trimMargin()
}

class InvalidCast(c: Cast) : Error {
    override val msg =
            """
            |${c.location}:
            |  can not cast type ${c.expr.type} to type ${c.type}
            """.trimMargin()
}

class InvalidReturn(rt: Return) : Error {
    override val msg: String

    init {
        val fd = rt.funcDef
        val exprType = rt.expr!!.type
        msg = """
              |${rt.location}:
              |  invalid return expression of type $exprType in function of type ${fd.type}
              """.trimMargin()
    }
}

class InvalidUnaryExpr(ue: UnaryExpr) : Error {
    override val msg =
            """
            |${ue.location}:
            |  invalid unary operation (${ue.op}) over operand of type ${ue.expr.type}
            """.trimMargin()
}

class NonAssignableExpression(a: Assignment) : Error {
    override val msg =
            """
            |${a.location}:
            |  non-assignable left hand expression in assignment
            """.trimMargin()
}

class NonBoolCondition(expr: Expr, word: String) : Error {
    override val msg =
            """
            |${expr.location}:
            |  non-bool condition in $word statement
            """.trimMargin()
}

class NonIntegerNode(expr: Expr) : Error {
    override val msg =
            """
            |${expr.location}:
            |  node index not an integer
            """.trimMargin()
}

class NonIntegerSize(gl: GraphLit) : Error {
    override val msg =
            """
            |${gl.location}:
            |  size not an integer in graph literal
            """.trimMargin()
}

class SubscriptTypeNotInt(ac: ArrayAccess) : Error {
    override val msg =
            """
            |${ac.location}:
            |  invalid array subscript of type ${ac.subscript.type}
            """.trimMargin()
}

class TypeNotSubscriptable(ac: ArrayAccess) : Error {
    override val msg =
            """
            |${ac.location}:
            |  a value of type ${ac.array.type} is not subscriptable
            """.trimMargin()
}

class VariableNotFoundInScope(vn: VarName) : Error {
    override val msg =
            """
            |${vn.location}:
            |  variable ${vn.id} was not found in current scope
            """.trimMargin()
}
