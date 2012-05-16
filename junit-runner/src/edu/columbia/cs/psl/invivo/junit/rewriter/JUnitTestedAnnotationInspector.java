package edu.columbia.cs.psl.invivo.junit.rewriter;

import org.objectweb.asm.AnnotationVisitor;

import edu.columbia.cs.psl.invivo.junit.rewriter.JUnitInvivoMethodDescription.VariableReplacement;

public class JUnitTestedAnnotationInspector extends AnnotationVisitor{
	private JUnitTestCaseClassInspector inspectorParent;
	private JUnitInvivoMethodDescription method;
	public JUnitTestedAnnotationInspector(int api, AnnotationVisitor av, JUnitTestCaseClassInspector inspectorParent, JUnitInvivoMethodDescription jdesc) {
		super(api, av);
		this.method = jdesc;
		this.inspectorParent = inspectorParent;
	}

	@Override
	public AnnotationVisitor visitArray(String name) {
		return new AnnotationVisitor(api) {
			@Override
			public AnnotationVisitor visitAnnotation(String name, String desc) {
				return new AnnotationVisitor(api) {
					@Override
					public void visit(String name, Object value) {

						if("clazz".equals(name))
							method.clazz = value.toString();
						else if("method".equals(name))
							method.name = value.toString();
					}
					@Override
					public AnnotationVisitor visitArray(String name) {
						
						return new AnnotationVisitor(api) {
							@Override
							public AnnotationVisitor visitAnnotation(String name, String desc) {
								final VariableReplacement vr = new VariableReplacement(null);
								method.replacements.add(vr);
								return new AnnotationVisitor(api) {
									@Override
									public void visit(String name, Object value) {
										if(name.equals("from"))
											vr.from = (String) value;
										else if(name.equals("to"))
											vr.to = (String) value;
										super.visit(name, value);
									}
								};
							}
						};
					}
				};
			}
		};
	}
}
