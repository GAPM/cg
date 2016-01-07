package grpc.lib.exception

class ErrorsInCodeException(c: Int) :
        CompilerException("$c error(s) found in code. Won't continue")
