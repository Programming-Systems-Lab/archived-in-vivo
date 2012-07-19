package edu.columbia.cs.psl.invivo.record.struct;

import java.util.LinkedList;
import java.util.Stack;

import org.objectweb.asm.Opcodes;

public class FieldInvocation implements IReadableInstance {
	private String name;
	private String owner;
	private String desc;
	private IReadableInstance parent;
	private int opcode;
	
	public FieldInvocation(String name, String owner, String desc, int opcode)
	{
		this.name = name;
		this.owner = owner;
		this.desc = desc;
		this.opcode = opcode;
	}
	public int getOpcode() {
		return opcode;
	}
	public String getName() {
		return name;
	}
	public String getOwner() {
		return owner;
	}
	public String getDesc() {
		return desc;
	}
	public IReadableInstance getParent() {
		return parent;
	}
	@Override
	public int getType() {
		return FIELD_TYPE;
	}
	public void setParent(IReadableInstance parent) {
		this.parent = parent;
	}
	@Override
	public int getStackElementsToSkip() {
		if(opcode == Opcodes.GETFIELD || opcode == Opcodes.GETSTATIC)
			return 0;
		return 1;
	}

	@Override
	public String toString() {
//		return "FieldInvocation [name=" + name + ", owner=" + owner + ", desc=" + desc + "]";
		return name;
	}
}
