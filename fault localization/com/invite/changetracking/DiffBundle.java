package com.invite.changetracking;

public class DiffBundle {
	String methodName;
	DiffValue[] diffVals;
	
	DiffBundle( String methodName, DiffValue[] diffVals ) {
		this.methodName = methodName;
		this.diffVals = diffVals;
	}
	
	public String getMethodName() {
		return methodName;
	}
	
	public DiffValue[] getDiffVals() {
		return diffVals;
	}
}
