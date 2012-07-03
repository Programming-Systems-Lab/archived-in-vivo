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
	//MethodInstance mi;
		
	/**
	 * Constructor for DummyMethodVisitor. Simply invokes superclass constructor.
	 * @param api				int				Generally Opcodes.ASM4
	 * @param mv				MethodVisitor	MethodVisitor to extend (can be null)
	 */
	public NDMethodVisitor(int api, MethodVisitor mv, String methodName) {
		super(api, mv);
		this.methodName = methodName;
	}
	
	@Override
	public void visitMethodInsn(int opcode, String owner, String name,
			String desc) {
		super.visitMethodInsn(opcode, owner, name, desc);
		MethodInstance f = new MethodInstance(name, desc, owner);
		String fName = f.getFullName();
		if (NativeDetector.methodMap.containsKey(fName)) {
			NativeDetector.methodMap.get(methodName).functionsICall.add(fName);
		} else {
			MethodInstance mi = new MethodInstance(methodName);
			mi.functionsICall.add(fName);
			NativeDetector.methodMap.put(methodName, mi);
		}
	}
}
