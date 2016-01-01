package grpc
package lib

import grpc.lib.compiler.internal.GrpLexer
import grpc.lib.compiler.internal.GrpParser.TypContext
import grpc.lib.symbol.Type
import grpc.lib.symbol.Type.Type
import org.antlr.v4.runtime.tree.TerminalNode

package object compiler {

  /**
    * Calculates the JVM architecture.
    *
    * @return An integer representing the JVM architecture
    */
  def getMachineArch: Int = {
    val arch = System.getProperty("os.arch")
    if (arch.contains("64")) 64 else 32
  }

  /**
    * Pairs a type context with the corresponding supported type
    *
    * @param ctx The type context
    * @return A value of the `Type` enumeration corresponding to the context
    */
  def tokIdxToDataType(ctx: TypContext): Type = {
    val tn = ctx.getChild(0).asInstanceOf[TerminalNode]
    val t = tn.getSymbol.getType

    t match {
      case GrpLexer.INT => if (getMachineArch == 64) Type.int64 else Type.int32
      case GrpLexer.INT8 => Type.int8
      case GrpLexer.INT16 => Type.int16
      case GrpLexer.INT32 => Type.int32
      case GrpLexer.INT64 => Type.int64
      case GrpLexer.FLOAT => Type.float
      case GrpLexer.DOUBLE => Type.double
      case GrpLexer.UINT => if (getMachineArch == 64) Type.uint64 else Type.uint32
      case GrpLexer.UINT8 => Type.uint8
      case GrpLexer.UINT16 => Type.uint16
      case GrpLexer.UINT32 => Type.uint32
      case GrpLexer.UINT64 => Type.uint64
      case GrpLexer.BOOL => Type.bool
      case GrpLexer.VOID => Type.void
      case GrpLexer.STRING => Type.string
      case GrpLexer.CHAR => Type.char
      case _ => Type.none
    }
  }

  def isNumeric(typ: Type): Boolean = typ match {
    case Type.bool | Type.char | Type.string => false
    case _ => true
  }
}
