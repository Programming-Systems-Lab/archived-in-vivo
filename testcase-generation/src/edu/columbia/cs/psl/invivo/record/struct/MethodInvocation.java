package edu.columbia.cs.psl.invivo.record.struct;

import java.util.Stack;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class MethodInvocation implements IReadableInstance {
	@Override
	public String toString() {
		String r ="";
		if(method.getMethod().getName().equals("<init>"))
			r+="new "+method.getClazz()+"(";
		else
			r += method.getMethod().getName()+"(";

		for(int j = 0; j<params.size() - (method.getMethod().getName().equals("<init>") ? 2: 0); j++)
		{
			IReadableInstance i = params.get(j);
			IReadableInstance parent = i.getParent();
			String paramParent = "";
			while(parent != null)
			{
				paramParent = parent.toString()+"."+paramParent;
				parent = parent.getParent();
			}
			r+= paramParent;
			r += i.toString();
			if(j != params.size() - 1 - (method.getMethod().getName().equals("<init>") ? 2 : 0))
				r += ",";
		}
		r+=")";
		return r;

	}
	private IReadableInstance parent;
	private Stack<IReadableInstance> params = new Stack<IReadableInstance>();

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
		return (method.getMethod().getName().equals("<init>") ? 1 : 0) + method.getMethod().getArgumentTypes().length;
	}
}