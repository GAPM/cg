package jgrpc.lib;

import jgrpc.lib.ex.GrpPhase;
import jgrpc.lib.internal.GrpLexer;
import jgrpc.lib.internal.GrpParser;
import jgrpc.lib.sym.Type;
import org.antlr.v4.runtime.tree.TerminalNode;

public class GlobalDeclarations extends GrpPhase {
    public Type getType(GrpParser.TypeContext ctx) {
        TerminalNode tn = (TerminalNode) ctx.getChild(0);
        int t = tn.getSymbol().getType();

        switch (t) {
            case GrpLexer.INT8:
                return Type.INT8;
            case GrpLexer.INT32:
                return Type.INT32;
            case GrpLexer.INT64:
                return Type.INT64;
            case GrpLexer.FLOAT:
                return Type.FLOAT;
            case GrpLexer.DOUBLE:
                return Type.DOUBLE;
            case GrpLexer.UINT8:
                return Type.UINT8;
            case GrpLexer.UINT32:
                return Type.UINT32;
            case GrpLexer.UINT64:
                return Type.UINT64;
            case GrpLexer.BOOL:
                return Type.BOOL;
            case GrpLexer.VOID:
                return Type.VOID;
            default:
                return Type.ERROR;
        }
    }

    @Override
    public void exitFdef(GrpParser.FdefContext ctx) {
        super.exitFdef(ctx);

        String name = ctx.Identifier().getText();
        Type ret_type = getType(ctx.type());
        System.out.println(name);
        System.out.println(ret_type.toString());
    }
}
