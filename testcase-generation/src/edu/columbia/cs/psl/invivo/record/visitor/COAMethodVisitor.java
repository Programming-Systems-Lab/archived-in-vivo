package edu.columbia.cs.psl.invivo.record.visitor;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

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
import edu.columbia.cs.psl.invivo.record.MethodCall;


public class COAMethodVisitor extends AdviceAdapter implements Constants{
	private static Logger logger = Logger.getLogger(COAMethodVisitor.class);
	private String name;
	private String desc;
	private String classDesc;
	private int pc;
	private static HashSet<String> nonDeterministicMethods = new HashSet<String>();
	private boolean isStatic;
	private boolean constructor;
	private boolean superInitialized;
	
	static{
		File f = new File("nondeterministic-methods.txt");
		Scanner s;
		try {
			s = new Scanner(f);
			while(s.hasNextLine())
				nonDeterministicMethods.add(s.nextLine());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@Override
		public void visitCode() {
			super.visitCode();
			if(!constructor) superInitialized = true;
		}
	private boolean isFirstConstructor;
	protected COAMethodVisitor(int api, MethodVisitor mv, int access, String name, String desc, String classDesc, boolean isFirstConstructor) {
		super(api, mv, access, name, desc);
		this.name=name;
		this.desc = desc;
		this.classDesc = classDesc;
		this.isStatic = (access & Opcodes.ACC_STATIC) != 0;
		this.constructor = "<init>".equals(name);
		this.isFirstConstructor = isFirstConstructor;
	}
	private COAClassVisitor parent;
	public void setClassVisitor(COAClassVisitor coaClassVisitor) {
		this.parent = coaClassVisitor;
	}
	
	@Override
	protected void onMethodEnter() {
		super.onMethodEnter();
	}
	@Override
	public void visitEnd() {
		super.visitEnd();
		parent.addFieldMarkup(methodCallsToClear);
	}
	private int lineNumber = 0;
	@Override
	public void visitLineNumber(int line, Label start) {
		super.visitLineNumber(line, start);
		lineNumber = line;
	}
	@Override
	public void visitMethodInsn(int opcode, String owner, String name, String desc) {
		try
		{
		MethodCall m = new MethodCall(this.name, this.desc, this.classDesc, pc, lineNumber, owner, name, desc, isStatic);
		Type returnType = Type.getMethodType(desc).getReturnType();
		if(
				(!constructor || isFirstConstructor || superInitialized)
				&& !returnType.equals(Type.VOID_TYPE) && nonDeterministicMethods.contains(owner+"."+name+":"+desc))
		{
			logger.debug("Adding field in MV to list " + m.getLogFieldName());
			methodCallsToClear.add(m);
			super.visitMethodInsn(opcode, owner, name, desc);
			isStatic = true;
			int getOpcode = Opcodes.GETSTATIC;
			int putOpcode = Opcodes.PUTSTATIC;
			
			//Grow the array if necessary
			
			if(!isStatic) loadThis();
			super.visitFieldInsn(getOpcode, Constants.LOG_DUMP_CLASS, m.getLogFieldName()+"_fill", Type.INT_TYPE.getDescriptor());
			if(!isStatic) loadThis();
			super.visitFieldInsn(getOpcode, Constants.LOG_DUMP_CLASS, m.getLogFieldName(), m.getLogFieldType().getDescriptor());
			super.arrayLength();
			Label labelForNoNeedToGrow = new Label();
			super.ifCmp(Type.INT_TYPE, Opcodes.IFNE, labelForNoNeedToGrow);
			//In this case, it's necessary to grow it

			//Create the new array and initialize its size
			int newArray = newLocal(m.getLogFieldType());
			if(!isStatic) loadThis();
			super.visitFieldInsn(getOpcode, Constants.LOG_DUMP_CLASS, m.getLogFieldName(), m.getLogFieldType().getDescriptor());
			super.arrayLength();
			super.visitInsn(Opcodes.I2D);
			super.visitLdcInsn(Constants.LOG_GROWTH_RATE);
			super.visitInsn(Opcodes.DMUL);
			super.visitInsn(Opcodes.D2I);
			super.newArray(Type.getType(m.getLogFieldType().getDescriptor().substring(1))); //Bug in ASM prevents us from doing type.getElementType
			super.storeLocal(newArray, m.getLogFieldType());
			
			//Do the copy
			if(!isStatic) loadThis();
			super.visitFieldInsn(getOpcode, Constants.LOG_DUMP_CLASS, m.getLogFieldName(), m.getLogFieldType().getDescriptor());
			super.visitInsn(Opcodes.ICONST_0);
			super.loadLocal(newArray);
			super.visitInsn(Opcodes.ICONST_0);
			if(!isStatic) super.loadThis();
			super.visitFieldInsn(getOpcode, Constants.LOG_DUMP_CLASS, m.getLogFieldName(), m.getLogFieldType().getDescriptor());
			super.arrayLength();
			super.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System", "arraycopy", "(Ljava/lang/Object;ILjava/lang/Object;II)V");
			
			//array = newarray
			if(!isStatic)
			super.loadThis();
			super.loadLocal(newArray);
			super.visitFieldInsn(putOpcode, Constants.LOG_DUMP_CLASS, m.getLogFieldName(), m.getLogFieldType().getDescriptor());
			
			
			visitLabel(labelForNoNeedToGrow);
			
			//Load this into the end piece of the array			
			if(returnType.getSize() == 1)
			{
				dup();
				if(!isStatic) super.loadThis();
				super.visitFieldInsn(getOpcode, Constants.LOG_DUMP_CLASS, m.getLogFieldName(), m.getLogFieldType().getDescriptor());
				swap();
				if(!isStatic) super.loadThis();
				super.visitFieldInsn(getOpcode, Constants.LOG_DUMP_CLASS, m.getLogFieldName()+"_fill",Type.INT_TYPE.getDescriptor());
				swap();
			}
			else if(returnType.getSize() == 2)
			{
				dup2();
				if(!isStatic) super.loadThis();
				super.visitFieldInsn(getOpcode, Constants.LOG_DUMP_CLASS, m.getLogFieldName(), m.getLogFieldType().getDescriptor());
				dupX2();
				pop();
				if(!isStatic) super.loadThis();
				super.visitFieldInsn(getOpcode, Constants.LOG_DUMP_CLASS, m.getLogFieldName()+"_fill",Type.INT_TYPE.getDescriptor());
				dupX2();
				pop();
			}
			if(returnType.getSort() == Type.OBJECT && ! returnType.getInternalName().equals("java/lang/String"))
			{
				super.visitFieldInsn(GETSTATIC, "edu/columbia/cs/psl/invivo/record/CloningUtils", "cloner", "Lcom/rits/cloning/Cloner;");
				swap();
				invokeVirtual(Type.getType(Cloner.class), Method.getMethod("Object deepClone(Object)"));
				checkCast(returnType);
			}

			
			super.arrayStore(Type.getMethodType(desc).getReturnType());
			
			if(!isStatic) super.loadThis();
			if(!isStatic) super.dup();
			super.visitFieldInsn(getOpcode, Constants.LOG_DUMP_CLASS, m.getLogFieldName()+"_fill",Type.INT_TYPE.getDescriptor());
			super.visitInsn(Opcodes.ICONST_1);
			super.visitInsn(Opcodes.IADD);
			super.visitFieldInsn(putOpcode, Constants.LOG_DUMP_CLASS, m.getLogFieldName()+"_fill",Type.INT_TYPE.getDescriptor());
		}
		else
			super.visitMethodInsn(opcode, owner, name, desc);
		pc++;
		}
		catch(Exception ex)
		{
			logger.error("Unable to instrument method call",ex);
		}
	}

	private ArrayList<MethodCall> methodCallsToClear = new ArrayList<MethodCall>();

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
