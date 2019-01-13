package com.ttpic.plugin

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.commons.LocalVariablesSorter

/**
 * Created by panda on 2018/12/24
 **/
public class AsmTimerClassReviser extends ClassVisitor {
    private String mClassName

    AsmTimerClassReviser(ClassVisitor cv) {
        super(Opcodes.ASM6, cv)
    }

    @Override
    void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        mClassName = name
        super.visit(version, access, name, signature, superName, interfaces)
    }

    @Override
    FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        return super.visitField(access, name, desc, signature, value)
    }

    @Override
    MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions)
        // 构造函数和静态构造函数不加耗时打点
        if (!name.equals("<init>") && !name.equals("<clinit>")) {
            mv = new TimerMethodAdapter(" / " + mClassName.replace("/", ".") + "." + name, access, desc, mv)
        }
        return mv
    }

    /**
     * 耗时打点功能
     */
    class TimerMethodAdapter extends LocalVariablesSorter implements Opcodes {
        private int startVarIndex
        private String methodName

        TimerMethodAdapter(String name, int access, String desc, MethodVisitor mv) {
            super(Opcodes.ASM6, access, desc, mv)
            methodName = name
        }

        @Override
        void visitCode() {
            mv.visitCode()
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false)
            startVarIndex = newLocal(Type.LONG_TYPE)
            mv.visitVarInsn(LSTORE, startVarIndex)
        }

        @Override
        void visitInsn(int opcode) {
            if ((opcode >= IRETURN && opcode <= RETURN) || opcode == ATHROW) {
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false)
                // 计算时差
                int endVarIndex = newLocal(Type.LONG_TYPE)
                mv.visitVarInsn(LSTORE, endVarIndex)
                mv.visitVarInsn(LLOAD, endVarIndex)
                mv.visitVarInsn(LLOAD, startVarIndex)
                mv.visitInsn(LSUB)
                int index = newLocal(Type.LONG_TYPE)
                mv.visitVarInsn(LSTORE, index)
                // 开始Log.e的代码
                mv.visitLdcInsn("timers")
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "currentThread", "()Ljava/lang/Thread;", false)
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Thread", "toString", "()Ljava/lang/String;", false)
                int index2 = newLocal(Type.CHAR_TYPE)
                mv.visitVarInsn(ASTORE, index2)
                mv.visitTypeInsn(NEW, "java/lang/StringBuilder")
                mv.visitInsn(DUP)
                mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false)
                mv.visitVarInsn(ALOAD, index2)
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false)
                mv.visitLdcInsn(methodName)
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false)
                mv.visitLdcInsn(" time cost: ")
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false)
                mv.visitVarInsn(LLOAD, index)
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(J)Ljava/lang/StringBuilder;", false)
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false)
                mv.visitMethodInsn(INVOKESTATIC, "android/util/Log", "e", "(Ljava/lang/String;Ljava/lang/String;)I", false)
                // 必须下面2行代码，否则无法编译，很奇怪
                int ii = newLocal(Type.INT_TYPE)
                mv.visitVarInsn(ISTORE, ii)
                mv.visitEnd()
            }
            super.visitInsn(opcode)
        }
    }
}
