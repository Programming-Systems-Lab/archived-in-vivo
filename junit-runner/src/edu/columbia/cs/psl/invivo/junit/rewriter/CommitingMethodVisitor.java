package edu.columbia.cs.psl.invivo.junit.rewriter;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

public class CommitingMethodVisitor extends MethodVisitor implements Opcodes {

	protected CommitingMethodVisitor(int api, MethodVisitor mv, int access, String name, String desc) {
		super(api,mv);
	}
	private interface StalledInstruction{
		public void execute();
	};
	private class visitAttribute implements StalledInstruction {
		Attribute attr;

		public visitAttribute(Attribute attr) {
			this.attr = attr;
		}
		public void execute()
		{
			mv.visitAttribute(attr);
		}
	};
	private class visitCode implements StalledInstruction {
		@Override
		public void execute() {
			mv.visitCode();
		}
	};
	private class visitEnd implements StalledInstruction {
		@Override
		public void execute() {
			mv.visitEnd();
		}
	};
	private class visitFieldInsn implements StalledInstruction {
		int opcode; String owner; String name; String desc;
		public visitFieldInsn(int opcode, String owner, String name, String desc) {
			this.opcode = opcode;
			this.owner = owner;
			this.name = name;
			this.desc = desc;
		}
		@Override
		public void execute() {
			mv.visitFieldInsn(opcode, owner, name, desc);
		}
	};
	private class visitFrame implements StalledInstruction {
		int type; int nLocal; Object[] local; int nStack; Object[] stack;
		public visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
			super();
			this.type = type;
			this.nLocal = nLocal;
			this.local = local;
			this.nStack = nStack;
			this.stack = stack;
		}
		@Override
		public void execute() {
			mv.visitFrame(type, nLocal, local, nStack, stack);
		}
	};
	private class visitIincInsn implements StalledInstruction {
		int var; int increment;
		public visitIincInsn(int var, int increment) {
			this.var = var;
			this.increment = increment;
		}
		@Override
		public void execute() {
			mv.visitIincInsn(var, increment);
		}
	};
	private class visitInsn implements StalledInstruction {
		int opcode;

		public visitInsn(int opcode) {
			this.opcode = opcode;
		}
		@Override
		public void execute() {
			mv.visitInsn(opcode);
		}
	};
	private class visitIntInsn implements StalledInstruction {
		int opcode; int operand;
		public visitIntInsn(int opcode, int operand) {
			this.opcode = opcode;
			this.operand = operand;
		}
		@Override
		public void execute() {
			mv.visitIntInsn(opcode, operand);
		}
	};
	private class visitInvokeDynamicInsn implements StalledInstruction {
		String name; String desc; Handle bsm; Object[] bsmArgs;
		public visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object[] bsmArgs) {
			this.name = name;
			this.desc = desc;
			this.bsm = bsm;
			this.bsmArgs = bsmArgs;
		}
		@Override
		public void execute() {
			mv.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs);
		}
	};
	private class visitJumpInsn implements StalledInstruction {
		int opcode; Label label;

		public visitJumpInsn(int opcode, Label label) {
			this.opcode = opcode;
			this.label = label;
		}
		@Override
		public void execute() {
			mv.visitJumpInsn(opcode, label);
		}
	};
	private class visitLabel implements StalledInstruction {
		Label label;

		public visitLabel(Label label) {
			this.label = label;
		}
		@Override
		public void execute() {
			mv.visitLabel(label);
		}
	};
	private class visitLdcInsn implements StalledInstruction {
		Object cst;

		public visitLdcInsn(Object cst) {
			this.cst = cst;
		}
		@Override
		public void execute() {
			mv.visitLdcInsn(cst);
		}
	};
	private class visitLineNumber implements StalledInstruction {
		int line; Label start;
		public visitLineNumber(int line, Label start) {
			this.line = line;
			this.start = start;
		}
		@Override
		public void execute() {
			mv.visitLineNumber(line, start);
		}
	};
	private class visitLocalVariable implements StalledInstruction {
		String name; String desc; String signature; Label start; Label end; int index;
		public visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
			this.name = name;
			this.desc = desc;
			this.signature = signature;
			this.start = start;
			this.end = end;
			this.index = index;
		}
		@Override
		public void execute() {
			mv.visitLocalVariable(name, desc, signature, start, end, index);
		}
	};
	private class visitLookupSwitchInsn implements StalledInstruction {
		Label dflt; int[] keys; Label[] labels;
		public visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
			this.dflt = dflt;
			this.keys = keys;
			this.labels = labels;
		}
		@Override
		public void execute() {
			mv.visitLookupSwitchInsn(dflt, keys, labels);
		}
	};
	private class visitMaxs implements StalledInstruction {
		int maxStack; int maxLocals;

		public visitMaxs(int maxStack, int maxLocals) {
			this.maxStack = maxStack;
			this.maxLocals = maxLocals;
		}
		@Override
		public void execute() {
			mv.visitMaxs(maxStack, maxLocals);
		}
		
	};
	private class visitMethodInsn implements StalledInstruction {
		int opcode; String owner; String name; String desc;

		public visitMethodInsn(int opcode, String owner, String name, String desc) {
			this.opcode = opcode;
			this.owner = owner;
			this.name = name;
			this.desc = desc;
		}
		@Override
		public void execute() {
			mv.visitMethodInsn(opcode, owner, name, desc);
		}
	};
	private class visitMultiANewArrayInsn implements StalledInstruction {
		String desc; int dims;

		public visitMultiANewArrayInsn(String desc, int dims) {
			this.desc = desc;
			this.dims = dims;
		}
		@Override
		public void execute() {
			mv.visitMultiANewArrayInsn(desc, dims);
		}
	};
	private class visitTableSwitchInsn implements StalledInstruction {
		int min; int max; Label dflt; Label[] labels;

		public visitTableSwitchInsn(int min, int max, Label dflt, Label[] labels) {
			this.min = min;
			this.max = max;
			this.dflt = dflt;
			this.labels = labels;
		}
		@Override
		public void execute() {
			mv.visitTableSwitchInsn(min, max, dflt, labels);
		}
	};
	private class visitTryCatchBlock implements StalledInstruction {
		Label start; Label end; Label handler; String type;

		public visitTryCatchBlock(Label start, Label end, Label handler, String type) {
			this.start = start;
			this.end = end;
			this.handler = handler;
			this.type = type;
		}
		@Override
		public void execute() {
			mv.visitTryCatchBlock(start, end, handler, type);
		}
	};
	private class visitTypeInsn implements StalledInstruction {
		int opcode; String type;

		public visitTypeInsn(int opcode, String type) {
			this.opcode = opcode;
			this.type = type;
		}
		@Override
		public void execute() {
			mv.visitTypeInsn(opcode, type);
		}
	};
	private class visitVarInsn implements StalledInstruction {
		int opcode; int var;

		public visitVarInsn(int opcode, int var) {
			this.opcode = opcode;
			this.var = var;
		}
		@Override
		public void execute() {
			mv.visitVarInsn(opcode, var);
		}
	};
	public void commit()
	{
		while(!instructionsToGo.isEmpty())
		{
			StalledInstruction i = instructionsToGo.remove();
			i.execute();
		}
	}
	public void removeLastPush()
	{
		int numPushesToRemove = 1;
		while(numPushesToRemove > 0)
		{
			StalledInstruction ins = instructionsToGo.removeLast();
			numPushesToRemove--;
			if(ins instanceof visitTypeInsn)
			{
				switch(((visitTypeInsn) ins).opcode)
				{
				case Opcodes.CHECKCAST:
					numPushesToRemove++; //This is not the droid you are looking for
				}
			}
			else if (ins instanceof visitVarInsn)
			{
				numPushesToRemove++;
			}
			else if(ins instanceof visitFieldInsn)
			{
				int opcode = ((visitFieldInsn) ins).opcode;
				if(opcode == Opcodes.GETFIELD || opcode == Opcodes.PUTFIELD)
					numPushesToRemove++; //Remove "this"
			}
			else if(ins instanceof visitMultiANewArrayInsn)
			{
				numPushesToRemove += ((visitMultiANewArrayInsn) ins).dims;
			}
			else if(ins instanceof visitInsn)
			{
				int opcode = ((visitInsn) ins).opcode;
				if((opcode >= Opcodes.INEG && opcode <= Opcodes.DNEG)
						|| opcode == Opcodes.IINC) // The 1-op math opcodes
				{
					numPushesToRemove ++;
				}
				else if(opcode >= Opcodes.IADD && opcode <= Opcodes.LXOR) //The rest of the "math" opcodes are 2-stack-operators
					numPushesToRemove+=2;
				else if(opcode >= Opcodes.I2L && opcode <= Opcodes.I2S) //The conversions
					numPushesToRemove++;
				else if(opcode >= Opcodes.LCMP && opcode<= Opcodes.DCMPG) //Comparisons
					numPushesToRemove+=2;
				else if((opcode >= IALOAD && opcode <= SALOAD))
					numPushesToRemove+=2;
				else if(opcode >= IASTORE && opcode <= SASTORE)
					numPushesToRemove+=3;
				else if(opcode == Opcodes.ARRAYLENGTH)
					numPushesToRemove++;
				else if(opcode == DUP || opcode == DUP_X1 || opcode == DUP_X2)
					numPushesToRemove++;
				else if(opcode == DUP2 || opcode == DUP2_X1 || opcode == DUP2_X2)
					numPushesToRemove+=2;
				else if(opcode == SWAP)
					numPushesToRemove++;
				else if(opcode >= INEG && opcode <= DNEG)
					numPushesToRemove++;
			}
			else if(ins instanceof visitMethodInsn)
			{
				visitMethodInsn call = (visitMethodInsn) ins;
				switch(call.opcode)
				{
				case Opcodes.INVOKESTATIC:
					break;
//				case Opcodes.INVOKEVIRTUAL:
//					numPushesToRemove++; //Remove "THIS"
//				case Opcodes.INVOKEINTERFACE:
//					numPushesToRemove++; //Remove "THIS"
				case Opcodes.INVOKESPECIAL:
					if(call.name.equals("<init>"))
					{
						numPushesToRemove++; //Because for "NEW X" we do a dup()
					}
				default:
					numPushesToRemove++; //Removes "THIS" or whoever we are calling on
				}
				
				//Remove the instructions that load the arguments
				numPushesToRemove += Type.getArgumentTypes(call.desc).length;
			}
		}
//		instructionsToGo.removeLast()
	}
	private LinkedList<StalledInstruction> instructionsToGo = new LinkedList<CommitingMethodVisitor.StalledInstruction>();
	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		return null;
		//		return super.visitAnnotation(desc, visible);
	}
	@Override
	public AnnotationVisitor visitAnnotationDefault() {
		return null;
//		return super.visitAnnotationDefault();
	}
	@Override
	public void visitAttribute(Attribute attr) {
		instructionsToGo.add(new visitAttribute(attr));
	}
	@Override
	public void visitCode() {
		instructionsToGo.add(new visitCode());
	}
	@Override
	public void visitEnd() {
		instructionsToGo.add(new visitEnd());
	}
	@Override
	public void visitFieldInsn(int opcode, String owner, String name, String desc) {
		
		instructionsToGo.add(new visitFieldInsn(opcode, owner, name, desc));
	}
	@Override
	public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
		instructionsToGo.add(new visitFrame(type, nLocal, local, nStack, stack));
	}
	@Override
	public void visitIincInsn(int var, int increment) {

		instructionsToGo.add(new visitIincInsn(var, increment));
	}
	@Override
	public void visitInsn(int opcode) {

		instructionsToGo.add(new visitInsn(opcode));
	}
	@Override
	public void visitIntInsn(int opcode, int operand) {

		instructionsToGo.add(new visitIntInsn(opcode, operand));
	}
	@Override
	public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs) {

		instructionsToGo.add(new visitInvokeDynamicInsn(name, desc, bsm, bsmArgs));
	}
	@Override
	public void visitJumpInsn(int opcode, Label label) {

		instructionsToGo.add(new visitJumpInsn(opcode, label));
	}
	@Override
	public void visitLabel(Label label) {

		instructionsToGo.add(new visitLabel(label));
	}
	public void visitLdcInsn(Object cst) {
		instructionsToGo.add(new visitLdcInsn(cst));
	};
	@Override
	public void visitLineNumber(int line, Label start) {

		instructionsToGo.add(new visitLineNumber(line, start));
	}
	@Override
	public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {

		instructionsToGo.add(new visitLocalVariable(name, desc, signature, start, end, index));
	}
	@Override
	public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {

		instructionsToGo.add(new visitLookupSwitchInsn(dflt, keys, labels));
	}
	@Override
	public void visitMaxs(int maxStack, int maxLocals) {

		instructionsToGo.add(new visitMaxs(maxStack, maxLocals));
	}
	@Override
	public void visitMethodInsn(int opcode, String owner, String name, String desc) {

		instructionsToGo.add(new visitMethodInsn(opcode, owner, name, desc));
	}
	@Override
	public void visitMultiANewArrayInsn(String desc, int dims) {

		instructionsToGo.add(new visitMultiANewArrayInsn(desc, dims));
	}
	@Override
	public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {

		return null;
		
//		return instructionsToGo.add(new visitParameterAnnotation(parameter, desc, visible));
	}
	@Override
	public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {

		instructionsToGo.add(new visitTableSwitchInsn(min, max, dflt, labels));
	}
	public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
		instructionsToGo.add(new visitTryCatchBlock(start, end, handler, type));
	}
	@Override
	public void visitTypeInsn(int opcode, String type) {

		instructionsToGo.add(new visitTypeInsn(opcode, type));
	}
	public void visitVarInsn(int opcode, int var) {
		instructionsToGo.add(new visitVarInsn(opcode, var));
	}
}
