package v2;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.Method;

public class FirstMethodVisitor extends MethodVisitor{

	public FirstMethodVisitor(int api, MethodVisitor mv, String method, String desc, String clazz) {
		super(api, mv);
	}

	@Override
	public void visitMethodInsn(int opcode, String owner, String name,
			String desc) {
		FirstClassVisitor.methods.add(new Method(name, desc));
		super.visitMethodInsn(opcode, owner, name, desc);
	}
}
