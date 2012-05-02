package edu.columbia.cs.psl.invivo.nativedetector2;

import org.objectweb.asm.commons.Method;

public class MethodInstance {
	private Method method;
	private String clazz;
	public MethodInstance(Method method, String clazz) {
		this.method = method;
		this.clazz = clazz;
	}
	public Method getMethod() {
		return method;
	}
	public void setMethod(Method method) {
		this.method = method;
	}
	public String getClazz() {
		return clazz;
	}
	public void setClazz(String clazz) {
		this.clazz = clazz;
	}
	
}
