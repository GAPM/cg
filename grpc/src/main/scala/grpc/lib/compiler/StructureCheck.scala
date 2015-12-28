package grpc.lib.compiler

import grpc.lib.internal.GrpParser._
import grpc.lib.symbol.{Location, Type}
import org.antlr.v4.runtime.ParserRuleContext

import scala.util.control.Breaks._

/**
  * Compilation phase that checks that all non-void functions return a value,
  * that void functions have only empty returns and that control statements such
  * as `continue` and `break` are used only inside loops.
  */
class StructureCheck extends CompilerPhase {
  private var insideLoop = false
  private var insideFunction = false
  private var currentFunctionType = Type.none
  private var fName = ""

  /**
    * Modifies the `returns`value of a `UnitResult` associated with a parse tree
    *
    * @param ctx The context of the parse tree
    * @param v returns or not
    */
  def setReturns(ctx: ParserRuleContext, v: Boolean) {
    var r = results.get(ctx)

    if (r == null) {
      r = new UnitResult
      results.put(ctx, r)
    }

    r.setReturns(v)
  }

  /**
    * Retrieves whether a parse tree contains or is a return
    * @param ctx the context of the parse tree
    * @return `true` if it contains or is a return, `false` otherwise
    */
  def getReturns(ctx: ParserRuleContext): Boolean = {
    val r = results.get(ctx)

    if (r == null) {
      return false
    }

    r.getReturns
  }

  /**
    * Reports that `continue` or `break` were used outside a loop
    *
    * @param location The location of the error
    * @param word `continue` or `break`
    */
  private def controlStmtError(location: Location, word: String) {
    addError(location, s"`$word` not inside a loop".toString)
  }

  /**
    * Reports that a non-empty return is inside a `void` function
    *
    * @param location The location of the error
    */
  private def nonEmptyReturnError(location: Location) {
    addError(location, s"non-empty return in void function `$fName`".toString)
  }

  private def notAllPathsReturnError(location: Location) {
    addError(location, s"in function $fName: not all paths have a return".toString)
  }

  /**
    * Marks that the phase entered inside a loop (`for`)
    *
    * @param ctx The context of the `for` loop
    */
  override def enterForc(ctx: ForcContext) {
    super.enterForc(ctx)
    insideLoop = true
  }

  /**
    * Marks that the phase entered inside a loop (`while`)
    *
    * @param ctx The context of the `while` loop
    */
  override def enterWhilec(ctx: WhilecContext) {
    super.enterWhilec(ctx)
    insideLoop = true
  }

  /**
    * Marks that the phase leaved a loop (`for`)
    *
    * @param ctx The context of the `for` loop
    */
  override def exitForc(ctx: ForcContext) {
    super.exitForc(ctx)
    insideLoop = false
  }

  /**
    * Marks that the phase leaved a loop (`while`)
    *
    * @param ctx The context of the `while` loop
    */
  override def exitWhilec(ctx: WhilecContext) {
    super.exitWhilec(ctx)
    insideLoop = false
  }

  /**
    * Checks whenever the phase enters a `continue` statement that it is inside
    * a loop
    *
    * @param ctx The `continue` statement context
    */
  override def enterContinue(ctx: ContinueContext) {
    super.enterContinue(ctx)
    if (!insideLoop) {
      controlStmtError(new Location(ctx.getStart), "continue")
    }
  }

  /**
    * Checks whenever the phase enters a `break` statement that it is inside
    * a loop
    *
    * @param ctx The `break` statement context
    */
  override def enterBreak(ctx: BreakContext) {
    super.enterBreak(ctx)
    if (!insideLoop) {
      controlStmtError(new Location(ctx.getStart), "break")
    }
  }

  /**
    * Marks that the phase entered a function definition, saving its name and
    * return type
    *
    * @param ctx The context of the function definition
    */
  override def enterFdef(ctx: FdefContext) {
    super.enterFdef(ctx)

    insideFunction = true
    currentFunctionType = tokIdxToDataType(ctx.typ())
    fName = ctx.Identifier().getText
  }

  /**
    * Marks that the phase leaved a function definition, checking that all paths
    * returns a value (unless its type is `void`) and removing current name and
    * return type
    *
    * @param ctx The context of the function definition
    */
  override def exitFdef(ctx: FdefContext) {
    super.exitFdef(ctx)

    for (i <- 0 until ctx.stmt().size()) {
      val s = ctx.stmt(i)

      if (getReturns(s)) {
        setReturns(ctx, v = true)
      }
    }

    if (!getReturns(ctx) && currentFunctionType != Type.void && fName != "main") {
      notAllPathsReturnError(new Location(ctx.Identifier()))
    }

    insideFunction = false
    currentFunctionType = Type.none
    fName = ""
  }

  /**
    * Checks whenever the phase leaves an if statement, if all branches have a
    * return statement.
    *
    * @param ctx The context of the if statement
    */
  override def exitIfc(ctx: IfcContext) {
    super.exitIfc(ctx)

    var elifCount = 0
    var mainIfReturns = false
    var allElifReturns = true
    var elseReturns = false

    breakable {
      for (i <- 0 until ctx.stmt().size()) {
        val s = ctx.stmt(i)
        if (getReturns(s)) {
          mainIfReturns = true
          break
        }
      }
    }

    for (i <- 0 until ctx.elifc().size()) {
      elifCount += 1
      var thisElifReturns = false

      for (j <- 0 until ctx.elifc(i).stmt().size()) {
        val s = ctx.elifc(i).stmt(j)
        if (thisElifReturns || getReturns(s)) {
          thisElifReturns = true
        }
      }

      allElifReturns &&= thisElifReturns
    }

    breakable {
      for (i <- 0 until ctx.elsec().stmt().size()) {
        val s = ctx.elsec().stmt(i)
        if (getReturns(s)) {
          elseReturns = true
          break
        }
      }
    }

    setReturns(ctx, mainIfReturns && allElifReturns && elseReturns)
  }

  /**
    * Marks that a return statement is indeed a return statement (used to check
    * that all paths in a function have a return) and checks if a void function
    * has empty returns.
    *
    * @param ctx The context of the return statement
    */
  override def enterReturn(ctx: ReturnContext) {
    super.enterReturn(ctx)
    setReturns(ctx, v = true)

    if (currentFunctionType == Type.void && ctx.expr() != null) {
      nonEmptyReturnError(new Location(ctx.getStart))
    }
  }

  /**
    * Sets the result of a simple statement to the same result of its used child
    *
    * @param ctx The context of the simple statement
    */
  override def exitSimpleStmt(ctx: SimpleStmtContext) {
    super.exitSimpleStmt(ctx)

    if (ctx.vdec() != null) {
      results.put(ctx, results.get(ctx.vdec()))
    }

    if (ctx.assign() != null) {
      results.put(ctx, results.get(ctx.assign()))
    }

    if (ctx.controlStmt() != null) {
      results.put(ctx, results.get(ctx.controlStmt()))
    }

    if (ctx.expr() != null) {
      results.put(ctx, results.get(ctx.expr()))
    }
  }

  /**
    * Sets the result of a compound statement to the same result of its used
    * child
    *
    * @param ctx The context of the compound statement
    */
  override def exitCompoundStmt(ctx: CompoundStmtContext) {
    super.exitCompoundStmt(ctx)

    if (ctx.ifc() != null) {
      results.put(ctx, results.get(ctx.ifc()))
    }

    if (ctx.forc() != null) {
      results.put(ctx, results.get(ctx.forc()))
    }

    if (ctx.whilec() != null) {
      results.put(ctx, results.get(ctx.whilec()))
    }
  }

  /**
    * Sets the result of a statement to the same result of its used child
    *
    * @param ctx The context of the statement
    */
  override def exitStmt(ctx: StmtContext) {
    super.exitStmt(ctx)

    if (ctx.simpleStmt() != null) {
      results.put(ctx, results.get(ctx.simpleStmt()))
    }

    if (ctx.compoundStmt() != null) {
      results.put(ctx, results.get(ctx.compoundStmt()))
    }
  }
}
