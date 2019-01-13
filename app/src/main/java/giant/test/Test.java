package giant.test;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.util.ASMifier;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.charset.Charset;

import static org.objectweb.asm.Opcodes.ASM6;
import static org.objectweb.asm.Opcodes.V1_5;
import static org.objectweb.asm.Opcodes.V1_7;

/**
 * Created by panda on 2019/1/6
 **/
public class Test {
    public static void main(String[] args) throws IOException {
//        doAdd();
//        traceCvExample();
//        checkClassAdapterExample();
        asmifierExample();
    }

    public static void asmifierExample() throws IOException {
        ClassReader cr = new ClassReader("giant/test/T");
        ClassWriter cw = new ClassWriter(0);
        TraceClassVisitor cv = new TraceClassVisitor(cw, new ASMifier(), new PrintWriter(System.out));
        cr.accept(cv, 0);
    }

    public static void checkClassAdapterExample() throws IOException {
        ClassReader cr = new ClassReader("java/lang/Runnable");
        ClassWriter cw = new ClassWriter(0);
        CheckClassAdapter cca = new CheckClassAdapter(cw);
        TraceClassVisitor cv = new TraceClassVisitor(cca, new PrintWriter(System.out));
        cr.accept(cv, 0);
    }

    public static void traceCvExample() throws IOException {
//        ClassWriter cw = new ClassWriter(0);
//        TraceClassVisitor cv = new TraceClassVisitor(cw, new PrintWriter(System.out));
//        cv.visit(Opcodes.V1_5, Opcodes.ACC_PUBLIC + Opcodes.ACC_ABSTRACT + Opcodes.ACC_INTERFACE,
//                "test/asm/examples/Comparable", null, "java/lang/Object",
//                new String[] { "test/asm/examples/Mesurable" });
//        cv.visitField(Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL + Opcodes.ACC_STATIC, "LESS", "I", null, new Integer(-1)).visitEnd();
//        cv.visitField(Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL + Opcodes.ACC_STATIC, "EQUAL", "I", null, new Integer(0)).visitEnd();
//        cv.visitField(Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL + Opcodes.ACC_STATIC, "GREATER", "I", null, new Integer(1)).visitEnd();
//        cv.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_ABSTRACT, "compareTo", "(Ljava/lang/Object;)I", null, null).visitEnd();
//        cv.visitEnd();

        ClassReader cr = new ClassReader("java/lang/Runnable");
        ClassWriter cw = new ClassWriter(0);
        TraceClassVisitor cv = new TraceClassVisitor(cw, new PrintWriter(System.out));
        cr.accept(cv, 0);
    }

    ///////// doAdd() ////////////////
    public static void doAdd() throws IOException {
        ClassReader cr = new ClassReader("java/lang/String");
        ClassWriter cw = new ClassWriter(cr,0);
        cr.accept(new AddFieldAdapter(cw, Opcodes.ACC_PUBLIC, "name", "I"), 0);
        // 打印结果
        byte[] b2 = cw.toByteArray();
        ClassPrinter cp = new ClassPrinter();
        ClassReader cr2 = new ClassReader(b2);
        cr2.accept(cp, 0);
    }

    public static class AddFieldAdapter extends ClassVisitor {
        private int fAcc;
        private String fName;
        private String fDesc;
        private boolean isFieldPresent;

        public AddFieldAdapter(ClassVisitor cv, int fAcc, String fName, String fDesc) {
            super(ASM6, cv);
            this.fAcc = fAcc;
            this.fName = fName;
            this.fDesc = fDesc;
        }

        @Override
        public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
            if (name.equals(fName)) {
                isFieldPresent = true;
            }
            return cv.visitField(access, name, desc, signature, value);
        }

        @Override
        public void visitEnd() {
            if (!isFieldPresent) {
                FieldVisitor fv = cv.visitField(fAcc, fName, fDesc, null, null);
                if (fv != null) {
                    fv.visitEnd();
                }
            }
            cv.visitEnd();
        }
    }

    ///////// end doAdd() ////////////////

    ///////// doRevise() ////////////////
    public static void doRevise() throws IOException {
        ClassReader cr = new ClassReader("java/lang/Runnable");
        ClassWriter cw = new ClassWriter(cr,0);
        ClassVisitor cv = new ClassVisitor(ASM6, cw) {
            @Override
            public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
                cv.visit(V1_5, access, name, signature, superName, interfaces);
            }

            @Override
            public void visitSource(String source, String debug) {
            }

            @Override
            public void visitOuterClass(String owner, String name, String desc) {
            }

            @Override
            public void visitInnerClass(String name, String outerName, String innerName, int access) {
            }

            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                return super.visitMethod(access, "myRun", desc, signature, exceptions);
            }

            @Override
            public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
                return super.visitField(access, name, desc, signature, value);
            }
        };
        cr.accept(cv, 0);
//        cr.accept(new RemoveMethodAdapter(cv, "run", ""), 0);
        // 打印结果
        byte[] b2 = cw.toByteArray();
        ClassPrinter cp = new ClassPrinter();
        ClassReader cr2 = new ClassReader(b2);
        cr2.accept(cp, 0);
    }

    public static class RemoveMethodAdapter extends ClassVisitor {
        private String mName;
        private String mDesc;

        public RemoveMethodAdapter(ClassVisitor cv, String mName, String mDesc) {
            super(ASM6, cv);
            this.mName = mName;
            this.mDesc = mDesc;
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            if (name.equals(mName)) { // 不要委托至下一个访问器 -> 这样将移除该方法
                return null;
            }
            return cv.visitMethod(access, name, desc, signature, exceptions);
        }
    }
    ///////// end doRevise() ////////////////

    ///////// doWrite() and classloader ////////////////
    public static void doWrite() throws IOException {
        ClassWriter cw = new ClassWriter(0);
        cw.visit(V1_7, Opcodes.ACC_PUBLIC + Opcodes.ACC_ABSTRACT + Opcodes.ACC_INTERFACE,
                "giant/test/Comparable", null, "java/lang/Object", null);
        cw.visitField(Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL + Opcodes.ACC_STATIC, "LESS", "I",
                null, -1).visitEnd();
        cw.visitField(Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL + Opcodes.ACC_STATIC, "EQUAL", "I",
                null, 0).visitEnd();
        cw.visitField(Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL + Opcodes.ACC_STATIC, "GREATER", "I",
                null, 1).visitEnd();
        cw.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_ABSTRACT, "compareTo",
                "(Ljava/lang/Object;)I", null, null).visitEnd();
        cw.visitEnd();
        byte[] b = cw.toByteArray();
        Class c = new MyClassLoader().defineClass("giant.test.Comparable", b);

        // 打印
        ClassPrinter cp = new ClassPrinter();
        ClassReader cr = new ClassReader(b);
        cr.accept(cp, 0);
    }
    ///////// end doWrite() and classloader ////////////////

    static class MyClassLoader extends ClassLoader {
        public Class defineClass(String name, byte[] b) {
            return defineClass(name, b, 0, b.length);
        }

        @Override
        protected URL findResource(String name) {
            return super.findResource(name);
        }
    }

    ///////// doPrint() ////////////////
    public static void doPrint(String s) throws IOException {
        ClassPrinter cp = new ClassPrinter();
        ClassReader cr = new ClassReader(s);
        cr.accept(cp, 0);
    }

    public static class ClassPrinter extends ClassVisitor {
        public ClassPrinter() {
            super(ASM6);
        }

        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            System.out.println(name + " extends " + superName + " {");
        }

        public void visitSource(String source, String debug) {
        }

        public void visitOuterClass(String owner, String name, String desc) {
        }

        public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
            return null;
        }

        public void visitAttribute(Attribute attr) {
        }

        public void visitInnerClass(String name, String outerName, String innerName, int access) {
        }

        public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
            System.out.println(" " + desc + " " + name);
            return null;
        }

        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            System.out.println(" " + name + desc);
            return null;
        }

        public void visitEnd() {
            System.out.println("}");
        }
    }
    ///////// end doPrint() ////////////////

}
