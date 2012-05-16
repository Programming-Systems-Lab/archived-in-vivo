package edu.columbia.cs.psl.invivo.junit.rewriter;


import org.apache.log4j.Logger;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import edu.columbia.cs.psl.invivo.junit.annotation.InvivoTest;
import edu.columbia.cs.psl.invivo.junit.rewriter.JUnitInvivoMethodDescription.VariableReplacement;
import edu.columbia.cs.psl.invivo.runtime.visitor.BuddyClassVisitor;

public class JUnitTestCaseClassVisitor extends BuddyClassVisitor<JUnitTestCaseClassInspector> {
	private static Logger logger = Logger.getLogger(JUnitTestCaseClassVisitor.class);
	public JUnitTestCaseClassVisitor(int api, ClassVisitor cv) {
		super(api, cv);
	}

	String className;
	@Override
	public void visitOuterClass(String owner, String name, String desc) {
		className = name;
		super.visitOuterClass(owner, name, desc);
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		if(getBuddy().getMethodsFlagged().containsKey(new JUnitInvivoMethodDescription(name, desc)) && getBuddy().getMethodsFlagged().get(new JUnitInvivoMethodDescription(name, desc)).isTestCase)
		{
			JUnitInvivoMethodDescription method = getBuddy().getMethodsFlagged().get(new JUnitInvivoMethodDescription(name, desc));
			Type[] existingArgs = Type.getArgumentTypes(desc);
			Type[] newArgs = new Type[existingArgs.length + method.replacements.size()];
			for(int i = 0; i < existingArgs.length; i++)
				newArgs[i] = existingArgs[i];
			int i = existingArgs.length;
			for(VariableReplacement s : method.replacements)
			{
				if(s.from == null)
				{
					logger.error("ABORTING - Invalid local variable replacement ("+s.from+") specified in class " + this.className + " method " + name);
					return super.visitMethod(access, name, desc, signature, exceptions);
				}
				s.argIndx = i;
				newArgs[i] = s.type;
				i++;
			}
			desc = Type.getMethodDescriptor(Type.getReturnType(desc), newArgs);
			return new JUnitTestCaseMethodVisitor(access, super.visitMethod(access, name, desc, signature, exceptions), access, name, desc,method);
		}
		return super.visitMethod(access, name, desc, signature, exceptions);
	}
	
	@Override
	public void visitEnd() {
		//Generate the new test case launcher class which will call the test case method with the appropriate parameters
		//getBuddy().getMethodsFlagged() will return the methods that are test methods and what the rewrite parameters are
		super.visitEnd();
	}
}
