package v2;

import java.util.HashSet;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.Method;

public class FirstClassVisitor extends ClassVisitor {
	private String name;
	static HashSet<Method> methods = new HashSet<Method>();

	public FirstClassVisitor(int api, ClassVisitor cv, String name) {
		super(api, cv);
		this.name = name;
	}

	
	@Override
	public MethodVisitor visitMethod(int access, String name, String desc,
			String signature, String[] exceptions) {
		return new FirstMethodVisitor(api, super.visitMethod(
				access, name, desc, signature, exceptions), name, desc, this.name);
	}
}
