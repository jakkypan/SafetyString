package giant.test;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.URL;

/**
 * Created by panda on 2019/1/8
 **/
public class MethodTest {
    public static void main(String[] args) throws Exception {
//        doRemove();
        doTimer();
    }

    public static void doRemove() throws IOException {
        ClassReader cr = new ClassReader("java/lang/Runnable");
        ClassWriter cw = new ClassWriter(0);
        RemoveNopClassAdapter adapter = new RemoveNopClassAdapter(cw);
//        TraceClassVisitor cv = new TraceClassVisitor(adapter, new PrintWriter(System.out));
        cr.accept(adapter, 0);

        // 打印结果
        byte[] b2 = cw.toByteArray();
        Test.ClassPrinter cp = new Test.ClassPrinter();
        ClassReader cr2 = new ClassReader(b2);
        cr2.accept(cp, 0);
    }

    public static class RemoveNopClassAdapter extends ClassVisitor implements Opcodes {
        public RemoveNopClassAdapter(ClassVisitor cv) {
            super(ASM6, cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
            if (mv != null) {
                mv = new RemoveNopAdapter(mv);
            }
            return mv;
        }
    }

    public static class RemoveNopAdapter extends MethodVisitor implements Opcodes {
        public RemoveNopAdapter(MethodVisitor mv) {
            super(ASM6, mv);
        }

        @Override
        public void visitInsn(int opcode) {
            if (opcode != NOP) {
                mv.visitInsn(opcode);
            }
        }
    }


    ////// add timer for function ///////

    public static void doTimer() throws Exception {
        ClassReader cr = new ClassReader("giant/test/C");
        ClassWriter cw = new ClassWriter(0);
        AddTimerAdapter adapter = new AddTimerAdapter(cw);
        cr.accept(adapter, 0);

        // 打印结果
        byte[] b2 = cw.toByteArray();
        ClassReader cr2 = new ClassReader(b2);
        TraceClassVisitor cv = new TraceClassVisitor(cw, new PrintWriter(System.out));
        cr2.accept(cv, 0);
//        Class c = new MyClassLoader().defineClass("giant.test.C", b2);
//        Object o = c.newInstance();
//        Method method = c.getMethod("m", null);
//        method.invoke(o, null);
    }

    static class MyClassLoader extends ClassLoader {
        public Class defineClass(String name, byte[] b) {
            return defineClass(name, b, 0, b.length);
        }
    }

    public static class AddTimerAdapter extends ClassVisitor implements Opcodes {
        private String owner;
        private boolean isInterface;

        public AddTimerAdapter(ClassVisitor cv) {
            super(ASM6, cv);
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            cv.visit(version, access, name, signature, superName, interfaces);
            owner = name;
            isInterface = (access & ACC_INTERFACE) != 0;
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
            if (!isInterface && mv != null && !name.equals("<init>")) {
                mv = new AddTimerMethodAdapter(mv);
            }
            return mv;
        }

        @Override
        public void visitEnd() {
            if (!isInterface) {
                FieldVisitor fv = cv.visitField(ACC_PUBLIC + ACC_STATIC, "timer", "J", null, null);
                if (fv != null) {
                    fv.visitEnd();
                }
            }
            cv.visitEnd();
        }

        class AddTimerMethodAdapter extends MethodVisitor {
            public AddTimerMethodAdapter(MethodVisitor mv) {
                super(ASM6, mv);
            }

            @Override
            public void visitCode() {
                mv.visitCode();
                mv.visitFieldInsn(GETSTATIC, owner, "timer", "J");
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J");
                mv.visitInsn(LSUB);
                mv.visitFieldInsn(PUTSTATIC, owner, "timer", "J");
            }

            @Override
            public void visitInsn(int opcode) {
                if ((opcode >= IRETURN && opcode <= RETURN) || opcode == ATHROW) {
                    mv.visitFieldInsn(GETSTATIC, owner, "timer", "J");
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J");
                    mv.visitInsn(LADD);
                    mv.visitFieldInsn(PUTSTATIC, owner, "timer", "J");
                }
                mv.visitInsn(opcode);
            }

            @Override
            public void visitMaxs(int maxStack, int maxLocals) {
                mv.visitMaxs(maxStack + 4, maxLocals);
            }
        }
    }
}
