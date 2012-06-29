package edu.columbia.cs.psl.invivo.nativedetector;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * @author miriammelnick
 * A MethodVisitor to find every function called by a given method.
 */
public class CallFindingMethodVisitor extends MethodVisitor {
	
	/**
	 * The method being visited.
	 * public MethodInstance methodInstance
	 * @see MethodVisitor
	 * @see MethodInstance
	 */
	public MethodInstance methodInstance;
	
	int count = 0;
	/**
	 * Constructor for CallFindingMethodVisitor. Its third argument is assumed to be the MethodInstance 
	 * from {@link NativeDetector#getMethodInstance(String, String, String)}.
	 * @param api			int				Generally pass Opcodes.ASM4
	 * @param mv			MethodVisitor	MV to extend (null is valid here)
	 * @param mi			MethodInstance	MI from {@link NativeDetector#getMethodInstance(String, String, String)}
	 * @param nd			NativeDetector
	 * @see NativeDetector#getMethodInstance(String, String, String)
	 * @see MethodInstance
	 * @see Opcodes#ASM4
	 * @see MethodVisitor
	 */
	public CallFindingMethodVisitor(int api, MethodVisitor mv, MethodInstance mi) {
		super(api, mv);
		this.methodInstance = mi; // this is the one from allMethods
	}

	@Override
	/**
	 * (Override) For each instruction that calls a method, 
	 * @param opcode		int
	 * @param owner			String
	 * @param name			String
	 * @param desc			String
	 * TODO fix partial redundancy with {@link NativeDetector#getMethodInstance(String, String, String)}
	 * @see MethodVisitor#visitMethodInsn
	 * @see MethodInstance#addCaller
	 * @see CallFindingMethodVisitor#methodInstance
	 */
	public void visitMethodInsn(int opcode, String owner, String name, String desc) {
		//logger.info(owner + "." + name);
		//engine.logStats();
		MethodInstance mi = new MethodInstance(name, desc, owner);
		
	//	this.methodInstance.calls.add((long) NativeDetector.allMethods.indexOf(mi));
		//		NativeDetector.getMethodInstance(owner, name, desc).addCaller(this.methodInstance);
		super.visitMethodInsn(opcode, owner, name, desc);
	}
	
}
