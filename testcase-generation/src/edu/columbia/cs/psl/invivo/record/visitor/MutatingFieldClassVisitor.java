package edu.columbia.cs.psl.invivo.record.visitor;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import edu.columbia.cs.psl.invivo.record.Instrumenter;

public class MutatingFieldClassVisitor extends ClassVisitor{

	public MutatingFieldClassVisitor(int api, ClassVisitor cv) {
		super(api, cv);
	}
	private String className;
	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		// TODO Auto-generated method stub
		super.visit(version, access, name, signature, superName, interfaces);
		className = name;
	}
	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
	
		MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
		if(Instrumenter.getAnnotatedMethod(className, name, desc).isMutatesFields())
			return new MutatingFieldMethodVisitor(access, mv, access, name, desc, className);
		else
			return mv;
	}
}
