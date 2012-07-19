package edu.columbia.cs.psl.invivo.record.struct;

import java.util.Stack;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.util.Printer;

public class ConstantInsnAdapter implements IReadableInstance {
	private AbstractInsnNode insn;

	@Override
	public int getOpcode() {
		return insn.getOpcode();
	}

	public ConstantInsnAdapter(AbstractInsnNode insn) {
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

	@Override
	public String toString() {
		return "[" + Printer.OPCODES[getOpcode()] + (getOpcode() == Opcodes.NEW ? " " + ((TypeInsnNode) insn).desc : "") + "]";
	}

}
