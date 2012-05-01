package edu.columbia.cs.psl.invivo.nativedetector2;


import java.util.HashSet;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.Method;

public class InitialClassVisitor extends ClassVisitor {
	private String name;
	HashSet<Method> methods = new HashSet<Method>();

	public InitialClassVisitor(int api, ClassVisitor cv, String name) {
		super(api, cv);
		this.name = name;
	}

	
	@Override
	public MethodVisitor visitMethod(int access, String name, String desc,
			String signature, String[] exceptions) {
		if ((access & Opcodes.ACC_NATIVE) == 0)
		{
			this.methods.add(new Method(name, desc));
		}
		return new InitialMethodVisitor(api, super.visitMethod(
				access, name, desc, signature, exceptions), name, desc, this.name);
	}


}
