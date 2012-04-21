package edu.columbia.cs.psl.invivo.nativedetector;

import org.objectweb.asm.MethodVisitor;

public class CallFindingMethodVisitor extends MethodVisitor{

	private String method;
	private String desc;
	private String clazz;
	public CallFindingMethodVisitor(int api, MethodVisitor mv, String method, String desc, String clazz) {
		super(api, mv);
		this.method = method;
		this.desc=desc;
		this.clazz = clazz;
	}

	@Override
	public void visitMethodInsn(int opcode, String owner, String name,
			String desc) {
		System.out.println(owner+name+desc + " found in " + this.clazz + this.method + this.desc);
		super.visitMethodInsn(opcode, owner, name, desc);
	}
}
