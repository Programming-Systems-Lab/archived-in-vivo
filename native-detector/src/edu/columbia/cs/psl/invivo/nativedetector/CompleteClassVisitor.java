package edu.columbia.cs.psl.invivo.nativedetector;


import java.util.LinkedList;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

/**
 * @author miriammelnick
 * TODO comment this
 */
public class CompleteClassVisitor extends ClassVisitor {
	private String className;
	LinkedList<MethodInstance> allMethods = new LinkedList<MethodInstance>();

	/**
	 * TODO comment
	 * @param api
	 * @param cv
	 * @param name
	 */
	public CompleteClassVisitor(int api, ClassVisitor cv, String name) {
		super(api, cv);
		this.className = name;
	}

	/**
	 * TODO comment
	 */
	@Override
	public MethodVisitor visitMethod(int access, String name, String desc,
			String signature, String[] exceptions) {

		MethodInstance mi = new MethodInstance(name, desc, this.className, access);
		this.allMethods.add(mi);
		return new CallFindingMethodVisitor(access, super.visitMethod(access, name, desc, signature, exceptions), mi);
//		return new DummyMethodVisitor(api, super.visitMethod(
//				access, name, desc, signature, exceptions)); 
	}


}
