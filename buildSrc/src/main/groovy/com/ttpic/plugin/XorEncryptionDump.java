package com.ttpic.plugin;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Created by panda on 2018/12/25
 **/
public class XorEncryptionDump extends Dump implements Opcodes {

    @Override
    public void dump(String dir) {
        ClassWriter cw = new ClassWriter(0);
        FieldVisitor fv;
        MethodVisitor mv;

        cw.visit(V1_7, ACC_PUBLIC + ACC_SUPER, "com/ttpic/plugin/XorEncryption", null, "java/lang/Object", new String[] { "com/ttpic/plugin/IEncryption" });

        cw.visitSource("XorEncryption.java", null);

        fv = cw.visitField(ACC_PRIVATE + ACC_FINAL + ACC_STATIC, "charSet", "Ljava/nio/charset/Charset;", null, null);
        fv.visitEnd();

        mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitCode();
        Label l0 = new Label();
        mv.visitLabel(l0);
        mv.visitLineNumber(10, l0);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        mv.visitInsn(RETURN);
        Label l1 = new Label();
        mv.visitLabel(l1);
        mv.visitLocalVariable("this", "Lcom/ttpic/plugin/XorEncryption;", null, l0, l1, 0);
        mv.visitMaxs(1, 1);
        mv.visitEnd();

        mv = cw.visitMethod(ACC_PUBLIC, "encrypt", "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", null, null);
        mv.visitCode();
        l0 = new Label();
        mv.visitLabel(l0);
        mv.visitLineNumber(15, l0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitMethodInsn(INVOKESTATIC, "com/ttpic/plugin/XorEncryption", "xor", "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", false);
        mv.visitInsn(ARETURN);
        l1 = new Label();
        mv.visitLabel(l1);
        mv.visitLocalVariable("this", "Lcom/ttpic/plugin/XorEncryption;", null, l0, l1, 0);
        mv.visitLocalVariable("data", "Ljava/lang/String;", null, l0, l1, 1);
        mv.visitLocalVariable("key", "Ljava/lang/String;", null, l0, l1, 2);
        mv.visitMaxs(2, 3);
        mv.visitEnd();

        mv = cw.visitMethod(ACC_PUBLIC, "decrypt", "(Ljava/lang/String;[B)Ljava/lang/String;", null, null);
        mv.visitCode();
        l0 = new Label();
        mv.visitLabel(l0);
        mv.visitLineNumber(20, l0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitMethodInsn(INVOKESTATIC, "com/ttpic/plugin/XorEncryption", "xor", "(Ljava/lang/String;[B)Ljava/lang/String;", false);
        mv.visitInsn(ARETURN);
        l1 = new Label();
        mv.visitLabel(l1);
        mv.visitLocalVariable("this", "Lcom/ttpic/plugin/XorEncryption;", null, l0, l1, 0);
        mv.visitLocalVariable("data", "Ljava/lang/String;", null, l0, l1, 1);
        mv.visitLocalVariable("keyBytes", "[B", null, l0, l1, 2);
        mv.visitMaxs(2, 3);
        mv.visitEnd();

        mv = cw.visitMethod(ACC_PRIVATE + ACC_STATIC, "xor", "(Ljava/lang/String;[B)Ljava/lang/String;", null, null);
        mv.visitCode();
        l0 = new Label();
        mv.visitLabel(l0);
        mv.visitLineNumber(24, l0);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETSTATIC, "com/ttpic/plugin/XorEncryption", "charSet", "Ljava/nio/charset/Charset;");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "getBytes", "(Ljava/nio/charset/Charset;)[B", false);
        mv.visitVarInsn(ASTORE, 2);
        l1 = new Label();
        mv.visitLabel(l1);
        mv.visitLineNumber(25, l1);
        mv.visitInsn(ICONST_0);
        mv.visitVarInsn(ISTORE, 3);
        Label l2 = new Label();
        mv.visitLabel(l2);
        mv.visitFrame(Opcodes.F_APPEND,2, new Object[] {"[B", Opcodes.INTEGER}, 0, null);
        mv.visitVarInsn(ILOAD, 3);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitInsn(ARRAYLENGTH);
        Label l3 = new Label();
        mv.visitJumpInsn(IF_ICMPGE, l3);
        Label l4 = new Label();
        mv.visitLabel(l4);
        mv.visitLineNumber(26, l4);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitVarInsn(ILOAD, 3);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitVarInsn(ILOAD, 3);
        mv.visitInsn(BALOAD);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitVarInsn(ILOAD, 3);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitInsn(ARRAYLENGTH);
        mv.visitInsn(IREM);
        mv.visitInsn(BALOAD);
        mv.visitInsn(ICONST_1);
        mv.visitInsn(IXOR);
        mv.visitInsn(IXOR);
        mv.visitInsn(I2B);
        mv.visitInsn(BASTORE);
        Label l5 = new Label();
        mv.visitLabel(l5);
        mv.visitLineNumber(25, l5);
        mv.visitIincInsn(3, 1);
        mv.visitJumpInsn(GOTO, l2);
        mv.visitLabel(l3);
        mv.visitLineNumber(28, l3);
        mv.visitFrame(Opcodes.F_CHOP,1, null, 0, null);
        mv.visitTypeInsn(NEW, "java/lang/String");
        mv.visitInsn(DUP);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitFieldInsn(GETSTATIC, "com/ttpic/plugin/XorEncryption", "charSet", "Ljava/nio/charset/Charset;");
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/String", "<init>", "([BLjava/nio/charset/Charset;)V", false);
        mv.visitInsn(ARETURN);
        Label l6 = new Label();
        mv.visitLabel(l6);
        mv.visitLocalVariable("i", "I", null, l2, l3, 3);
        mv.visitLocalVariable("data", "Ljava/lang/String;", null, l0, l6, 0);
        mv.visitLocalVariable("keyBytes", "[B", null, l0, l6, 1);
        mv.visitLocalVariable("inputBytes", "[B", null, l1, l6, 2);
        mv.visitMaxs(6, 4);
        mv.visitEnd();

        mv = cw.visitMethod(ACC_PRIVATE + ACC_STATIC, "xor", "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", null, null);
        mv.visitCode();
        l0 = new Label();
        mv.visitLabel(l0);
        mv.visitLineNumber(32, l0);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitFieldInsn(GETSTATIC, "com/ttpic/plugin/XorEncryption", "charSet", "Ljava/nio/charset/Charset;");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "getBytes", "(Ljava/nio/charset/Charset;)[B", false);
        mv.visitMethodInsn(INVOKESTATIC, "com/ttpic/plugin/XorEncryption", "xor", "(Ljava/lang/String;[B)Ljava/lang/String;", false);
        mv.visitInsn(ARETURN);
        l1 = new Label();
        mv.visitLabel(l1);
        mv.visitLocalVariable("data", "Ljava/lang/String;", null, l0, l1, 0);
        mv.visitLocalVariable("key", "Ljava/lang/String;", null, l0, l1, 1);
        mv.visitMaxs(3, 2);
        mv.visitEnd();

        mv = cw.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null);
        mv.visitCode();
        l0 = new Label();
        mv.visitLabel(l0);
        mv.visitLineNumber(11, l0);
        mv.visitLdcInsn("utf-8");
        mv.visitMethodInsn(INVOKESTATIC, "java/nio/charset/Charset", "forName", "(Ljava/lang/String;)Ljava/nio/charset/Charset;", false);
        mv.visitFieldInsn(PUTSTATIC, "com/ttpic/plugin/XorEncryption", "charSet", "Ljava/nio/charset/Charset;");
        mv.visitInsn(RETURN);
        mv.visitMaxs(1, 0);
        mv.visitEnd();
        cw.visitEnd();

        writeCwIntoFile(dir, "XorEncryption.class", cw.toByteArray());
    }
}
