package grpc
package lib
package compiler
package phase

import grpc.lib.compiler.internal.GrpParser._
import grpc.lib.symbol.SymType.SymType
import grpc.lib.symbol.Type.Type
import grpc.lib.symbol._
import org.antlr.v4.runtime.ParserRuleContext

/**
  * `Types` is the compiler phase where all type errors are found. Type errors
  * are a special type of error, because they are fatal. Compilation terminates
  * after at least one type error.
  */
class Types extends Phase {
  private var insideSimpleStmt = false
  private var scope = "global"

  /**
    * Retrieves the type of a (sub)parse tree
    *
    * @param ctx The parse tree
    * @return The type of the parse tree, `Type.none` if it does not exists
    */
  def getType(ctx: ParserRuleContext): Type = {
    val r = results.get(ctx)

    if (r != null) {
      return r.getType
    }

    Type.none
  }

  /**
    * Sets the type of a (sub)parse tree, if the parse tree does not have an
    * entry in the result map, it's created
    *
    * @param ctx The parse tree
    * @param typ The context of the type to be assigned
    */
  def setType(ctx: ParserRuleContext, typ: Type) {
    var r = results.get(ctx)

    if (r == null) {
      r = new UnitResult
    }

    r.setType(typ)
    results.put(ctx, r)
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
  def notFoundError(location: Location, name: String, typ: SymType) {
    val t = if (typ == SymType.FUNC) "function" else "variable"
    addError(location, s"$t $name not found".toString)
  }

  /**
    * Reports a type mismatch error while using an unary operator
    *
    * @param location The location of the error
    * @param op The operator
    * @param typ The type
    */
  def unaryOpError(location: Location, op: String, typ: Type) {
    addError(location, s"Invalid operation: $op $typ".toString)
  }

  /**
    * Reports a type mismatch error while using an binary operator
    *
    * @param location The location of the error
    * @param op The operator
    * @param typ1 The type of the left operand
    * @param typ2 The type of the right operand
    */
  def binaryOpError(location: Location, op: String, typ1: Type, typ2: Type) {
    addError(location, s"Invalid operation: $typ1 $op $typ2".toString)
  }

  /**
    * Reports a type mismatch in a function call regarding one of the parameters
    *
    * @param location The location of the function call
    * @param expr The text of the expression with mismatched type
    * @param exprType The type of the expression
    * @param argType The type expected by te argument
    * @param fName The function name
    */
  def argumentError(location: Location, expr: String, exprType: Type,
                    argType: Type, fName: String) {
    val msg = s"can not use $expr (type $exprType) as type $argType " +
      s"in argument to $fName".toString
    addError(location, msg)
  }

  /**
    * Reports an argument number mismatch in function call error
    *
    * @param location The location of the error
    * @param typ The type of error: `+` for too many arguments, `-` for not
    *            enough arguments
    * @param fName The function name
    */
  def argumentNumberError(location: Location, typ: Char, fName: String) {
    val t = typ match {
      case '+' => s"Too many arguments in call to $fName"
      case '-' => s"Not enough arguments in call to $fName"
    }
    addError(location, t.toString)
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

  /**
    * Checks that a variable being used was declared already
    *
    * @param ctx The context of the variable being used
    */
  override def exitVarName(ctx: VarNameContext) {
    super.exitVarName(ctx)

    val name = ctx.Identifier().getText
    val location = new Location(ctx.Identifier())

    val qry = symbolTable.getSymbol(name, scope, SymType.VAR)
    qry match {
      case Some(s) =>
        val v = s.asInstanceOf[Variable]
        setType(ctx, v.getType)
      case None =>
        notFoundError(location, name, SymType.VAR)
        setType(ctx, Type.error)
    }
  }

  /**
    * Sets the type of an integer literal to the machine dependant integer type
    *
    * @param ctx The context of the integer literal
    */
  override def exitInteger(ctx: IntegerContext) {
    super.exitInteger(ctx)

    getMachineArch match {
      case 64 => setType(ctx, Type.int64)
      case 32 => setType(ctx, Type.int32)
    }
  }

  /**
    * Sets the type of an unsigned integer literal parse tree to the machine
    * dependant unsigned integer type
    *
    * @param ctx The context of the integer literal
    */
  override def exitUInteger(ctx: UIntegerContext) {
    super.exitUInteger(ctx)

    getMachineArch match {
      case 64 => setType(ctx, Type.uint64)
      case 32 => setType(ctx, Type.uint32)
    }
  }

  /**
    * Sets the type of an float literal parse tree to the machine dependant
    * float type
    *
    * @param ctx The context of the float literal
    */
  override def exitFloat(ctx: FloatContext) {
    super.exitFloat(ctx)

    getMachineArch match {
      case 64 => setType(ctx, Type.double)
      case 32 => setType(ctx, Type.float)
    }
  }

  /**
    * Sets the type of a boolean literal parse tree to the bool type
    *
    * @param ctx The context of the boolean literal
    */
  override def exitBoolean(ctx: BooleanContext) {
    super.exitBoolean(ctx)
    setType(ctx, Type.bool)
  }

  /**
    * Sets the type of a character literal parse tree to the char type
    *
    * @param ctx The context of the char literal
    */
  override def exitCharacter(ctx: CharacterContext) {
    super.exitCharacter(ctx)
    setType(ctx, Type.char)
  }

  /**
    * Sets the type of a string literal parse tree to the string type
    *
    * @param ctx The context of the string literal
    */
  override def exitStringAtom(ctx: StringAtomContext) {
    super.exitStringAtom(ctx)
    setType(ctx, Type.string)
  }

  override def exitFcall(ctx: FcallContext) {
    super.exitFcall(ctx)

    val name = ctx.Identifier().getText
    val location = new Location(ctx.Identifier())
    var error = false

    val qry = symbolTable.getSymbol(name, SymType.FUNC)
    qry match {
      case Some(s) =>
        val f = s.asInstanceOf[Function]
        val args = f.getArgs
        val expr = ctx.exprList().expr()

        if (args.size > expr.size) {
          argumentNumberError(location, '-', name)
          setType(ctx, Type.error)
        } else if (args.size < expr.size) {
          argumentNumberError(location, '+', name)
          setType(ctx, Type.error)
        } else {
          for (i <- args.indices) {
            val typ1 = args(i).getType
            val typ2 = getType(expr.get(i))
            val exp = expr.get(i).getText

            if (typ1 == Type.error || typ2 == Type.error) {
              setType(ctx, Type.error)
              return
            }

            if (typ1 != typ2) {
              argumentError(location, exp, typ2, typ1, name)
              error = true
            }
          }

          setType(ctx, if (error) Type.error else f.getRetType)
        }

      case None =>
        notFoundError(location, name, SymType.FUNC)
        setType(ctx, Type.error)
    }
  }

  override def exitCast(ctx: CastContext) {
    val typ1 = tokIdxToDataType(ctx.typ())
    val typ2 = getType(ctx.expr())

    if (CastTable.check(typ1, typ2)) {
      setType(ctx, typ1)
    } else {
      setType(ctx, Type.error)
    }

    super.exitCast(ctx)
  }

  override def exitAtomic(ctx: AtomicContext) {
    super.exitAtomic(ctx)
    results.put(ctx, results.get(ctx.atom()))
  }

  override def exitUnary(ctx: UnaryContext) {
    super.exitUnary(ctx)

    val op = ctx.op.getText
    val typ = getType(ctx.expr())
    val location = new Location(ctx.op)

    if (typ != Type.error) {
      op match {
        case "-" | "+" =>
          if (!isNumeric(typ)) {
            unaryOpError(location, op, typ)
            setType(ctx, Type.error)
          } else {
            setType(ctx, typ)
          }
        case "!" =>
          if (typ != Type.bool) {
            unaryOpError(location, op, typ)
            setType(ctx, Type.error)
          } else {
            setType(ctx, Type.bool)
          }
      }
    } else {
      setType(ctx, Type.error)
    }
  }

  override def exitAssoc(ctx: AssocContext) {
    super.exitAssoc(ctx)
    results.put(ctx, results.get(ctx.expr()))
  }

  override def exitMulDivMod(ctx: MulDivModContext) {
    super.exitMulDivMod(ctx)

    val typ1 = getType(ctx.expr(0))
    val typ2 = getType(ctx.expr(1))
    val op = ctx.op.getText
    val location = new Location(ctx.getStart)

    if (typ1 != Type.error && typ2 != Type.error) {
      if (typ1 != typ2) {
        binaryOpError(location, op, typ1, typ2)
        setType(ctx, Type.error)
      } else {
        setType(ctx, typ1)
      }
    } else {
      setType(ctx, Type.error)
    }
  }

  override def exitAddSub(ctx: AddSubContext) {
    super.exitAddSub(ctx)

    val typ1 = getType(ctx.expr(0))
    val typ2 = getType(ctx.expr(1))
    val op = ctx.op.getText
    val location = new Location(ctx.getStart)

    if (typ1 != Type.error && typ2 != Type.error) {
      if (typ1 != typ2) {
        binaryOpError(location, op, typ1, typ2)
        setType(ctx, Type.error)
      } else {
        setType(ctx, typ1)
      }
    } else {
      setType(ctx, Type.error)
    }
  }
}
