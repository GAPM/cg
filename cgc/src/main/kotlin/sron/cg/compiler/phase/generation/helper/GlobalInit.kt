package sron.cg.compiler.phase.generation.helper

import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Label
import org.objectweb.asm.Opcodes.*
import sron.cg.compiler.ast.GlVarDec
import sron.cg.type.Type
import sron.cg.type.descriptor

fun initializer(cw: ClassWriter, glVarDec: List<GlVarDec>) {
    val mv = cw.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null)
    mv.visitCode()

    val ls = Label()
    mv.visitLabel(ls)

    for (gvd in glVarDec) {
        if (gvd.type == Type.graph || gvd.type == Type.digraph) {
            pushDefaultToStack(mv, gvd.type)
            mv.visitFieldInsn(PUTSTATIC, "EntryPoint", gvd.name, gvd.type.descriptor())
        }
    }

    val le = Label()
    mv.visitLabel(le)

    mv.visitInsn(RETURN)
    mv.visitMaxs(0, 0)
    mv.visitEnd()
}
