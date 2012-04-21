package edu.columbia.cs.psl.invivo.nativedetector;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class NativeFindingClassVisitor extends ClassVisitor {

	public NativeFindingClassVisitor(int api, ClassVisitor cv) {
		super(api, cv);
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc,
			String signature, String[] exceptions) {
		//System.err.println("visitMethod" + access + " " + name + " " + desc + " " + signature + " " + exceptions);
		if ((access & Opcodes.ACC_NATIVE) == 0) {
			System.out.println(name +"\t"+ desc);
		}
		
		return super.visitMethod(access, name, desc, signature, exceptions);
	}
}
