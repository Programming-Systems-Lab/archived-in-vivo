package edu.columbia.cs.psl.invivo.junit;

import java.lang.annotation.Annotation;

import org.objectweb.asm.ClassVisitor;

import edu.columbia.cs.psl.invivo.junit.annotation.Tested;
import edu.columbia.cs.psl.invivo.junit.compiler.JUnitTestRunnerGenerator;
import edu.columbia.cs.psl.invivo.junit.rewriter.JUnitTestCaseClassInspector;
import edu.columbia.cs.psl.invivo.junit.rewriter.JUnitTestCaseClassVisitor;
import edu.columbia.cs.psl.invivo.junit.rewriter.JUnitTestCaseMethodVisitor;
import edu.columbia.cs.psl.invivo.runtime.AbstractConfiguration;
import edu.columbia.cs.psl.invivo.runtime.AbstractInterceptor;
import edu.columbia.cs.psl.invivo.runtime.TestRunnerGenerator;
import edu.columbia.cs.psl.invivo.runtime.visitor.BuddyClassVisitor;

public class Configuration extends AbstractConfiguration<JUnitTestCaseClassInspector>{

	@Override
	public Class<? extends AbstractInterceptor> getInterceptorClass() {
		return Interceptor.class;
	}

	@Override
	public Class<? extends Annotation> getAnnotationClass() {
		return Tested.class;
	}
	@Override
	public ClassVisitor getAdditionalCV(int api, ClassVisitor cv) {
		return new JUnitTestCaseClassVisitor(api, cv);
	}
	@Override
	public JUnitTestCaseClassInspector getPreCV(int api, ClassVisitor cv) {
		return new JUnitTestCaseClassInspector(api, cv);
	}
	
	@Override
	public TestRunnerGenerator<JUnitTestCaseClassInspector> getTestRunnerGenerator(JUnitTestCaseClassInspector preClassVisitor) {
		return new JUnitTestRunnerGenerator(preClassVisitor);
	}
}
