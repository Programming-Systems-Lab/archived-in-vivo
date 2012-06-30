package edu.columbia.cs.psl.invivo.nativedetector;

import org.objectweb.asm.MethodVisitor;

/**
 * @author miriammelnick
 * A MethodVisitor subclass that simply invokes the superclass. 
 * Useful when visitMethod is non-empty in ClassVisitor.
 * @see CompleteClassVisitor
 */
public class DummyMethodVisitor extends MethodVisitor{

	MethodInstance mi;
		
	/**
	 * Constructor for DummyMethodVisitor. Simply invokes superclass constructor.
	 * @param api				int				Generally Opcodes.ASM4
	 * @param mv				MethodVisitor	MethodVisitor to extend (can be null)
	 */
	public DummyMethodVisitor(int api, MethodVisitor mv, String methodName) {
		super(api, mv);
		this.mi = NativeDetector.methodMap.get(methodName);
	}
	
	@Override
	public void visitMethodInsn(int opcode, String owner, String name,
			String desc) {
		super.visitMethodInsn(opcode, owner, name, desc);
		MethodInstance f = new MethodInstance(name, desc, owner);
		mi.functionsICall.add(f.getFullName());
	}
}
