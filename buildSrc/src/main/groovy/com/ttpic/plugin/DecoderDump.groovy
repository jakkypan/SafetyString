package com.ttpic.plugin

import jdk.internal.org.objectweb.asm.Opcodes
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor

import java.nio.charset.Charset

/**
 * Created by panda on 2018/12/25
 **/
public class DecoderDump extends Dump implements Opcodes {
    private String implementation
    private byte[] keyBytes

    DecoderDump(String implementation, String key) {
        this.implementation = implementation
        this.keyBytes = key.getBytes(Charset.forName("utf-8"))
    }

    @Override
    public void dump(String dir) {
        ClassWriter cw = new ClassWriter(0)
        FieldVisitor fv
        MethodVisitor mv

        cw.visit(V1_7, ACC_PUBLIC + ACC_SUPER, "com/ttpic/plugin/Decoder", null, "java/lang/Object", null)

        cw.visitSource("Decoder.java", null)

        fv = cw.visitField(ACC_PRIVATE + ACC_STATIC, "IMPL", "Lcom/ttpic/plugin/XorEncryption;", null, null)
        fv.visitEnd()

        mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null)
        mv.visitCode()
        Label l0 = new Label()
        mv.visitLabel(l0)
        mv.visitLineNumber(6, l0)
        mv.visitVarInsn(ALOAD, 0)
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false)
        mv.visitInsn(RETURN)
        Label l1 = new Label()
        mv.visitLabel(l1)
        mv.visitLocalVariable("this", "Lcom/ttpic/plugin/Decoder;", null, l0, l1, 0)
        mv.visitMaxs(1, 1)
        mv.visitEnd()

        mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "decrypt", "(Ljava/lang/String;)Ljava/lang/String;", null, null)
        mv.visitCode()
        l0 = new Label()
        mv.visitLabel(l0)
        mv.visitLineNumber(10, l0)
        mv.visitFieldInsn(GETSTATIC, "com/ttpic/plugin/Decoder", "IMPL", "Lcom/ttpic/plugin/XorEncryption;")
        mv.visitVarInsn(ALOAD, 0)
        mv.visitIntInsn(BIPUSH, keyBytes.length)
        mv.visitIntInsn(NEWARRAY, T_BYTE)
        for (int i = 0; i < keyBytes.length; i++) {
            mv.visitInsn(DUP)
            mv.visitIntInsn(BIPUSH, i)
            mv.visitIntInsn(BIPUSH, keyBytes[i] ^= 0x00001)
            mv.visitInsn(BASTORE)
        }
        mv.visitMethodInsn(INVOKEVIRTUAL, "com/ttpic/plugin/XorEncryption", "decrypt", "(Ljava/lang/String;[B)Ljava/lang/String;", false)
        mv.visitInsn(ARETURN)
        l1 = new Label()
        mv.visitLabel(l1)
        mv.visitLocalVariable("data", "Ljava/lang/String;", null, l0, l1, 0)
        mv.visitMaxs(6, 1)
        mv.visitEnd()

        mv = cw.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null)
        mv.visitCode()
        l0 = new Label()
        mv.visitLabel(l0)
        mv.visitLineNumber(7, l0)
        mv.visitTypeInsn(NEW, "com/ttpic/plugin/XorEncryption")
        mv.visitInsn(DUP)
        mv.visitMethodInsn(INVOKESPECIAL, "com/ttpic/plugin/XorEncryption", "<init>", "()V", false)
        mv.visitFieldInsn(PUTSTATIC, "com/ttpic/plugin/Decoder", "IMPL", "Lcom/ttpic/plugin/XorEncryption;")
        mv.visitInsn(RETURN)
        mv.visitMaxs(2, 0)
        mv.visitEnd()
        cw.visitEnd()

        writeCwIntoFile(dir, "Decoder.class", cw.toByteArray())
    }
}
