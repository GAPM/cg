package grpc.lib.compiler

import grpc.lib.internal.GrpParser.{FdefContext, SimpleStmtContext, VarNameContext, VdecContext}
import grpc.lib.symbol.{Location, SymType, Type, Variable}
import org.antlr.v4.runtime.ParserRuleContext

class TypeCheck extends CompilerPhase {
  var insideSimpleStmt = false
  var scope = "global"

  def setType(ctx: ParserRuleContext, typ: Type.Value) = {
    var r = results.get(ctx)

    if (r == null) {
      r = new UnitResult
      results.put(ctx, r)
    }

    r.setType(typ)
  }

  def redeclarationError(last: Location, first: Location, name: String) = {
    val e = s"Redeclaration of variable `$name`." +
      s"Previously declared at $first".toString
    addError(last, e)
  }

  def notFoundError(location: Location, name: String, typ: SymType.Value) = {
    val t = if (typ == SymType.FUNC) "function" else "variable"
    addError(location, s"$t $name not found".toString)
  }

  override def enterFdef(ctx: FdefContext) = {
    super.enterFdef(ctx)
    scope = ctx.Identifier().getText
  }

  override def exitFdef(ctx: FdefContext) = {
    super.exitFdef(ctx)
    scope = "global"
  }

  override def enterSimpleStmt(ctx: SimpleStmtContext) = {
    super.enterSimpleStmt(ctx)
    insideSimpleStmt = true
  }

  override def exitSimpleStmt(ctx: SimpleStmtContext) = {
    super.exitSimpleStmt(ctx)
    insideSimpleStmt = false
  }

  override def exitVdec(ctx: VdecContext) = {
    super.exitVdec(ctx)

    if (insideSimpleStmt) {
      val name = ctx.Identifier().getText
      val typ = getType(ctx.typ())
      val location = new Location(ctx.Identifier())

      val variable = new Variable(name, typ, scope, location)
      val qry = symbolTable.getSymbol(name, scope, SymType.VAR)

      qry match {
        case Some(v) => redeclarationError(location, v.getLocation, name)
        case None => symbolTable.addSymbol(variable)
      }
    }
  }

  override def exitVarName(ctx: VarNameContext) = {
    super.exitVarName(ctx)

    val name = ctx.Identifier().getText
    val location = new Location(ctx.Identifier())

    val qry = symbolTable.getSymbol(name, scope, SymType.VAR)
    qry match {
      case Some(s) =>
        val v = s.asInstanceOf[Variable]
        setType(ctx, v.typ)
      case None => notFoundError(location, name, SymType.VAR)
    }
  }
}
