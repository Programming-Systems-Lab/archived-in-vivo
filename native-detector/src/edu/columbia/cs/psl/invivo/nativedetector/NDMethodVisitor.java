package edu.columbia.cs.psl.invivo.nativedetector;

import java.util.LinkedList;

import org.apache.log4j.Logger;
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
		
	private static Logger logger = Logger.getLogger(NDMethodVisitor.class);
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
//		MethodInstance f = new MethodInstance(name, desc, owner);
//		String fName = f.getFullName();
		String fName = owner + "." + name + ":" + desc;
		if (NativeDetector.methodMap.containsKey(methodName)) {
			MethodInstance mi = NativeDetector.methodMap.get(methodName);
			NativeDetector.methodMap.remove(methodName);
//			LinkedList<String> newList = new LinkedList<String>();
//			newList.addAll(ll);
//			newList.add(fName);
			mi.functionsICall.add(fName);
			NativeDetector.methodMap.put(methodName, mi);
		} else {
			MethodInstance mi = new MethodInstance(methodName);
			mi.functionsICall.add(fName);
			NativeDetector.methodMap.put(methodName, mi);
		}
//		logger.info(NativeDetector.methodMap.get(methodName).functionsICall.size() + " of " + this.numMethodsICall);
//		logger.info(NativeDetector.methodMap.get(methodName).functionsICall.size());
//		logger.info(methodName + " calls " + NativeDetector.methodMap.get(methodName).functionsICall);
	}
}
