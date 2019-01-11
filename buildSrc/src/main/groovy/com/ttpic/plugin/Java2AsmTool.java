package com.ttpic.plugin;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.ASMifier;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;

/**
 * Created by panda on 2019/1/9
 **/
public class Java2AsmTool {

    public static void main(String[] args) throws IOException {
        ClassReader cr = new ClassReader("com/ttpic/plugin/Decoder");
        ClassWriter cw = new ClassWriter(0);
        TraceClassVisitor cv = new TraceClassVisitor(cw, new ASMifier(), new PrintWriter(System.out));
        cr.accept(cv, 0);
    }
}
