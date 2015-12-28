package grpc.lib.compiler

import grpc.lib.internal.GrpParser.{FdefContext, SimpleStmtContext, VarNameContext, VdecContext}
import grpc.lib.symbol.{Location, SymType, Type, Variable}
import org.antlr.v4.runtime.ParserRuleContext

/**
  * TypeCheck is the compiler phase where all type errors are found. Type errors
  * are a special type of error, because they are fatal. Compilation terminates
  * after at least one type error.
  */
class TypeCheck extends CompilerPhase {
  private var insideSimpleStmt = false
  private var scope = "global"

  /**
    * Sets the type of a (sub)parse tree, if the parse tree does not have an
    * entry in the result map, it gets created
    *
    * @param ctx the parse tree
    * @param typ the context of the type to be assigned
    */
  def setType(ctx: ParserRuleContext, typ: Type.Value) {
    var r = results.get(ctx)

    if (r == null) {
      r = new UnitResult
      results.put(ctx, r)
    }

    r.setType(typ)
  }

  /**
    * Reports that a variable is being re-declared in a common scope
    *
    * @param last The re-declaration location
    * @param first The location of the already declared variable
    * @param name The name of the variable
    */
  def redeclarationError(last: Location, first: Location, name: String) {
    val e = s"Redeclaration of variable `$name`." +
      s"Previously declared at $first".toString
    addError(last, e)
  }

  /**
    * Reports that a function or variable that is not in the symbol table is
    * being used
    *
    * @param location the location of the use
    * @param name the name of the symbol
    * @param typ the symbol type
    */
  def notFoundError(location: Location, name: String, typ: SymType.Value) {
    val t = if (typ == SymType.FUNC) "function" else "variable"
    addError(location, s"$t $name not found".toString)
  }

  /**
    * Updates the scope whenever the phase enters a function definition
    *
    * @param ctx The context of the function definition
    */
  override def enterFdef(ctx: FdefContext) {
    super.enterFdef(ctx)
    scope = ctx.Identifier().getText
  }

  /**
    * Updates the scope to `"global"` whenever the phase leaves a function
    * definition
    *
    * @param ctx The context of the function definition
    */
  override def exitFdef(ctx: FdefContext) {
    super.exitFdef(ctx)
    scope = "global"
  }

  /**
    * Marks whenever the phase enters a simple statement. Variable declarations
    * inside simple statements are not global/
    *
    * @param ctx The context of the simple statement
    */
  override def enterSimpleStmt(ctx: SimpleStmtContext) {
    super.enterSimpleStmt(ctx)
    insideSimpleStmt = true
  }

  /**
    * Marks whenever the phase leaves a simple statement. Variable declarations
    * inside simple statements are not global/
    *
    * @param ctx The context of the simple statement
    */
  override def exitSimpleStmt(ctx: SimpleStmtContext) {
    super.exitSimpleStmt(ctx)
    insideSimpleStmt = false
  }

  /**
    * Checks that the variable being declared is not declared already
    *
    * @param ctx The context of the variable declaration
    */
  override def exitVdec(ctx: VdecContext) {
    super.exitVdec(ctx)

    if (insideSimpleStmt) {
      val name = ctx.Identifier().getText
      val typ = tokIdxToDataType(ctx.typ())
      val location = new Location(ctx.Identifier())

      val variable = new Variable(name, typ, scope, location)
      val qry = symbolTable.getSymbol(name, scope, SymType.VAR)

      qry match {
        case Some(v) => redeclarationError(location, v.getLocation, name)
        case None => symbolTable.addSymbol(variable)
      }
    }
  }

  override def exitVarName(ctx: VarNameContext) {
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
