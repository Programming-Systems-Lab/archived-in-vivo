package mrm.nativedetector;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class NDClassVisitor extends ClassVisitor {

	public NDClassVisitor(int api) {
		super(api);
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc,
			String signature, String[] exceptions) {
		EngineTest.methodCount++;
		return new NDMethodVisitor(Opcodes.ASM4, 
				super.visitMethod(access, name, desc, signature, exceptions));
	}
	
	
}
