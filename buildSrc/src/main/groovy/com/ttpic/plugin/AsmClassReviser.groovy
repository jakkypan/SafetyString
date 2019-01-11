package com.ttpic.plugin

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

/**
 * Created by panda on 2018/12/24
 **/
public class AsmClassReviser extends ClassVisitor {
    String mKey
    IEncryption mImplementation
    String[] mSensitives

    private String mClassName
    private boolean isFiledAdded = false

    AsmClassReviser(ClassVisitor cv, String key, String implementation, String[] sensitives) {
        super(Opcodes.ASM6, cv)
        this.mKey = key
        this.mImplementation = Class.forName(implementation).newInstance()
        this.mSensitives = sensitives
    }

    @Override
    void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        mClassName = name
        super.visit(version, access, name, signature, superName, interfaces)
    }

    @Override
    FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        if (!isFiledAdded) {
            isFiledAdded = true
            for (int i = 0; i < mSensitives.size(); i += 2) {
                def k = mSensitives[i]
                FieldVisitor fv = cv.visitField(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC + Opcodes.ACC_FINAL,
                        k,
                        "Ljava/lang/String;",
                        null,
                        null)
                fv.visitEnd()
            }
        }
        // 删除打桩的field，最后是不要写入到class里的
        if (mSensitives.contains(name)) {
            return null
        }
        return super.visitField(access, name, desc, signature, value)
    }

    @Override
    MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions)
        // 静态方法，加入decode的代码
        if (name.equals("<clinit>")) {
            mv = new DecodeMethodAdapter(mv)
        }
        return mv
    }

    @Override
    void visitEnd() {

        // 写入需要保护的数据
//        for (int i = 0; i < mSensitives.size(); i += 2) {
//            def name = mSensitives[i]
//            def value = mSensitives[i+1]
//            FieldVisitor fv = cv.visitField(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC + Opcodes.ACC_FINAL,
//                    name,
//                    "Ljava/lang/String",
//                    null,
//                    mImplementation.decrypt(value, mKey))
//            fv.visitEnd()
//        }

        super.visitEnd()
    }

    /**
     * 插入自动解码功能
     */
    class DecodeMethodAdapter extends MethodVisitor {
        DecodeMethodAdapter(MethodVisitor mv) {
            super(Opcodes.ASM6, mv)
        }

        @Override
        void visitCode() {
            mv.visitCode()

            for (int i = 0; i < mSensitives.size(); i += 2) {
                def name = mSensitives[i]
                def value = mImplementation.encrypt(mSensitives[i+1], mKey)

                mv.visitLdcInsn(value)
//                mv.visitLdcInsn(mKey)
                mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                        "com/ttpic/plugin/Decoder",
                        "decrypt",
                        "(Ljava/lang/String;)Ljava/lang/String;",
                        false)
                mv.visitFieldInsn(Opcodes.PUTSTATIC, mClassName, name, "Ljava/lang/String;")
            }
            mv.visitInsn(Opcodes.RETURN)
            mv.visitMaxs(2, 0)
        }
    }
}
