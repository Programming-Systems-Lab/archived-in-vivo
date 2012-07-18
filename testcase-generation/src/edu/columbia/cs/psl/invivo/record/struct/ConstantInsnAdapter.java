package edu.columbia.cs.psl.invivo.record.struct;

import java.util.Stack;

import org.objectweb.asm.tree.AbstractInsnNode;

public class ConstantInsnAdapter implements IReadableInstance{
	private AbstractInsnNode insn;
	@Override
	public int getOpcode() {
		return insn.getOpcode();
	}
	public ConstantInsnAdapter(AbstractInsnNode insn)
	{
		this.insn = insn;
	}
	public AbstractInsnNode getInsn() {
		return insn;
	}
	@Override
	public IReadableInstance getParent() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setParent(IReadableInstance ir) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public int getType() {
		return CONSTANT_TYPE;
	}
	@Override
	public int getStackElementsToSkip() {
		return 0;
	}
}
