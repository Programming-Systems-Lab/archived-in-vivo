package edu.columbia.cs.psl.invivo.nativedetector;

import java.util.HashMap;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

public class NDClassVisitor extends ClassVisitor {

	private String className;
	private HashMap<String, MethodInstance> lookupCache;

	public NDClassVisitor(int api, ClassVisitor cv, HashMap<String, MethodInstance> lookupCache) {
		super(api, cv);
		this.lookupCache = lookupCache;
	}

	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		this.className = name;
	}

	/**
	 * We are seeing method A.x for the first time. Add it to methodMap.
	 */
	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {

		MethodInstance mi = new MethodInstance(name, desc, this.className, access);
		if (lookupCache.containsKey(mi.getFullName()))
			lookupCache.get(mi.getFullName()).setAccess(access);
		else
			lookupCache.put(mi.getFullName(), mi);
		mi = lookupCache.get(mi.getFullName());

		if ((className.startsWith("java/io") || className.startsWith("java/lang/Readable.")) && !className.startsWith("java/io/String"))
			mi.forceNative();
		if (NativeDetector.deterministicNativeMethods.contains(mi.getFullName()))
			mi.setAccess(0);
		if (mi.isNative())
			mi.setNonDeterministic(true);
		return new NDMethodVisitor(api, null, mi.getFullName(), access, lookupCache);
	}

}
