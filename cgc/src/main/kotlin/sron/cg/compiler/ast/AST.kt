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

package sron.cg.compiler.ast

import sron.cg.compiler.symbol.Location
import sron.cg.compiler.type.Type

abstract class ASTNode(val location: Location)

class Arg(val name: String, val type: Type, location: Location) : ASTNode(location)

class Edge(val source: Expr, val target: Expr, location: Location) : ASTNode(location)

class FuncDef(val name: String, val type: Type, val args: List<Arg>,
              val stmts: List<Stmt>, location: Location) : ASTNode(location) {
    var scope: String = ""
}

class GlExpr(val type: Type, val text: String, location: Location) : ASTNode(location)

class GlVarDec(val name: String, val type: Type, val exp: GlExpr?,
               location: Location) : ASTNode(location)

class Init(val glVarDec: List<GlVarDec>, val funcDef: List<FuncDef>) : ASTNode(Location(0))
