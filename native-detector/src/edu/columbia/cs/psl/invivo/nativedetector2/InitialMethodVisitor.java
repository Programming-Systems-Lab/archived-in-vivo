package edu.columbia.cs.psl.invivo.nativedetector2;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.Method;

public class InitialMethodVisitor extends MethodVisitor{

	public InitialMethodVisitor(int api, MethodVisitor mv, String method, String desc, String clazz) {
		super(api, mv);
	}

	@Override
	public void visitMethodInsn(int opcode, String owner, String name,
			String desc) {
//TODO
		super.visitMethodInsn(opcode, owner, name, desc);
	}
}
