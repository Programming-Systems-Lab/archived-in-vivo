package edu.columbia.cs.psl.wallace.visitor;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import edu.columbia.cs.psl.wallace.WallaceExportRunner;

public class MainLoggingMethodVisitor extends AdviceAdapter {

	private String className;
	protected MainLoggingMethodVisitor(int api, MethodVisitor mv, int access, String name, String desc,String className) {
		super(api, mv, access, name, desc);
		this.className = className;
	}

	@Override
	protected void onMethodEnter() {
		super.onMethodEnter();
		visitLdcInsn(this.className);
		loadArg(0);
		super.invokeStatic(Type.getType(WallaceExportRunner.class), Method.getMethod("void logMain(String, String[])"));
	}
}
