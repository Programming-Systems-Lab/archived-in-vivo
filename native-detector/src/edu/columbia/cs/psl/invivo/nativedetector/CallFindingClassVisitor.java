package edu.columbia.cs.psl.invivo.nativedetector;

import org.apache.log4j.Logger;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * @author miriammelnick
 * ClassVisitor to facilitate use of CallFindingMethodVisitor (which finds all invocations of a method).
 * @see CallFindingMethodVisitor
 */
public class CallFindingClassVisitor extends ClassVisitor{
	String className;
	NativeDetector engine;
	//TODO comment logger
	private static Logger logger = Logger.getLogger(CallFindingClassVisitor.class);
	
	/**
	 * Constructor for CallFindingClassVisitor.
	 * @param api					int					Generally Opcodes.ASM4
	 * @param cv					ClassVisitor		ClassVisitor to extend (null is valid)
	 * @param className				String				name of owning class
	 * @param nd					NativeDetector		NativeDetector containing allMethods list
	 * @see Opcodes#ASM4
	 * @see ClassVisitor
	 * @see NativeDetector
	 */
	public CallFindingClassVisitor(int api, ClassVisitor cv, String className, NativeDetector nd) {
		super(api, cv);
		this.className = className;
		this.engine = nd;
	}
	
	/**
	 * TODO finish commenting
	 * (Override) 
	 * @param access				int					
	 */
	@Override
	public MethodVisitor visitMethod(int access, String name, String desc,
			String signature, String[] exceptions) {
		//logger.info(className + ": " + name);
		engine.logStats();
		//MethodInstance mi = this.engine.getMethodInstance(this.className, name, desc);
		MethodInstance mi = new MethodInstance(this.className, name, desc, access); //TODO undebug
		return new CallFindingMethodVisitor(api, super.visitMethod(
				access, name, desc, signature, exceptions), mi, this.engine);
	}


}
