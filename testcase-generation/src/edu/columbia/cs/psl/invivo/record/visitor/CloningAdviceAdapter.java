package edu.columbia.cs.psl.invivo.record.visitor;

import java.util.HashSet;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.commons.Method;

import com.rits.cloning.Cloner;

import edu.columbia.cs.psl.invivo.record.Constants;
import edu.columbia.cs.psl.invivo.record.Instrumenter;

public class CloningAdviceAdapter extends AdviceAdapter{

	private static final HashSet<String> immutableClasses = new HashSet<String>();
	static{
		immutableClasses.add("java/lang/Integer");
		immutableClasses.add("java/lang/Long");
		immutableClasses.add("java/lang/Short");
		immutableClasses.add("java/lang/Float");
		immutableClasses.add("java/lang/String");
		immutableClasses.add("java/lang/Char");
		immutableClasses.add("java/lang/Byte");
		immutableClasses.add("I");
		immutableClasses.add("S");
		immutableClasses.add("F");
		immutableClasses.add("L");
		immutableClasses.add("C");
		immutableClasses.add("B");
	}
	private String className;
	protected CloningAdviceAdapter(int api, MethodVisitor mv, int access, String name, String desc, String classname) {
		super(api, mv, access, name, desc);
		this.className = className;
	}
	
	protected void generateCopyMethod()
	{
		try {
			Class<?> thisClass = Instrumenter.loader.loadClass(className.replace("/", "."));
		} catch (ClassNotFoundException e) {
			// We have not instrumented this class!
			e.printStackTrace();
		}
		loadThis();
		visitFieldInsn(GETFIELD, className, "the field you want to visit", "type of that field");
	}
	/**
	 * Precondition: Current element at the top of the stack is the element we need cloned
	 * Post condition: Current element at the top of the stack is the cloned element (and non-cloned is removed)
	 */
	protected void generateCloneOf(String typeOfField)
	{
		Type fieldType = Type.getType(typeOfField);
		/* If we are simply working with primitives, simply make a copy and move on */
		if(fieldType.getSort() != Type.OBJECT || immutableClasses.contains(typeOfField))
		{
			return;
		}
//		Instrumenter.loader
		//http://code.google.com/p/cloning/
		
//		super.visitFieldInsn(GETSTATIC, "edu/columbia/cs/psl/invivo/record/CloningUtils", "cloner", "Lcom/rits/cloning/Cloner;");
//		swap();
//		invokeVirtual(Type.getType(Cloner.class), Method.getMethod("Object deepClone(Object)"));
//		checkCast(Type.getType(typeOfField));
//		visitInsn(Opcodes.POP);

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
