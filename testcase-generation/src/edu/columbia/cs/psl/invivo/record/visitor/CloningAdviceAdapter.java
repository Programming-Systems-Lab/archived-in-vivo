package edu.columbia.cs.psl.invivo.record.visitor;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.commons.Method;

import com.rits.cloning.Cloner;

import edu.columbia.cs.psl.invivo.record.Constants;

public class CloningAdviceAdapter extends AdviceAdapter{

	protected CloningAdviceAdapter(int api, MethodVisitor mv, int access, String name, String desc) {
		super(api, mv, access, name, desc);
	}
	protected void logValueAtTopOfStackToArray(String logFieldOwner, String logFieldName, String logFieldTypeDesc, Type elementType, boolean isStaticLoggingField)
	{
		int getOpcode = (isStaticLoggingField ? Opcodes.GETSTATIC : Opcodes.GETFIELD);
		int putOpcode = (isStaticLoggingField ? Opcodes.PUTSTATIC : Opcodes.PUTFIELD);
		
		//Grow the array if necessary
		
		if(!isStaticLoggingField) loadThis();
		super.visitFieldInsn(getOpcode, logFieldOwner,logFieldName+"_fill", Type.INT_TYPE.getDescriptor());
		if(!isStaticLoggingField) loadThis();
		super.visitFieldInsn(getOpcode, logFieldOwner,logFieldName,logFieldTypeDesc);
		super.arrayLength();
		Label labelForNoNeedToGrow = new Label();
		super.ifCmp(Type.INT_TYPE, Opcodes.IFNE, labelForNoNeedToGrow);
		//In this case, it's necessary to grow it

		//Create the new array and initialize its size
		int newArray = newLocal(Type.getType(logFieldTypeDesc));
		if(!isStaticLoggingField) loadThis();
		super.visitFieldInsn(getOpcode, logFieldOwner,logFieldName, logFieldTypeDesc);
		super.arrayLength();
		super.visitInsn(Opcodes.I2D);
		super.visitLdcInsn(Constants.LOG_GROWTH_RATE);
		super.visitInsn(Opcodes.DMUL);
		super.visitInsn(Opcodes.D2I);
		super.newArray(Type.getType(logFieldTypeDesc.substring(1))); //Bug in ASM prevents us from doing type.getElementType
		super.storeLocal(newArray,Type.getType(logFieldTypeDesc));
		
		//Do the copy
		if(!isStaticLoggingField) loadThis();
		super.visitFieldInsn(getOpcode, logFieldOwner,logFieldName, logFieldTypeDesc);
		super.visitInsn(Opcodes.ICONST_0);
		super.loadLocal(newArray);
		super.visitInsn(Opcodes.ICONST_0);
		if(!isStaticLoggingField) super.loadThis();
		super.visitFieldInsn(getOpcode, logFieldOwner,logFieldName, logFieldTypeDesc);
		super.arrayLength();
		super.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System", "arraycopy", "(Ljava/lang/Object;ILjava/lang/Object;II)V");
		
		//array = newarray
		if(!isStaticLoggingField)
		super.loadThis();
		super.loadLocal(newArray);
		super.visitFieldInsn(putOpcode, logFieldOwner,logFieldName, logFieldTypeDesc);
		
		
		visitLabel(labelForNoNeedToGrow);
		
		//Load this into the end piece of the array			
		if(elementType.getSize() == 1)
		{
			dup();
			if(!isStaticLoggingField) super.loadThis();
			super.visitFieldInsn(getOpcode, logFieldOwner,logFieldName, logFieldTypeDesc);
			swap();
			if(!isStaticLoggingField) super.loadThis();
			super.visitFieldInsn(getOpcode, logFieldOwner,logFieldName+"_fill",Type.INT_TYPE.getDescriptor());
			swap();
		}
		else if(elementType.getSize() == 2)
		{
			dup2();
			if(!isStaticLoggingField) super.loadThis();
			super.visitFieldInsn(getOpcode, logFieldOwner,logFieldName, logFieldTypeDesc);
			dupX2();
			pop();
			if(!isStaticLoggingField) super.loadThis();
			super.visitFieldInsn(getOpcode, logFieldOwner,logFieldName+"_fill",Type.INT_TYPE.getDescriptor());
			dupX2();
			pop();
		}
		if(elementType.getSort() == Type.OBJECT && ! elementType.getInternalName().equals("java/lang/String"))
		{
			super.visitFieldInsn(GETSTATIC, "edu/columbia/cs/psl/invivo/record/CloningUtils", "cloner", "Lcom/rits/cloning/Cloner;");
			swap();
			invokeVirtual(Type.getType(Cloner.class), Method.getMethod("Object deepClone(Object)"));
			checkCast(elementType);
		}

		
		super.arrayStore(elementType);
		
		if(!isStaticLoggingField) super.loadThis();
		if(!isStaticLoggingField) super.dup();
		super.visitFieldInsn(getOpcode, logFieldOwner,logFieldName+"_fill",Type.INT_TYPE.getDescriptor());
		super.visitInsn(Opcodes.ICONST_1);
		super.visitInsn(Opcodes.IADD);
		super.visitFieldInsn(putOpcode, logFieldOwner,logFieldName+"_fill",Type.INT_TYPE.getDescriptor());
	}
}
