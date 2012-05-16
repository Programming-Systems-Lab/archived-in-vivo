package edu.columbia.cs.psl.invivo.junit.compiler;

import edu.columbia.cs.psl.invivo.junit.rewriter.JUnitInvivoMethodDescription;
import edu.columbia.cs.psl.invivo.junit.rewriter.JUnitTestCaseClassInspector;
import edu.columbia.cs.psl.invivo.runtime.TestRunnerGenerator;

public class JUnitTestRunnerGenerator extends TestRunnerGenerator<JUnitTestCaseClassInspector>{
	
	public JUnitTestRunnerGenerator(JUnitTestCaseClassInspector cv) {
		super(cv);
	}

	@Override
	public String generateTestRunner() {
		if(cv.getTestedMethods().size() > 0)
		{
			System.out.println(cv.getClassName());
			for(JUnitInvivoMethodDescription method : cv.getTestedMethods().keySet())
			{
				System.out.println(method);
			}
		}
		return null;
	}
}
