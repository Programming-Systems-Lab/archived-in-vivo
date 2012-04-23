package edu.columbia.cs.psl.invivo.nativedetector;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
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

    private ArrayList<Callee> callees = new ArrayList<Callee>();

    private static class Callee {
        String className;
        String methodName;
        String methodDesc;
        String source;
        int line;

        public Callee(String cName, String mName, String mDesc, String src, int ln) {
            className = cName; methodName = mName; methodDesc = mDesc; source = src; line = ln;
        }
    }
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
                callees.add(new Callee(cv.className, cv.methodName, cv.methodDesc, 
                        cv.source, line));
        }
    }
    	
    	private class InvocationFindingClassVisitor extends ClassVisitor {

        private InvocationFindingMethodVisitor mv = new InvocationFindingMethodVisitor();

        public String source;
        public String className;
        public String methodName;
        public String methodDesc;

        public InvocationFindingClassVisitor() { super(Opcodes.ASM4); }

        public void visit(int version, int access, String name,
                          String signature, String superName, String[] interfaces) {
            className = name;
        }

        public void visitSource(String source, String debug) {
            this.source = source;
        }

        public MethodVisitor visitMethod(int access, String name, 
                                         String desc, String signature,
                                         String[] exceptions) {
            methodName = name;
            methodDesc = desc;

            return mv;
        }
    }


    public void findCallingMethodsInJar(String jarPath, String targetClass,
                                        String targetMethodDeclaration) throws Exception {

        this.targetClass = targetClass;
        this.targetMethod = Method.getMethod(targetMethodDeclaration);

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
    }
    
    public void run( String jarPath, String targetClass, String targetMethodDeclaration) {
        try {
            InvocationFinder ifinder = new InvocationFinder();

            ifinder.findCallingMethodsInJar(jarPath, targetClass, targetMethodDeclaration);

            for (Callee c : ifinder.callees) {
                System.out.println(c.source+":"+c.line+" "+c.className+" "+c.methodName+" "+c.methodDesc);
            }

            System.out.println("--\n"+ifinder.callees.size()+" methods invoke "+
                    ifinder.targetClass+" "+
                    ifinder.targetMethod.getName()+" "+ifinder.targetMethod.getDescriptor());
        } catch(Exception x) {
            x.printStackTrace();
        }
    }

    public void runAndWrite( String jarPath, String targetClass, String targetMethodDeclaration, String fileName, boolean append, boolean debug) {
        try {
        	
        	FileWriter fw = new FileWriter(debug ? fileName+"debug.txt" : fileName+".txt", append);
        	BufferedWriter bw = new BufferedWriter(fw);
        	
        	
            InvocationFinder ifinder = new InvocationFinder();

            ifinder.findCallingMethodsInJar(jarPath, targetClass, targetMethodDeclaration);

            for (Callee c : ifinder.callees) {
            	if (debug)
            		bw.write(c.source+":"+c.line+" "+c.className+" "+c.methodName+" "+c.methodDesc+"\n");
            	else
            		bw.write(c.className+" "+c.methodName+" "+c.methodDesc+"\n");

            }
            bw.close();
            fw.close();
            if (debug) {
            	System.out.println("--\n"+ifinder.callees.size()+" methods invoke "+
                    ifinder.targetClass+" "+
                    ifinder.targetMethod.getName()+" "+ifinder.targetMethod.getDescriptor());
            }
        } catch(Exception x) {
            x.printStackTrace();
        }
    }
    
    public static void main( String[] args ) {
        try {
            InvocationFinder ifinder = new InvocationFinder();

            ifinder.findCallingMethodsInJar(args[0], args[1], args[2]);

            for (Callee c : ifinder.callees) {
                System.out.println(c.source+":"+c.line+" "+c.className+" "+c.methodName+" "+c.methodDesc);
            }

            System.out.println("--\n"+ifinder.callees.size()+" methods invoke "+
                    ifinder.targetClass+" "+
                    ifinder.targetMethod.getName()+" "+ifinder.targetMethod.getDescriptor());
        } catch(Exception x) {
            x.printStackTrace();
        }
    }

}