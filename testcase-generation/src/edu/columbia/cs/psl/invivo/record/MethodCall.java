package edu.columbia.cs.psl.invivo.record;

import org.objectweb.asm.Type;

public class MethodCall {
	private String sourceMethodName;
	private String sourceMethodDesc;
	private String sourceClass;
	private int pc;
	private int lineNumber;
	private String methodOwner;
	private String methodName;
	private String methodDesc;
	public MethodCall(String sourceMethodName, String sourceMethodDesc, String sourceClass, int pc, int lineNumber, String methodOwner, String methodName, String methodDesc) {
		this.sourceMethodName = sourceMethodName;
		this.sourceMethodDesc = sourceMethodDesc;
		this.sourceClass = sourceClass;
		this.pc = pc;
		this.lineNumber = lineNumber;
		this.methodOwner = methodOwner;
		this.methodName = methodName;
		this.methodDesc = methodDesc;
	}
	public String getSourceMethodName() {
		return sourceMethodName;
	}
	public String getSourceMethodDesc() {
		return sourceMethodDesc;
	}
	public String getSourceClass() {
		return sourceClass;
	}
	public int getPc() {
		return pc;
	}
	public int getLineNumber() {
		return lineNumber;
	}
	public String getMethodOwner() {
		return methodOwner;
	}
	public String getMethodName() {
		return methodName;
	}
	public String getMethodDesc() {
		return methodDesc;
	}

	public String getLogFieldName()
	{
//		Type[] args = Type.getArgumentTypes(methodDesc);
		String r = Constants.LOGGED_CALL_PREFIX+sourceMethodName+"_"+methodName+"_";
//		for(Type t : args)
//		{
//			r+=t.getInternalName().replace("/", "$")+"$$";
//		}
		r += lineNumber+ "."+pc;
		return r;

	}
	public Type getLogFieldType() {
		return Type.getType("["+Type.getMethodType(methodDesc).getReturnType().getDescriptor());
	}
}
