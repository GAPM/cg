package grpc.lib.compiler

import grpc.lib.internal.GrpParser.{FdefContext, SimpleStmtContext, VdecContext}
import grpc.lib.symbol.{Function, Location, SymType, Variable}

import scala.collection.mutable.ListBuffer

class GlobalDeclarations extends CompilerPhase {
  private var insideSimpleStmt = false

  /**
    * Reports a global function or variable redeclaration
    *
    * @param last  The location of the redeclaration
    * @param first The location of the previously declared symbol
    * @param name  The name of the symbol
    * @param typ  Whether it is a function or a variable
    */
  def redeclarationError(last: Location, first: Location, name: String,
                         typ: SymType.Value) = {
    val t = typ match {
      case SymType.FUNC => "function with same signature"
      case _ => "global variable"
    }

    val m = s"Redeclaration of $t `$name`. Previously declared at $first"
    addError(last, m.toString)
  }

  /**
    * Marks whenever the phase enters in a simple statement. Variables being
    * declared inside a simple statement are not global.
    *
    * @param ctx The context of the statement
    */
  override def enterSimpleStmt(ctx: SimpleStmtContext) = {
    super.enterSimpleStmt(ctx)
    insideSimpleStmt = true
  }

  /**
    * Marks whenever the phase leaves a simple statement. Variables being
    * declared inside a simple statement are not global
    *
    * @param ctx The context of the statement
    */
  override def exitSimpleStmt(ctx: SimpleStmtContext) = {
    super.exitSimpleStmt(ctx)
    insideSimpleStmt = false
  }

  /**
    * Inserts a function and its arguments into the symbol table
    *
    * @param ctx The context of the function definition
    */
  override def exitFdef(ctx: FdefContext) = {
    super.exitFdef(ctx)

    val name = ctx.Identifier().getText
    val returnType = getType(ctx.typ())
    val location = new Location(ctx.Identifier())
    val args = ListBuffer.empty[Variable]

    val ar = ctx.argList().arg()
    for (i <- 0 until ar.size()) {
      val argName = ar.get(i).Identifier().getText
      val argTyp = getType(ar.get(i).typ())
      val argLoc = new Location(ar.get(i).Identifier())
      val v = new Variable(argName, argTyp, name, argLoc)
      args += v
    }

    val function = new Function(name, returnType, location, args)

    val qry = symbolTable.getSymbol(name, SymType.FUNC)
    qry match {
      case Some(f) => redeclarationError(location, f.getLocation, name, SymType.FUNC)
      case _ => symbolTable.addSymbol(function)
    }
  }

  /**
    * Inserts a global variable into the symbol table
    *
    * @param ctx The context of the variable declaration
    */
  override def exitVdec(ctx: VdecContext) = {
    super.exitVdec(ctx)

    if (!insideSimpleStmt) {
      val name = ctx.Identifier().getText
      val typ = getType(ctx.typ())
      val location = new Location(ctx.Identifier())

      val variable = new Variable(name, typ, "global", location)

      val qry = symbolTable.getSymbol(name, "global", SymType.VAR)
      qry match {
        case Some(v) => redeclarationError(location, v.getLocation, name, SymType.VAR)
        case _ => symbolTable.addSymbol(variable)
      }
    }
  }
}
