package edu.columbia.cs.psl.invivo.nativedetector2;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.Method;

public class InvocationFinder {
	// modified from: http://stackoverflow.com/questions/930289/how-can-i-find-all-the-methods-that-call-a-given-method-in-java
    private String targetClass;
    private Method targetMethod;

    private InvocationFindingClassVisitor cv;
    public HashMap<Method,String> invokersWithClasses = new HashMap<Method,String>();
    public HashSet<Method> invokers = new HashSet<Method>();

    private class InvocationFindingMethodVisitor extends MethodVisitor {

        boolean callsTarget;
        int line;
        public InvocationFindingMethodVisitor() {super(Opcodes.ASM4);}

        public void visitMethodInsn(int opcode, String owner, String name, String desc) {
            if (owner.equals(targetClass)
                    && name.equals(targetMethod.getName())
                    && desc.equals(targetMethod.getDescriptor())) {
                callsTarget = true;
            }
        }

        public void visitCode() {
            callsTarget = false;
        }

        public void visitLineNumber(int line, Label start) {
            this.line = line;
        }

        public void visitEnd() {
            if (callsTarget)
            	invokersWithClasses.put(new Method(cv.methodName, cv.methodDesc), cv.className);
            	invokers.add(new Method(cv.methodName, cv.methodDesc)); // TODO comment out
        }
    }
    	
    	private class InvocationFindingClassVisitor extends ClassVisitor {

        private InvocationFindingMethodVisitor mv = new InvocationFindingMethodVisitor();

        public String className;
        public String methodName;
        public String methodDesc;

        public InvocationFindingClassVisitor() { super(Opcodes.ASM4); }

        public void visit(int version, int access, String name,
                          String signature, String superName, String[] interfaces) {
            className = name;
        }


        public MethodVisitor visitMethod(int access, String name, 
                                         String desc, String signature,
                                         String[] exceptions) {
            methodName = name;
            methodDesc = desc;

            return mv;
        }
    }


    public HashSet<Method> findCallingMethodsInJar(String jarPath, String newTargetClass,
                                        Method newTargetMethod) throws Exception {

        this.targetClass = newTargetClass;
        this.targetMethod = newTargetMethod;

        this.cv = new InvocationFindingClassVisitor();

        JarFile jarFile = new JarFile(jarPath);
        Enumeration<JarEntry> entries = jarFile.entries();

        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();

            if (entry.getName().endsWith(".class")) {
                InputStream stream = new BufferedInputStream(jarFile.getInputStream(entry), 1024);
                ClassReader reader = new ClassReader(stream);

                reader.accept(cv, 0);

                stream.close();
            }
        }
        return invokers;
    }
}