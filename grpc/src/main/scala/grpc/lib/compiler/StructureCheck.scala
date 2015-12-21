package grpc.lib.compiler

import grpc.lib.internal.GrpParser._
import grpc.lib.symbol.{Location, Type}

class StructureCheck extends CompilerPhase {
  var insideLoop = false
  var insideVoidFunction = false
  var fname = ""

  def controlStmtError(location: Location, word: String) = {
    addError(location, s"`$word` not inside a loop".toString)
  }

  def nonEmptyReturnError(location: Location) = {
    addError(location, s"non-empty return in void function `$fname`".toString)
  }

  override def enterForc(ctx: ForcContext) = {
    super.enterForc(ctx)
    insideLoop = true
  }

  override def enterWhilec(ctx: WhilecContext) = {
    super.enterWhilec(ctx)
    insideLoop = true
  }

  override def exitForc(ctx: ForcContext) = {
    super.exitForc(ctx)
    insideLoop = false
  }

  override def exitWhilec(ctx: WhilecContext) = {
    super.exitWhilec(ctx)
    insideLoop = false
  }

  override def enterContinue(ctx: ContinueContext) = {
    super.enterContinue(ctx)
    if (!insideLoop) {
      controlStmtError(new Location(ctx.getStart), "continue")
    }
  }


  override def enterBreak(ctx: BreakContext) = {
    super.enterBreak(ctx)
    if (!insideLoop) {
      controlStmtError(new Location(ctx.getStart), "break")
    }
  }

  override def enterFdef(ctx: FdefContext) = {
    super.enterFdef(ctx)

    if (getType(ctx.typ()) == Type.void) {
      insideVoidFunction = true
      fname = ctx.Identifier().getText
    } else {
      insideVoidFunction = false
      fname = ""
    }
  }

  override def exitFdef(ctx: FdefContext) = {
    super.exitFdef(ctx)
    insideVoidFunction = false
    fname = ""
  }

  override def enterReturn(ctx: ReturnContext) = {
    super.enterReturn(ctx)
    if (insideVoidFunction && ctx.expr() != null) {
      nonEmptyReturnError(new Location(ctx.getStart))
    }
  }
}
