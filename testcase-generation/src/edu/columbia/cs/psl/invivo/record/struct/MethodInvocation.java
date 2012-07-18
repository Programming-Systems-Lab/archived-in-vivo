package edu.columbia.cs.psl.invivo.record.struct;

import java.util.Stack;

public class MethodInvocation implements IReadableInstance {
	@Override
	public String toString() {
		return "MethodInvocation [method=" + method + "]";
	}
	private IReadableInstance parent;
	private Stack<IReadableInstance> params;

	private MethodInstance method;
	private int opcode;
	public MethodInvocation(MethodInstance method, int opcode)
	{

		this.method = method;
		this.opcode = opcode;
	}
	public int getOpcode() {
		return opcode;
	}
	@Override
	public int getType() {
		return METHOD_TYPE;
	}
	public Stack<IReadableInstance> getParams() {
		return params;
	}
	@Override
	public IReadableInstance getParent() {
		return parent;
	}
	public MethodInstance getMethod() {
		return method;
	}
	public int getNumParamsNeeded()
	{
		return (method.getMethod().getName().equals("<init>") ? 2 : 0) + method.getMethod().getArgumentTypes().length;
	}
	@Override
	public void setParent(IReadableInstance parent) {
		this.parent = parent;
	}
	public boolean hasAllParameters() {
		// TODO Auto-generated method stub
		return getNumParamsNeeded() == params.size();
	}
	@Override
	public int getStackElementsToSkip() {
		return getNumParamsNeeded();
	}
}