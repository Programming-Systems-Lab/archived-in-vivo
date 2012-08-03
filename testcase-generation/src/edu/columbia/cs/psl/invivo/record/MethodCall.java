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
	private boolean isStatic;
	public MethodCall(String sourceMethodName, String sourceMethodDesc, String sourceClass, int pc, int lineNumber, String methodOwner, String methodName, String methodDesc, boolean isStatic) {
		this.sourceMethodName = sourceMethodName;
		this.sourceMethodDesc = sourceMethodDesc;
		this.sourceClass = sourceClass;
		this.pc = pc;
		this.lineNumber = lineNumber;
		this.methodOwner = methodOwner;
		this.methodName = methodName;
		this.methodDesc = methodDesc;
		this.isStatic = isStatic;
	}
	public boolean isStatic() {
		return isStatic;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + lineNumber;
		result = prime * result + ((methodDesc == null) ? 0 : methodDesc.hashCode());
		result = prime * result + ((methodName == null) ? 0 : methodName.hashCode());
		result = prime * result + ((methodOwner == null) ? 0 : methodOwner.hashCode());
		result = prime * result + pc;
		result = prime * result + ((sourceClass == null) ? 0 : sourceClass.hashCode());
		result = prime * result + ((sourceMethodName == null) ? 0 : sourceMethodName.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MethodCall other = (MethodCall) obj;
		if(other.getLogFieldName().equals(this.getLogFieldName()) && other.sourceClass.equals(this.sourceClass))
			return true;
		return false;
	}
	public String getLogFieldName()
	{
		Type[] args = Type.getArgumentTypes(methodDesc);
		String r = sourceMethodName.replace("<", "___").replace(">", "___")+"$$$$"+methodName+"$$$$";
//		for(Type t : args)
//		{
//			r+=t.getInternalName().replace("/", "$")+"$$";
//		}
		r += lineNumber+ "$"+pc;
		return r;

	}
	public Type getLogFieldType() {
		return Type.getType("["+Type.getMethodType(methodDesc).getReturnType().getDescriptor());
	}
}
