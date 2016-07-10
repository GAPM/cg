package sron.cg.compiler.phase

import sron.cg.compiler.State
import sron.cg.compiler.ast.Init

abstract class Phase {
    abstract fun execute(s: State, init: Init)
}
