package grpc.lib.compiler;

import grpc.lib.internal.GrpLexer;
import grpc.lib.internal.GrpParser;
import grpc.lib.symbol.*;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.LinkedList;
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

    public void redeclarationError(Location first, Location last, String name, SymType type) {
        String t = (type == SymType.FUNC)
                ? "function with same signature"
                : "global variable";

        String e = String.format("Redeclaration of %s `%s`. ", t, name);
        e += String.format("Previously declared at %s", last);

        addError(first, e);
    }

    @Override
    public void exitFdef(GrpParser.FdefContext ctx) {
        super.exitFdef(ctx);

        // Get the function info
        String name = ctx.Identifier().getText();
        Type rType = getType(ctx.type());
        Location location = new Location(ctx.Identifier());
        LinkedList<Variable> args = new LinkedList<>();

        // The argument list gets populated
        for (GrpParser.ArgContext arg : ctx.argList().arg()) {
            String argName = arg.Identifier().getText();
            Type argType = getType(arg.type());
            Location argLoc = new Location(arg.Identifier());
            Variable v = new Variable(argName, argType, name, argLoc);
            args.add(v);
        }

        Function function = new Function(name, rType, location, args);

        // Search for a function with the same name and signature
        Symbol[] qry = symbolTable.getSymbols(name, SymType.FUNC);
        Location l = null;

        if (qry.length > 0) {
            for (Symbol s : qry) {
                Function f = (Function) s;
                if (function.equals(f)) {
                    l = f.getLocation();
                    break;
                }
            }
        }

        if (l != null) {
            redeclarationError(location, l, name, SymType.FUNC);
        } else {
            // Both the functions and the args are added to the symbol table
            symbolTable.addSymbol(function);
            for (Variable v : args) {
                symbolTable.addSymbol(v);
            }
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
            redeclarationError(location, v.getLocation(), name, SymType.VAR);
        }
    }
}
