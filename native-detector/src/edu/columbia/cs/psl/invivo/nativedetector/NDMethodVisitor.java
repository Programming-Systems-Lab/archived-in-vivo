package edu.columbia.cs.psl.invivo.nativedetector;

import java.util.HashMap;

import org.objectweb.asm.MethodVisitor;

/**
 * @author miriammelnick A MethodVisitor subclass that simply invokes the
 *         superclass. Useful when visitMethod is non-empty in ClassVisitor.
 * @see NDClassVisitor
 */
public class NDMethodVisitor extends MethodVisitor {
	String methodName;
	int access;
	private HashMap<String, MethodInstance> lookupCache;

	public NDMethodVisitor(int api, MethodVisitor mv, String methodName, int access, HashMap<String, MethodInstance> lookupCache) {
		super(api, mv);
		this.methodName = methodName;
		this.access = access;
		this.lookupCache = lookupCache;

	}

	@Override
	public void visitMethodInsn(int opcode, String owner, String name, String desc) {
		super.visitMethodInsn(opcode, owner, name, desc);
		String fName = owner + "." + name + ":" + desc;

		lookupCache.get(methodName).functionsThatICall.add(fName);

		if (lookupCache.containsKey(fName)) {
			lookupCache.get(fName).functionsThatCallMe.add(methodName);
		} else {
			MethodInstance mi = new MethodInstance(fName);
			mi.functionsThatCallMe.add(methodName);
			lookupCache.put(fName, mi);
		}
	}
}
