package edu.columbia.cs.psl.invivo.nativedetector;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

public class CallFindingClassVisitor extends ClassVisitor {

	public CallFindingClassVisitor(int api, ClassVisitor cv, String name) {
		super(api, cv);
		this.name = name;
	}

	private String name;
	
	@Override
	public MethodVisitor visitMethod(int access, String name, String desc,
			String signature, String[] exceptions) {
		return new CallFindingMethodVisitor(api, super.visitMethod(
				access, name, desc, signature, exceptions), name, desc, this.name);
	}
}
