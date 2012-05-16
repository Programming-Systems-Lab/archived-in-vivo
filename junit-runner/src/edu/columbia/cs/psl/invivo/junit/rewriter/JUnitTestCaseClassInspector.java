package edu.columbia.cs.psl.invivo.junit.rewriter;

import java.util.HashMap;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class JUnitTestCaseClassInspector extends ClassVisitor {
	private String className;
	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		this.className = name;
		super.visit(version, access, name, signature, superName, interfaces);
	}
	public String getClassName() {
		return className;
	}
	public JUnitTestCaseClassInspector(int api, ClassVisitor cv) {
		super(api, cv);
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		JUnitTestCaseMethodInspector inspector = new JUnitTestCaseMethodInspector(api,  access, super.visitMethod(access, name, desc, signature, exceptions),this,name,desc);
		return inspector;
	}
	private HashMap<JUnitInvivoMethodDescription,JUnitInvivoMethodDescription> methodsFlagged = new HashMap<JUnitInvivoMethodDescription,JUnitInvivoMethodDescription>();
	public void addMethodToProcess(JUnitInvivoMethodDescription method)
	{
		methodsFlagged.put(method,method);
	}
	public HashMap<JUnitInvivoMethodDescription,JUnitInvivoMethodDescription> getMethodsFlagged() {
		return methodsFlagged;
	}
	
	private HashMap<JUnitInvivoMethodDescription,JUnitInvivoMethodDescription> testedMethods = new HashMap<JUnitInvivoMethodDescription,JUnitInvivoMethodDescription>();
	public void addTestedMethod(JUnitInvivoMethodDescription method)
	{
		testedMethods.put(method,method);
	}
	public HashMap<JUnitInvivoMethodDescription,JUnitInvivoMethodDescription> getTestedMethods() {
		return testedMethods;
	}
}
