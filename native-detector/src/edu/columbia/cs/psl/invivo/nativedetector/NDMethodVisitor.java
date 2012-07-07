package edu.columbia.cs.psl.invivo.nativedetector;

import org.objectweb.asm.MethodVisitor;

/**
 * @author miriammelnick
 * A MethodVisitor subclass that simply invokes the superclass. 
 * Useful when visitMethod is non-empty in ClassVisitor.
 * @see NDClassVisitor
 */
public class NDMethodVisitor extends MethodVisitor{
	String methodName;
	int access;
		
	/**
	 * Constructor for DummyMethodVisitor. Simply invokes superclass constructor.
	 * @param api				int				Generally Opcodes.ASM4
	 * @param mv				MethodVisitor	MethodVisitor to extend (can be null)
	 */
	public NDMethodVisitor(int api, MethodVisitor mv, String methodName, int access) {
		super(api, mv);
		this.methodName = methodName;
		this.access = access;
	}
	
	@Override
	public void visitMethodInsn(int opcode, String owner, String name,
			String desc) {
		super.visitMethodInsn(opcode, owner, name, desc);
		String fName = owner + "." + name + ":" + desc;
		if (NativeDetector.methodMap.containsKey(methodName)) {
			MethodInstance mi = NativeDetector.methodMap.get(methodName);
			mi.functionsICall.add(fName);
			mi.setAccess(access);
			NativeDetector.methodMap.put(methodName, mi);
		} else {
			MethodInstance mi = new MethodInstance(methodName);
			mi.functionsICall.add(fName);
			mi.setAccess(access);
			NativeDetector.methodMap.put(methodName, mi);
		}
	}
}
