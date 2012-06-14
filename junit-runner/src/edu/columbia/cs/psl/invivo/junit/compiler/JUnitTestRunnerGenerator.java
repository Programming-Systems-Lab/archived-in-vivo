package edu.columbia.cs.psl.invivo.junit.compiler;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import edu.columbia.cs.psl.invivo.junit.rewriter.JUnitInvivoMethodDescription;
import edu.columbia.cs.psl.invivo.junit.rewriter.JUnitInvivoMethodDescription.VariableReplacement;
import edu.columbia.cs.psl.invivo.junit.rewriter.JUnitTestCaseClassInspector;
import edu.columbia.cs.psl.invivo.runtime.TestRunnerGenerator;
import edu.columbia.psl.invivoexpreval.asmeval.InVivoClassDesc;
import edu.columbia.psl.invivoexpreval.asmeval.InVivoMethodDesc;
import edu.columbia.psl.invivoexpreval.asmeval.InVivoVariableReplacement;

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
	
	@Override
	public InVivoClassDesc getClsDesc() {
		InVivoClassDesc dsc = new InVivoClassDesc();
		dsc.setClassName(cv.getClassName());
		for (JUnitInvivoMethodDescription m : cv.getTestedMethods().keySet()) {
			InVivoMethodDesc mDesc = new InVivoMethodDesc();
			mDesc.setMethodDesc(m.desc);
			mDesc.setMethodName(m.name);
			mDesc.setMethodTestClass(m.testMethodClass);
			mDesc.setMethodTestMethod(m.testMethodName);
			List<InVivoVariableReplacement> vrs = new ArrayList<InVivoVariableReplacement>();
			for (VariableReplacement vr : m.replacements) {
				InVivoVariableReplacement newVr = new InVivoVariableReplacement();
				newVr.setArgIndx(vr.argIndx);
				newVr.setFrom(vr.from);
				newVr.setIndx(vr.indx);
				newVr.setTo(vr.to);
				newVr.setType(vr.type);
				vrs.add(newVr);
			}
			dsc.addMethod(mDesc, vrs);
		}
		return dsc;
	}
}
