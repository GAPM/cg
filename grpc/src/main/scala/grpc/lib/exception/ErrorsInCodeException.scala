package grpc
package lib
package exception

class ErrorsInCodeException(c: Int)
  extends CompilerException(s"$c error(s) found in code. Won't continue".toString)
