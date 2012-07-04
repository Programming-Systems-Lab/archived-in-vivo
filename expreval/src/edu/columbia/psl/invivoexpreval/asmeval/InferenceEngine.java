package edu.columbia.psl.invivoexpreval.asmeval;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

/**
 * This is used to get various types of information about java classes and
 * methods. Architecture 1) Use the thread's class loader to load the class we
 * desire. If a classnotfound exception is thrown... 2) Use objectasm to read
 * the byte array of the classes in a particular directory
 */
public class InferenceEngine {
    public static File directory = new File("/home/nikhil/in-vivo");

    public static String packageName;

    public static Object loadClass(String className) {
        Class c = null;
        if (className != "") {
            try {
                c = Thread.currentThread().getContextClassLoader().loadClass(className);
            } catch (ClassNotFoundException e) {
                try {
                    List<ClassReader> packageClasses =
                        findClasses(InferenceEngine.directory, InferenceEngine.packageName);
                    for (ClassReader cls : packageClasses) {
                        if (cls.getClassName().equals(className))
                            return cls;
                    }
                } catch (ClassNotFoundException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                    return null;
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        }
        return c;
    }

    /**
     * Recursive method used to find all classes in a given directory and its
     * sub directories.
     * 
     * @throws IOException
     */
    @SuppressWarnings("rawtypes")
    private static List<ClassReader> findClasses(File directory, String className)
        throws ClassNotFoundException, IOException {
        List<ClassReader> classes = new ArrayList<ClassReader>();
        if (!directory.exists()) {
            return classes;
        }

        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, className));
            } else if (file.getName().endsWith(".class") && file.getName().contains(className)) {
                InputStream is = new FileInputStream(file);
                classes.add(new ClassReader(is));
                is.close();
            }
        }

        return classes;
    }

    public static List<InVivoMethodDesc> getMethodInfo(String className, String methodName,
        String desc) {
        List<InVivoMethodDesc> mdescs = new ArrayList<InVivoMethodDesc>();
        try {
            List<ClassReader> crs = InferenceEngine.findClasses(directory, className);

            if (crs.size() <= 0)
                return null;
            for (ClassReader cr : crs) {
                ClassNode cn = new ClassNode();
                cr.accept(cn, ClassReader.SKIP_FRAMES);

                List<MethodNode> methods = cn.methods;

                for (MethodNode m : methods) {
                    if (m.name.equals(methodName)
                        || (m.name.equals(methodName) && !desc.equals("") && m.desc.contains(desc))) {
                        InVivoMethodDesc mDesc = new InVivoMethodDesc();
                        mDesc.setMethodName(m.name);
                        mDesc.setMethodDesc(m.desc);
                        mDesc.setMethodAcc(m.access);
                        mDesc.setLocals(m.localVariables);
                        System.out.println(cn.name);
                        mDesc.setMethodParentClassDesc(InferenceEngine.getClassInfo(cn.name.substring(cn.name.lastIndexOf("/") + 1)));
                        mdescs.add(mDesc);
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return mdescs;
    }

    /**
     * TODO
     */
    public static InVivoClassDesc getClassInfo(String className) {
        try {
            List<ClassReader> crs = InferenceEngine.findClasses(directory, className);
            if (crs.size() <= 0)
                return null;
            InVivoClassDesc c = new InVivoClassDesc();
            for (ClassReader cr : crs) {
                c.setClassName(cr.getClassName());
                return c;
            }
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /*
     * This infers a given identifier's type information.
     */
    public static InVivoIdentifierDesc getIdentifierInfo(String className, String methodName,
        String identifier, int index) {
        try {
            List<InVivoMethodDesc> methods = InferenceEngine.getMethodInfo(className, methodName, "");
            if (methods.size() <= 0)
                return null;
            for (InVivoMethodDesc m : methods) {
                for (LocalVariableNode lvn : m.getLocals()) {
                    if (lvn.name.equals(identifier) && lvn.index == index) {
                        InVivoIdentifierDesc id = new InVivoIdentifierDesc();
                        id.setDesc(lvn.desc);
                        id.setIndex(lvn.index);
                        id.setName(lvn.name);
                        id.setSignature(lvn.signature);
                        return id;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void main(String args[]) {
        // System.out.println(InferenceEngine.findClasses(directory,
        // "SimpleExample"));
        System.out.println(Integer.valueOf("100000000000000000"));
        System.out.println(InferenceEngine
            .getClassFields("edu/columbia/cs/psl/invivo/junit/SimpleExample"));
    }

    public static List<InVivoIdentifierDesc> getClassFields(String className) {
        try {
            List<ClassReader> crs =
                InferenceEngine.findClasses(directory,
                    className.substring(className.lastIndexOf('/') + 1));

            if (crs.size() <= 0)
                return null;

            ClassNode cn = new ClassNode();

            for (ClassReader cr : crs) {
                if (cr.getClassName().equals(className)) {
                    cr.accept(cn, ClassReader.SKIP_FRAMES);
                    break;
                }
            }

            List<InVivoIdentifierDesc> fields = new ArrayList<InVivoIdentifierDesc>();
            for (Object fn : cn.fields) {
                InVivoIdentifierDesc id = new InVivoIdentifierDesc();
                id.setDesc(((FieldNode) fn).desc);
                id.setName(((FieldNode) fn).name);
                id.setSignature(((FieldNode) fn).signature);
                id.setClassName(cn.name);
                id.setAccess(((FieldNode) fn).access);
                fields.add(id);
            }
            return fields;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
