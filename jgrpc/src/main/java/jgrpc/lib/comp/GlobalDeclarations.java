package jgrpc.lib.comp;

import jgrpc.lib.internal.GrpLexer;
import jgrpc.lib.internal.GrpParser;
import jgrpc.lib.sym.*;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.Optional;

public class GlobalDeclarations extends CompilerPhase {
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

        // Get the function info
        String name = ctx.Identifier().getText();
        Type rType = getType(ctx.type());
        Location location = new Location(ctx.Identifier());

        // Search for a function with the same name and scope
        Optional<Symbol> qry = symbolTable.getSymbol(name, "global", SymType.FUNC);
        if (!qry.isPresent()) {
            Function function = new Function(name, rType, "global", location);
            symbolTable.addSymbol(function);
        } else {
            // Error reporting
            Function f = (Function) qry.get();
            String e = String.format("Redeclaration of function `%s`. ", name);
            e += String.format("Previosly declared at %s", f.location);
            addError(location, e);
        }
    }

    @Override
    public void exitVdec(GrpParser.VdecContext ctx) {
        super.exitVdec(ctx);

        String name = ctx.Identifier().getText();
        Type type = getType(ctx.type());
        Location location = new Location(ctx.Identifier());

        Optional<Symbol> qry = symbolTable.getSymbol(name, "global", SymType.VAR);
        if (!qry.isPresent()) {
            Variable variable = new Variable(name, type, "global", location);
            symbolTable.addSymbol(variable);
        } else {
            Variable v = (Variable) qry.get();
            String e = String.format("Redeclaration of global var `%s`. ", name);
            e += String.format("Previosly declared at %s", v.location);
            addError(location, e);
        }
    }
}
