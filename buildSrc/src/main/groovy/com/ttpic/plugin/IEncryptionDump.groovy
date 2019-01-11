package com.ttpic.plugin

import org.objectweb.asm.ClassWriter
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

/**
 * Created by panda on 2018/12/25
 **/
public class IEncryptionDump extends Dump implements Opcodes {

    @Override
    public void dump(String dir) {
        ClassWriter cw = new ClassWriter(0)
        MethodVisitor mv

        cw.visit(V1_7,
                ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE,
                "com/ttpic/plugin/IEncryption",
                null,
                "java/lang/Object",
                null)
        cw.visitSource("IEncryption.java", null)

        mv = cw.visitMethod(ACC_PUBLIC + ACC_ABSTRACT,
                "encrypt",
                "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;",
                null,
                null)
        mv.visitEnd()

        mv = cw.visitMethod(ACC_PUBLIC + ACC_ABSTRACT,
                "decrypt",
                "(Ljava/lang/String;[B)Ljava/lang/String;",
                null,
                null)
        mv.visitEnd()
        cw.visitEnd()

        writeCwIntoFile(dir, "IEncryption.class", cw.toByteArray())
    }
}
