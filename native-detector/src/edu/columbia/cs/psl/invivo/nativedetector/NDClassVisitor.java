package edu.columbia.cs.psl.invivo.nativedetector;


import java.util.LinkedList;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

/**
 * @author miriammelnick
 * TODO comment this
 */
public class NDClassVisitor extends ClassVisitor {
	
	private String className;

//	public LinkedList<MethodInstance> allMethods = new LinkedList<MethodInstance>();
	
	/**
	 * TODO comment
	 * @param api
	 * @param cv
	 * @param name
	 */
	public NDClassVisitor(int api, ClassVisitor cv, String name) {
		super(api, cv);
		this.className = name;
	}

	/**
	 * We are seeing method A.x for the first time. Add it to methodMap.
	 */
	@Override
	public MethodVisitor visitMethod(int access, String name, String desc,
			String signature, String[] exceptions) {

		MethodInstance mi = new MethodInstance(name, desc, this.className, access);
		NativeDetector.methodMap.put(mi.getFullName(), mi);
		
//		this.allMethods.add(mi);
		
		return new NDMethodVisitor(api, super.visitMethod(
				access, name, desc, signature, exceptions), mi.getFullName()); 
	}
	
}
