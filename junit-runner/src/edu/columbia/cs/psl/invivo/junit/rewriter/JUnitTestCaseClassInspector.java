package edu.columbia.cs.psl.invivo.junit.rewriter;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import edu.columbia.cs.psl.invivo.junit.rewriter.JUnitInvivoMethodDescription.VariableReplacement;

public class JUnitTestCaseClassInspector extends ClassVisitor {
	private static Logger	logger	= Logger.getLogger(JUnitTestCaseClassInspector.class);
	private String			className;

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
		JUnitTestCaseMethodInspector inspector = new JUnitTestCaseMethodInspector(api, access, super.visitMethod(access, name, desc, signature,
				exceptions), this, name, desc, this.className);
		return inspector;
	}

	private HashMap<JUnitInvivoMethodDescription, JUnitInvivoMethodDescription>	methodsFlagged	= new HashMap<JUnitInvivoMethodDescription, JUnitInvivoMethodDescription>();

	public void addMethodToProcess(JUnitInvivoMethodDescription method) {

		methodsFlagged.put(method, method);
	}

	public HashMap<JUnitInvivoMethodDescription, JUnitInvivoMethodDescription> getMethodsFlagged() {
		return methodsFlagged;
	}

	private HashMap<JUnitInvivoMethodDescription, JUnitInvivoMethodDescription>	testedMethods	= new HashMap<JUnitInvivoMethodDescription, JUnitInvivoMethodDescription>();

	public void addTestedMethod(JUnitInvivoMethodDescription method) {
		testedMethods.put(method, method);
	}

	private HashMap<String, ArrayList<JUnitInvivoMethodDescription>>	testMethods;

	public HashMap<JUnitInvivoMethodDescription, JUnitInvivoMethodDescription> getTestedMethods() {
		if (testMethods == null) {
			testMethods = new HashMap<String, ArrayList<JUnitInvivoMethodDescription>>();

			for (JUnitInvivoMethodDescription d : testedMethods.keySet()) {
				if (!testMethods.containsKey(d.testMethodClass)) {
					try {
						ClassReader cr = new ClassReader(d.testMethodClass);
						JUnitTestCaseClassInspector testCaseInspector = new JUnitTestCaseClassInspector(api, null);
						cr.accept(testCaseInspector, 0);
						testMethods.put(d.testMethodClass, new ArrayList<JUnitInvivoMethodDescription>());
						testMethods.get(d.testMethodClass).addAll(testCaseInspector.getMethodsFlagged().values());
					} catch (Exception ex) {
						logger.error("Unable to find test case info ", ex);
					}
				}
				JUnitInvivoMethodDescription testCaseDescription = null;
				for (JUnitInvivoMethodDescription desc : testMethods.get(d.testMethodClass)) {
					if (desc.name.equals(d.testMethodName))
						testCaseDescription = desc;
				}
				if (testCaseDescription == null) {
					logger.error("Unable to find descriptive info for test case " + d);
				} else {
					d.desc = testCaseDescription.desc;
					for (VariableReplacement vr : d.replacements) {
						for(VariableReplacement z : testCaseDescription.replacements)
							if(z.from.equals(vr.from))
								vr.type = z.type;
					}
				}
			}

		}
		
		return testedMethods;
	}
}
