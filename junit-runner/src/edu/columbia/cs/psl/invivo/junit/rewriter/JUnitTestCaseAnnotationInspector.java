package edu.columbia.cs.psl.invivo.junit.rewriter;

import org.objectweb.asm.AnnotationVisitor;

import edu.columbia.cs.psl.invivo.junit.rewriter.JUnitInvivoMethodDescription.VariableReplacement;

public class JUnitTestCaseAnnotationInspector extends AnnotationVisitor{
	private JUnitTestCaseClassInspector inspectorParent;
	private JUnitInvivoMethodDescription method;
	public JUnitTestCaseAnnotationInspector(int api, AnnotationVisitor av, JUnitTestCaseClassInspector inspectorParent, JUnitInvivoMethodDescription jdesc) {
		super(api, av);
		this.method = jdesc;
		this.inspectorParent = inspectorParent;
	}

	@Override
	public AnnotationVisitor visitArray(String name) {
		return new AnnotationVisitor(api) {
			@Override
			public void visit(String name, Object value) {
				method.replacements.add(new VariableReplacement((String) value));
			}
		};
	}
}
