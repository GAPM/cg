package main

import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.tree.ParseTreeWalker
import parser.Listener
import parser.internal.GrpLexer
import parser.internal.GrpParser
import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets

fun main(args: Array<String>) {
    val s = "2+2*4/5+9*2/1-7;";
    val inst = ByteArrayInputStream(s.toByteArray(StandardCharsets.UTF_8))

    val input = ANTLRInputStream(inst)
    val lexer = GrpLexer(input)
    val tokens = CommonTokenStream(lexer)
    val parser = GrpParser(tokens)

    val tree = parser.init()

    val walker = ParseTreeWalker()
    val listener = Listener()
    walker.walk(listener, tree)

    println(bothSideCrop("Simon"))
    println(findExec("gcc"))
}