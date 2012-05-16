package edu.columbia.cs.psl.invivo.junit.rewriter;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.LocalVariablesSorter;

import edu.columbia.cs.psl.invivo.junit.annotation.InvivoTest;
import edu.columbia.cs.psl.invivo.junit.annotation.Tested;
import edu.columbia.cs.psl.invivo.junit.rewriter.JUnitInvivoMethodDescription.VariableReplacement;

public class JUnitTestCaseMethodInspector extends MethodVisitor {

	private JUnitTestCaseClassInspector classInspector;
	private String name;
	private String desc;
	public JUnitTestCaseMethodInspector(int api, int access, MethodVisitor mv, JUnitTestCaseClassInspector classInspector, String name, String desc) {
		super(api,mv);
		this.classInspector= classInspector;
		this.name = name;
		this.desc = desc;
	}
	@Override
	public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
		if(identifyVariableTypes)
		{
			JUnitInvivoMethodDescription jdesc = classInspector.getMethodsFlagged().get(new JUnitInvivoMethodDescription(this.name, this.desc));
			for(VariableReplacement s : jdesc.replacements)
			{
				if(s.from.equals(name))
				{
					s.indx = index;
					s.type = Type.getType(desc);
				}
			}
		}
		super.visitLocalVariable(name, desc, signature, start, end, index);
	}
	@Override
	public void visitVarInsn(int opcode, int var) {
		super.visitVarInsn(opcode, var);
	}
	boolean identifyVariableTypes;
	
	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		if(desc.equals(Type.getDescriptor(InvivoTest.class)))
		{
			identifyVariableTypes = true;
			JUnitInvivoMethodDescription jdesc = new JUnitInvivoMethodDescription(this.name, this.desc);
			classInspector.addMethodToProcess(jdesc);
			return new JUnitTestCaseAnnotationInspector(api, super.visitAnnotation(desc, visible),classInspector,jdesc);
		}
		else if(desc.equals(Type.getDescriptor(Tested.class)))
		{
			JUnitInvivoMethodDescription jdesc = new JUnitInvivoMethodDescription(null,null);
			classInspector.addTestedMethod(jdesc);
			return new JUnitTestedAnnotationInspector(api, super.visitAnnotation(desc, visible),classInspector,jdesc);
		}
		return super.visitAnnotation(desc, visible);
	}
}
