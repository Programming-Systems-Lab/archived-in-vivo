package edu.columbia.cs.psl.invivo.junit;

import java.lang.annotation.Annotation;

import org.objectweb.asm.ClassVisitor;

import edu.columbia.cs.psl.invivo.junit.annotation.Tested;
import edu.columbia.cs.psl.invivo.junit.rewriter.JUnitTestCaseClassInspector;
import edu.columbia.cs.psl.invivo.junit.rewriter.JUnitTestCaseClassVisitor;
import edu.columbia.cs.psl.invivo.junit.rewriter.JUnitTestCaseMethodVisitor;
import edu.columbia.cs.psl.invivo.runtime.AbstractConfiguration;
import edu.columbia.cs.psl.invivo.runtime.AbstractInterceptor;
import edu.columbia.cs.psl.invivo.runtime.visitor.BuddyClassVisitor;

public class Configuration extends AbstractConfiguration{

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
	public ClassVisitor getPreCV(int api, ClassVisitor cv) {
		return new JUnitTestCaseClassInspector(api, cv);
	}
}
