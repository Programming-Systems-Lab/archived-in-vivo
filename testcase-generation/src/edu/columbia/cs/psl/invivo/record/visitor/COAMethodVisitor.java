package edu.columbia.cs.psl.invivo.record.visitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.commons.Method;

import com.rits.cloning.Cloner;

import edu.columbia.cs.psl.invivo.record.Constants;


public class COAMethodVisitor extends AdviceAdapter implements Constants{
	private static Logger logger = Logger.getLogger(COAMethodVisitor.class);
	private String name;
	private String desc;
	private String classDesc;
	private int pc;
	protected COAMethodVisitor(int api, MethodVisitor mv, int access, String name, String desc, String classDesc) {
		super(api, mv, access, name, desc);
		this.name=name;
		this.desc = desc;
		this.classDesc = classDesc;
	}
	private COAClassVisitor parent;
	public void setClassVisitor(COAClassVisitor coaClassVisitor) {
		this.parent = coaClassVisitor;
	}

//	private boolean isCloned;
//	private String value;
//	private String _orig_value;
//
//	private void magic()
//	{
//		String ret;
//		if(isCloned)
//		{
//			_orig_value = CloningUtils.cloner.deepClone(value);
//			isCloned = true;
//		}
//		ret = value;
//	}

	@Override
	protected void onMethodEnter() {
		super.onMethodEnter();
		super.visitFieldInsn(GETSTATIC, "edu/columbia/cs/psl/invivo/record/CloningUtils", "cloner", "Lcom/rits/cloning/Cloner;");
		loadThis();
		invokeVirtual(Type.getType(Cloner.class), Method.getMethod("Object deepClone(Object)"));
		
	}
	
	@Override
	protected void onMethodExit(int opcode) {
		super.onMethodExit(opcode);

	}
	@Override
	public void visitLineNumber(int line, Label start) {
		super.visitLineNumber(line, start);
	}
	@Override
	public void visitMethodInsn(int opcode, String owner, String name, String desc) {
		super.visitMethodInsn(opcode, owner, name, desc);
	}
//	private HashMap<String, String> variablesToClear = new HashMap<String, String>();
	@Override
	public void visitFieldInsn(int opcode, String owner, String name, String desc) {
		super.visitFieldInsn(opcode, owner, name, desc);
		pc++;
	}
	
	@Override
	public void visitIincInsn(int var, int increment) {
		super.visitIincInsn(var, increment);
		pc++;
	}
	@Override
	public void visitInsn(int opcode) {
		super.visitInsn(opcode);
		pc++;
	}
	@Override
	public void visitIntInsn(int opcode, int operand) {
		super.visitIntInsn(opcode, operand);
		pc++;
	}
	@Override
	public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs) {
		super.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs);
		pc++;
	}
	@Override
	public void visitJumpInsn(int opcode, Label label) {
		super.visitJumpInsn(opcode, label);
		pc++;
	}
	@Override
	public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
		super.visitLookupSwitchInsn(dflt, keys, labels);
		pc++;
	}
	@Override
	public void visitMultiANewArrayInsn(String desc, int dims) {
		super.visitMultiANewArrayInsn(desc, dims);
		pc++;
	}
	@Override
	public void visitTypeInsn(int opcode, String type) {
		super.visitTypeInsn(opcode, type);
		pc++;
	}
	@Override
	public void visitVarInsn(int opcode, int var) {
		super.visitVarInsn(opcode, var);
		pc++;
	}
	@Override
	public void visitLdcInsn(Object cst) {
		super.visitLdcInsn(cst);
		pc++;
	}
	@Override
	public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
		super.visitTableSwitchInsn(min, max, dflt, labels);
		pc++;
	}
//		if(opcode == GETFIELD && desc.length() > 1) //Do this in the case of objects only
//		{	
//			variablesToClear.put(name,desc);
//			Label lblbForReadThrough = new Label();
//			dup();
//			super.visitFieldInsn(GETFIELD, owner, BEEN_CLONED_PREFIX+name, Type.BOOLEAN_TYPE.getDescriptor());
//			visitJumpInsn(IFNE, lblbForReadThrough);
//
//			dup();
//			dup();
//			super.visitFieldInsn(GETSTATIC, "edu/columbia/cs/psl/invivo/record/CloningUtils", "cloner", "Lcom/rits/cloning/Cloner;");
//			swap();
//			super.visitFieldInsn(opcode, owner, name, desc);
//			invokeVirtual(Type.getType(Cloner.class), Method.getMethod("Object deepClone(Object)"));
//			checkCast(Type.getType(desc));
//			super.visitFieldInsn(Opcodes.PUTFIELD, owner, Constants.PREV_VALUE_PREFIX+name, desc);
//
//			dup();
//			visitLdcInsn(true);
//			super.visitFieldInsn(Opcodes.PUTFIELD, owner, Constants.BEEN_CLONED_PREFIX+name, Type.BOOLEAN_TYPE.getDescriptor());
//			
//			visitLabel(lblbForReadThrough);
//			
//			super.visitFieldInsn(opcode, owner, name, desc);			
//		}
//		else if(opcode == PUTFIELD && desc.length() == 1) //If we are going to do a putfield on a primitive do a simple copy
//		{
//			variablesToClear.put(name,desc);
//			super.visitFieldInsn(opcode, owner, name, desc);
//		}
//		else if(opcode == PUTFIELD && desc.length() >1) //Need a copy on a putfield for objects too
//		{
//			variablesToClear.put(name,desc);
//			Label lblbForReadThrough = new Label();
//			
//			swap();
//			dup();
//			super.visitFieldInsn(GETFIELD, owner, BEEN_CLONED_PREFIX+name, Type.BOOLEAN_TYPE.getDescriptor());
//			visitJumpInsn(IFNE, lblbForReadThrough);
//
//			dup();
//			dup();
//			super.visitFieldInsn(GETSTATIC, "edu/columbia/cs/psl/invivo/record/CloningUtils", "cloner", "Lcom/rits/cloning/Cloner;");
//			swap();
//			super.visitFieldInsn(GETFIELD, owner, name, desc);
//			invokeVirtual(Type.getType(Cloner.class), Method.getMethod("Object deepClone(Object)"));
//			checkCast(Type.getType(desc));
//			super.visitFieldInsn(Opcodes.PUTFIELD, owner, Constants.PREV_VALUE_PREFIX+name, desc);
//
//			dup();
//			visitLdcInsn(true);
//			super.visitFieldInsn(Opcodes.PUTFIELD, owner, Constants.BEEN_CLONED_PREFIX+name, Type.BOOLEAN_TYPE.getDescriptor());
//			
//			visitLabel(lblbForReadThrough);
//			swap();
//			super.visitFieldInsn(opcode, owner, name, desc);			
//
//		}
//		else
//			super.visitFieldInsn(opcode, owner, name, desc);
//	}
}
