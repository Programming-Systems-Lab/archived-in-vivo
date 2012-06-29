package edu.columbia.cs.psl.invivo.nativedetector;


import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

/**
 * @author miriammelnick
 * TODO comment this
 */
public class CompleteClassVisitor extends ClassVisitor {
	
	private static Logger logger = Logger.getLogger(CompleteClassVisitor.class);
	private String className;
	LinkedList<MethodInstance> allMethods = new LinkedList<MethodInstance>();
	LinkedList<String> allMethodNames = new LinkedList<String>();
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

		NativeDetector.allMethods.add(mi);
		
	//	this.allMethods.add(mi);
	//	this.allMethodNames.add(mi.getMethod().getDescriptor());
	//	return new CallFindingMethodVisitor(access, super.visitMethod(access, name, desc, signature, exceptions), mi);
		return new DummyMethodVisitor(api, super.visitMethod(
				access, name, desc, signature, exceptions), mi); 
	}
	
}
