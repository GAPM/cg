package grpc.lib.compiler;

import grpc.lib.internal.GrpParser;
import grpc.lib.symbol.Location;

public class Structure extends CompilerPhase {
    private boolean insideLoop;

    public Structure() {
        insideLoop = false;
    }

    public void structureError(Location location, String word) {
        String e = String.format("`%s` not inside a loop", word);
        addError(location, e);
    }

    @Override
    public void enterForc(GrpParser.ForcContext ctx) {
        super.enterForc(ctx);
        insideLoop = true;
    }

    @Override
    public void enterWhilec(GrpParser.WhilecContext ctx) {
        super.enterWhilec(ctx);
        insideLoop = true;
    }

    @Override
    public void exitForc(GrpParser.ForcContext ctx) {
        super.exitForc(ctx);
        insideLoop = false;
    }

    @Override
    public void exitWhilec(GrpParser.WhilecContext ctx) {
        super.exitWhilec(ctx);
        insideLoop = false;
    }

    @Override
    public void enterContinue(GrpParser.ContinueContext ctx) {
        super.enterContinue(ctx);
        if (!insideLoop) {
            structureError(new Location(ctx.getStart()), "continue");
        }
    }

    @Override
    public void enterBreak(GrpParser.BreakContext ctx) {
        super.enterBreak(ctx);
        if (!insideLoop) {
            structureError(new Location(ctx.getStart()), "break");
        }
    }
}
