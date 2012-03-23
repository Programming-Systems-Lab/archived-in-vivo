package edu.columbia.cs.psl.invivo.junit;

import java.lang.annotation.Annotation;

import edu.columbia.cs.psl.invivo.runtime.AbstractConfiguration;
import edu.columbia.cs.psl.invivo.runtime.AbstractInterceptor;

public class Configuration extends AbstractConfiguration{

	@Override
	public Class<? extends AbstractInterceptor> getInterceptorClass() {
		return Interceptor.class;
	}

	@Override
	public Class<? extends Annotation> getAnnotationClass() {
		return Tested.class;
	}

}
