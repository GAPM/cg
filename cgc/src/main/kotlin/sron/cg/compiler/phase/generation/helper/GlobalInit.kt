package sron.cg.compiler.phase.generation.helper

import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.*
import sron.cg.compiler.ast.GlVarDec
import sron.cg.type.descriptor
import sron.cg.type.name
import sron.cg.type.Type

fun initGraph(mv: MethodVisitor, gvd: GlVarDec) {
    val typeName = gvd.type.name()
    val typeDes = gvd.type.descriptor()
    mv.visitTypeInsn(NEW, typeName)
    mv.visitInsn(DUP)
    mv.visitInsn(ICONST_0)
    mv.visitMethodInsn(INVOKESPECIAL, typeName, "<init>", "(I)V", false)
    mv.visitFieldInsn(PUTSTATIC, "EntryPoint", gvd.name, typeDes)
}

fun initializer(cw: ClassWriter, glVarDec: List<GlVarDec>) {
    val mv = cw.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null)
    mv.visitCode()

    val ls = Label()
    mv.visitLabel(ls)

    for (gvd in glVarDec) {
        if (gvd.type == Type.graph || gvd.type == Type.digraph) {
            initGraph(mv, gvd)
        }
    }

    val le = Label()
    mv.visitLabel(le)

    mv.visitInsn(RETURN)
    mv.visitMaxs(0, 0)
    mv.visitEnd()
}