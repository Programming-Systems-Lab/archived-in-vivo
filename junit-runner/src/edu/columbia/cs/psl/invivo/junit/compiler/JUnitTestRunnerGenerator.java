package edu.columbia.cs.psl.invivo.junit.compiler;

import org.apache.log4j.Logger;

import edu.columbia.cs.psl.invivo.junit.rewriter.JUnitInvivoMethodDescription;
import edu.columbia.cs.psl.invivo.junit.rewriter.JUnitTestCaseClassInspector;
import edu.columbia.cs.psl.invivo.runtime.TestRunnerGenerator;

public class JUnitTestRunnerGenerator extends TestRunnerGenerator<JUnitTestCaseClassInspector>{
	
	public JUnitTestRunnerGenerator(JUnitTestCaseClassInspector cv) {
		super(cv);
	}

	private static Logger logger = Logger.getLogger(JUnitTestRunnerGenerator.class);
	@Override
	public String generateTestRunner() {
		if(cv.getTestedMethods().size() > 0)
		{	
			logger.info("Class name is " + cv.getClassName());
			for(JUnitInvivoMethodDescription method : cv.getTestedMethods().keySet())
			{
				logger.info(method);
			}
		}
		return null;
	}
}
