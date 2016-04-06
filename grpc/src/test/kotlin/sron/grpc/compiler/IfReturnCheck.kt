package sron.grpc.compiler

import org.junit.Test
import sron.grpc.exception.ErrorsInCodeException
import sron.grpc.test.TestCompiler
import kotlin.test.assertFailsWith

class IfReturnCheck {
    @Test
    fun test() {
        val badSource =
                """
                func some() int {
                    var a int = 3;
                    if (a > 0) {
                        return 1;
                    } elif (a < 0) {
                        return -1;
                    } else {
                    }
                }
                func main() int {}
                """
        var compiler = TestCompiler(badSource)

        assertFailsWith<ErrorsInCodeException> {
            compiler.compile()
        }
        assert(compiler.errors.size == 1)
        assert(compiler.errors[0] is NotAllPathsReturn)

        var goodSource =
                """
                func some() int {
                    var a int = 3;
                    if (a > 0) {
                        return 1;
                    } elif (a < 0) {
                        return -1;
                    } else {
                        return 0;
                    }
                }
                func main() int {}
                """
        compiler = TestCompiler(goodSource)
        compiler.compile()

        assert(compiler.errors.size == 0)
    }
}

