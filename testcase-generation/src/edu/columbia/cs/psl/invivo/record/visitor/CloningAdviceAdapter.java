package edu.columbia.cs.psl.invivo.record.visitor;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.IdentityHashMap;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.commons.Method;

import com.rits.cloning.Cloner;

import edu.columbia.cs.psl.invivo.record.CloningUtils;
import edu.columbia.cs.psl.invivo.record.Constants;
import edu.columbia.cs.psl.invivo.record.Instrumenter;

public class CloningAdviceAdapter extends AdviceAdapter {

	private static final HashSet<String> immutableClasses = new HashSet<String>();
	static {
		immutableClasses.add("Ljava/lang/Integer;");
		immutableClasses.add("Ljava/lang/Long;");
		immutableClasses.add("Ljava/lang/Short;");
		immutableClasses.add("Ljava/lang/Float;");
		immutableClasses.add("Ljava/lang/String;");
		immutableClasses.add("Ljava/lang/Char;");
		immutableClasses.add("Ljava/lang/Byte;");
		immutableClasses.add("Ljava/lang/Integer;");
		immutableClasses.add("Ljava/lang/Long;");
		immutableClasses.add("Ljava/lang/Short;");
		immutableClasses.add("Ljava/lang/Float;");
		immutableClasses.add("Ljava/lang/String;");
		immutableClasses.add("Ljava/lang/Char;");
		immutableClasses.add("Ljava/lang/Byte;");
		immutableClasses.add("Z");
		immutableClasses.add("B");
		immutableClasses.add("C");
		immutableClasses.add("S");
		immutableClasses.add("I");
		immutableClasses.add("J");
		immutableClasses.add("F");
		immutableClasses.add("L");



	}
	private String className;

	protected CloningAdviceAdapter(int api, MethodVisitor mv, int access, String name, String desc, String classname) {
		super(api, mv, access, name, desc);
		this.className = classname;
	}

	public void fastCloneList(String fieldName, String fieldDesc) {
		/* Null check */
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitFieldInsn(Opcodes.GETFIELD, className, fieldName, "Ljava/util/ArrayList;");
		Label ifNull = new Label();
		mv.visitJumpInsn(Opcodes.IFNULL, ifNull);
		Label notNull = new Label();
		mv.visitLabel(notNull);

		/* Instantiation */
		mv.visitVarInsn(Opcodes.ALOAD, 1);
		mv.visitTypeInsn(Opcodes.NEW, "java/util/ArrayList");
		mv.visitInsn(Opcodes.DUP);
		mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V");
		mv.visitFieldInsn(PUTFIELD, className, fieldName, "Ljava/util/ArrayList;");

		loadThis();
		mv.visitFieldInsn(GETFIELD, className, fieldName, "Ljava/util/ArrayList;");
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/ArrayList", "iterator", "()Ljava/util/Iterator;");
		mv.visitVarInsn(ASTORE, 3);
		Label l6 = new Label();
		mv.visitJumpInsn(GOTO, l6);
		Label l7 = new Label();
		mv.visitLabel(l7);
//		mv.visitFrame(Opcodes.F_FULL, 4, new Object[] { className, className, Opcodes.TOP, "java/util/Iterator" }, 0, new Object[] {});
		mv.visitVarInsn(ALOAD, 3);
		mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "next", "()Ljava/lang/Object;");
		mv.visitTypeInsn(CHECKCAST, fieldDesc);
		mv.visitVarInsn(ASTORE, 2);

		mv.visitVarInsn(ALOAD, 1);
		mv.visitFieldInsn(GETFIELD, className, fieldName, "Ljava/util/ArrayList;");
		mv.visitVarInsn(ALOAD, 2);
		visitMethodInsn(INVOKEVIRTUAL, className, Constants.INNER_COPY_METHOD_NAME, "()" + fieldDesc);
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/ArrayList", "add", "(Ljava/lang/Object;)Z");
		mv.visitInsn(POP);
//		mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
		mv.visitVarInsn(ALOAD, 3);
		mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "hasNext", "()Z");
		mv.visitJumpInsn(IFNE, l7);
		mv.visitLabel(ifNull);
//		mv.visitFrame(Opcodes.F_FULL, 2, new Object[] { className, className }, 0, new Object[] {});
	}

	public void fastCloneMap(String fieldName, String keyDesc, String valueDesc) {
		/* Null check */
		loadThis();
		mv.visitFieldInsn(GETFIELD, className, fieldName, "Ljava/util/HashMap;");
		Label l2 = new Label();
		mv.visitJumpInsn(IFNULL, l2);
		Label l3 = new Label();
		mv.visitLabel(l3);

		/* Instantiate the hashmap */
		mv.visitVarInsn(ALOAD, 1);
		mv.visitTypeInsn(NEW, "java/util/HashMap");
		mv.visitInsn(DUP);
		mv.visitMethodInsn(INVOKESPECIAL, "java/util/HashMap", "<init>", "()V");
		mv.visitFieldInsn(PUTFIELD, className, fieldName, "Ljava/util/HashMap;");

		/* Copy the entries */
		loadThis();
		mv.visitFieldInsn(GETFIELD, className, fieldName, "Ljava/util/HashMap;");
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap", "entrySet", "()Ljava/util/Set;");
		mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Set", "iterator", "()Ljava/util/Iterator;");
		mv.visitVarInsn(ASTORE, 3);
		Label l5 = new Label();
		mv.visitJumpInsn(GOTO, l5);
		Label l6 = new Label();
		mv.visitLabel(l6);
//		mv.visitFrame(Opcodes.F_FULL, 4, new Object[] { className, className, Opcodes.TOP, "java/util/Iterator" }, 0, new Object[] {});
		mv.visitVarInsn(ALOAD, 3);
		mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "next", "()Ljava/lang/Object;");
		mv.visitTypeInsn(CHECKCAST, "java/util/Map$Entry");
		mv.visitVarInsn(ASTORE, 2);

		mv.visitVarInsn(ALOAD, 1);
		mv.visitFieldInsn(GETFIELD, className, fieldName, "Ljava/util/HashMap;");
		mv.visitVarInsn(ALOAD, 2);
		mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map$Entry", "getKey", "()Ljava/lang/Object;");
		mv.visitTypeInsn(CHECKCAST, keyDesc);
		/* Put in the checks here or the call to copy */
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "toString", "()Ljava/lang/String;");
		mv.visitVarInsn(ALOAD, 2);
		mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map$Entry", "getValue", "()Ljava/lang/Object;");
		mv.visitTypeInsn(CHECKCAST, valueDesc);
		/* Put in the checks here or the call to copy */
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
		mv.visitInsn(POP);
		mv.visitLabel(l5);
//		mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
		mv.visitVarInsn(ALOAD, 3);
		mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "hasNext", "()Z");
		mv.visitJumpInsn(IFNE, l6);
		mv.visitLabel(l2);
//		mv.visitFrame(Opcodes.F_FULL, 2, new Object[] { className, className }, 0, new Object[] {});
	}

	public static void fastCloneSet() {

	}

	public static void fastCloneQueue() {

	}

	protected void generateOuterCopyMethod() {
		mv.visitTypeInsn(NEW, "java/util/IdentityHashMap");
		mv.visitInsn(DUP);
		mv.visitMethodInsn(INVOKESPECIAL, "java/util/IdentityHashMap", "<init>", "()V");
		mv.visitFieldInsn(PUTSTATIC, "edu/columbia/cs/psl/invivo/record/CloningUtils", "cloneCache", "Ljava/util/IdentityHashMap;");
		loadThis();
		mv.visitMethodInsn(INVOKEVIRTUAL, className, Constants.INNER_COPY_METHOD_NAME, "()L" + className + ";");
	}

	protected void generateCopyMethod() {
		Class<?> thisClass = null;
		try {
			thisClass = Instrumenter.loader.loadClass(className.replace("/", "."));
		} catch (ClassNotFoundException e) {
			// We have not instrumented this class!
			e.printStackTrace();
		}

		/* If what we are looking for is cached, just return that */

		mv.visitFieldInsn(GETSTATIC, "edu/columbia/cs/psl/invivo/record/CloningUtils", "cloneCache", "Ljava/util/IdentityHashMap;");
		loadThis();
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/IdentityHashMap", "containsKey", "(Ljava/lang/Object;)Z");
		Label notCached = new Label();
		mv.visitJumpInsn(IFEQ, notCached);
		mv.visitFieldInsn(GETSTATIC, "edu/columbia/cs/psl/invivo/record/CloningUtils", "cloneCache", "Ljava/util/IdentityHashMap;");
		loadThis();
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/IdentityHashMap", "get", "(Ljava/lang/Object;)Ljava/lang/Object;");
		mv.visitTypeInsn(CHECKCAST, thisClass.getName().replace(".", "/"));
		mv.visitInsn(ARETURN);
		mv.visitLabel(notCached);

		Label varStart = new Label();
		visitLabel(varStart);
		int localVar = this.newLocal(Type.getType("L" + className + ";"));

		/* 1) Call the clone constructor */
		loadThis();
		visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "clone", "()Ljava/lang/Object;");
		visitTypeInsn(CHECKCAST, className);
		visitVarInsn(ASTORE, localVar);

		loadThis();
		visitVarInsn(ALOAD, localVar);
		visitMethodInsn(Opcodes.INVOKEVIRTUAL, className, Constants.SET_FIELDS_METHOD_NAME, "(L" + className + ";)L" + className + ";");

		Label varEnd = new Label();
		visitLabel(varEnd);
		visitLocalVariable("myClone", "L" + className + ";", null, varStart, varEnd, localVar);
	}

	private boolean isCollection(String desc) {
		if (desc.contains("ArrayList") || desc.contains("HashMap"))
			return true;
		return false;
	}

	public void generateSetFieldsMethod() {
		Class<?> thisClass = null;
		try {
			thisClass = Instrumenter.loader.loadClass(className.replace("/", "."));
		} catch (ClassNotFoundException e) {
			// We have not instrumented this class!
			e.printStackTrace();
		}

		/*
		 * 2) For each field do the following a) If its a primitive simply copy
		 * it b) If its an object, call the respective ._copy method iff its a
		 * class we have instrumented c) If its an array, create a loop and do
		 * steps a) and b) d) If its a collection, take care of that e) If
		 * nothing works, call the reflection cloning util
		 */
		int cloneVar = 1;
		int iteratorVar = this.newLocal(Type.getType(Integer.class));
		for (Field f : thisClass.getDeclaredFields()) {
			Type fieldType = Type.getType(f.getType());
			if (immutableClasses.contains(fieldType.getDescriptor())) {
				visitVarInsn(ALOAD, cloneVar);
				loadThis();
				visitFieldInsn(GETFIELD, className, f.getName(), fieldType.getDescriptor()); // Put
																								// L
																								// and
																								// ;
																								// in
																								// front
																								// and
																								// back
																								// of
																								// getname
				visitFieldInsn(PUTFIELD, className, f.getName(), fieldType.getDescriptor());
			} else if (fieldType.getSort() == Type.OBJECT && (Instrumenter.instrumentedClasses.containsKey(fieldType.getClassName()))) {
				loadThis();
				visitFieldInsn(GETFIELD, className, f.getName(), fieldType.getDescriptor());
				Label nullContinue = new Label();
				visitJumpInsn(IFNULL, nullContinue);
				Label nonNull = new Label();
				visitLabel(nonNull);

				visitVarInsn(ALOAD, cloneVar);
				loadThis();
				visitFieldInsn(GETFIELD, className, f.getName(), fieldType.getDescriptor());
				visitMethodInsn(INVOKEVIRTUAL, fieldType.getInternalName(), Constants.INNER_COPY_METHOD_NAME, "()" + fieldType.getDescriptor());
				visitFieldInsn(PUTFIELD, className, f.getName(), fieldType.getDescriptor());
				visitLabel(nullContinue);
			} else if (fieldType.getSort() == Type.ARRAY) {
				/* Check if non null */
				loadThis();
				visitFieldInsn(GETFIELD, className, f.getName(), fieldType.getDescriptor());
				Label nullContinue = new Label();
				visitJumpInsn(IFNULL, nullContinue);
				Label nonNull = new Label();
				visitLabel(nonNull);

				/* Instantiate new array */
				String arrayTypeDescriptor = fieldType.getDescriptor().replace("[L", "L");
				
				visitVarInsn(ALOAD, cloneVar);
				loadThis();
				visitFieldInsn(GETFIELD, className, f.getName(), fieldType.getDescriptor());
				visitInsn(ARRAYLENGTH);
				visitTypeInsn(ANEWARRAY, arrayTypeDescriptor);
				visitFieldInsn(PUTFIELD, className, f.getName(), fieldType.getDescriptor());

				/* Start copying */
				// TODO: Do a system.arraycopy if its an array of immutables
				if (immutableClasses.contains(fieldType.getElementType().getDescriptor())) {
					loadThis();
					mv.visitFieldInsn(GETFIELD, className, f.getName(), fieldType.getDescriptor());
					mv.visitInsn(ICONST_0);
					mv.visitVarInsn(ALOAD, cloneVar);
					mv.visitFieldInsn(GETFIELD, className, f.getName(), fieldType.getDescriptor());
					mv.visitInsn(ICONST_0);
					loadThis();
					mv.visitFieldInsn(GETFIELD, className, f.getName(), fieldType.getDescriptor());
					mv.visitInsn(ARRAYLENGTH);
					mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "arraycopy", "(Ljava/lang/Object;ILjava/lang/Object;II)V");
				} else {

					mv.visitInsn(ICONST_0);
					mv.visitVarInsn(ISTORE, iteratorVar);

					Label l7 = new Label();
					mv.visitJumpInsn(GOTO, l7);
					Label l8 = new Label();
					mv.visitLabel(l8);
//					mv.visitFrame(Opcodes.F_APPEND, 2, new Object[] { className, Opcodes.INTEGER }, 0, null);
					mv.visitVarInsn(ALOAD, cloneVar);
					mv.visitFieldInsn(GETFIELD, className, f.getName(), fieldType.getDescriptor());
					mv.visitVarInsn(ILOAD, iteratorVar);
					loadThis();
					mv.visitFieldInsn(GETFIELD, className, f.getName(), fieldType.getDescriptor());
					mv.visitVarInsn(ILOAD, iteratorVar);
					mv.visitInsn(AALOAD);
					
					generateCloneInner(arrayTypeDescriptor);

					mv.visitInsn(AASTORE);
					mv.visitIincInsn(2, 1);
					mv.visitLabel(l7);
//					mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
					mv.visitVarInsn(ILOAD, iteratorVar);
					mv.visitVarInsn(ALOAD, cloneVar);
					mv.visitFieldInsn(GETFIELD, className, f.getName(), fieldType.getDescriptor());
					mv.visitInsn(ARRAYLENGTH);
					mv.visitJumpInsn(IF_ICMPLT, l8);

					Label doneCopying = new Label();
					mv.visitLabel(doneCopying);
//					mv.visitFrame(Opcodes.F_CHOP, 1, null, 0, null);
				}
				visitLabel(nullContinue);
			} else {
				/* All else fails, just call the reflective cloning */
				mv.visitVarInsn(ALOAD, cloneVar);
				mv.visitFieldInsn(GETSTATIC, "edu/columbia/cs/psl/invivo/record/CloningUtils", "cloner", "Lcom/rits/cloning/Cloner;");
				loadThis();
				mv.visitFieldInsn(GETFIELD, className, f.getName(), fieldType.getDescriptor());
				mv.visitMethodInsn(INVOKEVIRTUAL, "com/rits/cloning/Cloner", "deepClone", "(Ljava/lang/Object;)Ljava/lang/Object;");
				mv.visitTypeInsn(CHECKCAST, fieldType.getClassName().replace(".", "/"));
				mv.visitFieldInsn(PUTFIELD, className, f.getName(), fieldType.getDescriptor());
			}
		}

		/*
		 * If the super class is instrumented, we should probably call that as
		 * well
		 */

		String parent = Instrumenter.instrumentedClasses.get(className);
		if (Instrumenter.instrumentedClasses.containsKey(parent)) {
			loadThis();
			mv.visitVarInsn(ALOAD, cloneVar);
			mv.visitMethodInsn(INVOKESPECIAL, parent, Constants.SET_FIELDS_METHOD_NAME, "(L" + parent + ";)L" + parent + ";");
			mv.visitInsn(POP);
		}

		/* We are done, put the result in the cache */
		mv.visitFieldInsn(GETSTATIC, "edu/columbia/cs/psl/invivo/record/CloningUtils", "cloneCache", "Ljava/util/IdentityHashMap;");
		mv.visitVarInsn(ALOAD, cloneVar);
		mv.visitVarInsn(ALOAD, cloneVar);
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/IdentityHashMap", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
		mv.visitInsn(POP);

		visitVarInsn(ALOAD, cloneVar);
	}

	/**
	 * Precondition: Current element at the top of the stack is the element we
	 * need cloned Post condition: Current element at the top of the stack is
	 * the cloned element (and non-cloned is removed)
	 */

	protected void generateCloneOf(String typeOfField) {
		_generateClone(typeOfField, Constants.OUTER_COPY_METHOD_NAME);
	}

	protected void generateCloneInner(String typeOfField) {
		_generateClone(typeOfField, Constants.INNER_COPY_METHOD_NAME);
	}
	private void _generateClone(String typeOfField, String copyMethodToCall)
	{
		Type fieldType = Type.getType(typeOfField);
		//Also need to special case here for the fast cloners
		if (fieldType.getSort() != Type.ARRAY && (fieldType.getSort() != Type.OBJECT || immutableClasses.contains(typeOfField))) {
			return;
		}
		if(fieldType.getSort() == Type.ARRAY)
		{
			if(fieldType.getElementType().getSort() != Type.OBJECT|| immutableClasses.contains(fieldType.getElementType().getDescriptor()))
			{
				//Just need to duplicate the array
				dup();
				Label nullContinue = new Label();
				ifNull(nullContinue);
				dup();
				visitInsn(ARRAYLENGTH);
				visitTypeInsn(ANEWARRAY, fieldType.getDescriptor().replace("[L", "["));
				dupX2();
				swap();
				push(0);
				dupX2();
				swap();
				super.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System", "arraycopy", "(Ljava/lang/Object;ILjava/lang/Object;II)V");
				dup();
				visitLabel(nullContinue);
				pop();
			}
			else if(Instrumenter.instrumentedClasses.containsKey(fieldType.getElementType().getDescriptor()))
			{
				//TODO Need to iterate, clone fast?
				super.visitFieldInsn(GETSTATIC, "edu/columbia/cs/psl/invivo/record/CloningUtils", "cloner", "Lcom/rits/cloning/Cloner;");
				swap();
				invokeVirtual(Type.getType(Cloner.class), Method.getMethod("Object deepClone(Object)"));
				checkCast(fieldType);
			}
			else
			{
				//Just use the reflective cloner
				super.visitFieldInsn(GETSTATIC, "edu/columbia/cs/psl/invivo/record/CloningUtils", "cloner", "Lcom/rits/cloning/Cloner;");
				swap();
				invokeVirtual(Type.getType(Cloner.class), Method.getMethod("Object deepClone(Object)"));
				checkCast(fieldType);
			}
		}
		else if (Instrumenter.instrumentedClasses.containsKey(fieldType.getDescriptor()))
			mv.visitMethodInsn(INVOKEVIRTUAL, fieldType.getClassName(), copyMethodToCall, "()" + fieldType.getDescriptor());
		else {
			super.visitFieldInsn(GETSTATIC, "edu/columbia/cs/psl/invivo/record/CloningUtils", "cloner", "Lcom/rits/cloning/Cloner;");
			swap();
			invokeVirtual(Type.getType(Cloner.class), Method.getMethod("Object deepClone(Object)"));
			checkCast(fieldType);
		}
	}
	protected void logValueAtTopOfStackToArray(String logFieldOwner, String logFieldName, String logFieldTypeDesc, Type elementType, boolean isStaticLoggingField) {
		int getOpcode = (isStaticLoggingField ? Opcodes.GETSTATIC : Opcodes.GETFIELD);
		int putOpcode = (isStaticLoggingField ? Opcodes.PUTSTATIC : Opcodes.PUTFIELD);

		// Grow the array if necessary

		if (!isStaticLoggingField)
			loadThis();
		super.visitFieldInsn(getOpcode, logFieldOwner, logFieldName + "_fill", Type.INT_TYPE.getDescriptor());
		if (!isStaticLoggingField)
			loadThis();
		super.visitFieldInsn(getOpcode, logFieldOwner, logFieldName, logFieldTypeDesc);
		super.arrayLength();
		Label labelForNoNeedToGrow = new Label();
		super.ifCmp(Type.INT_TYPE, Opcodes.IFNE, labelForNoNeedToGrow);
		// In this case, it's necessary to grow it

		// Create the new array and initialize its size
		int newArray = newLocal(Type.getType(logFieldTypeDesc));
		if (!isStaticLoggingField)
			loadThis();
		super.visitFieldInsn(getOpcode, logFieldOwner, logFieldName, logFieldTypeDesc);
		super.arrayLength();
		super.visitInsn(Opcodes.I2D);
		super.visitLdcInsn(Constants.LOG_GROWTH_RATE);
		super.visitInsn(Opcodes.DMUL);
		super.visitInsn(Opcodes.D2I);
		super.newArray(Type.getType(logFieldTypeDesc.substring(1))); // Bug in
																		// ASM
																		// prevents
																		// us
																		// from
																		// doing
																		// type.getElementType
		super.storeLocal(newArray, Type.getType(logFieldTypeDesc));
		// Do the copy
		if (!isStaticLoggingField)
			loadThis();
		super.visitFieldInsn(getOpcode, logFieldOwner, logFieldName, logFieldTypeDesc);
		super.visitInsn(Opcodes.ICONST_0);
		super.loadLocal(newArray);
		super.visitInsn(Opcodes.ICONST_0);
		if (!isStaticLoggingField)
			super.loadThis();
		super.visitFieldInsn(getOpcode, logFieldOwner, logFieldName, logFieldTypeDesc);
		super.arrayLength();
		super.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System", "arraycopy", "(Ljava/lang/Object;ILjava/lang/Object;II)V");

		// array = newarray
		if (!isStaticLoggingField)
			super.loadThis();
		super.loadLocal(newArray);
		super.visitFieldInsn(putOpcode, logFieldOwner, logFieldName, logFieldTypeDesc);

		visitLabel(labelForNoNeedToGrow);

		// Load this into the end piece of the array
		if (elementType.getSize() == 1) {
			dup();
			if (!isStaticLoggingField)
				super.loadThis();
			super.visitFieldInsn(getOpcode, logFieldOwner, logFieldName, logFieldTypeDesc);
			swap();
			if (!isStaticLoggingField)
				super.loadThis();
			super.visitFieldInsn(getOpcode, logFieldOwner, logFieldName + "_fill", Type.INT_TYPE.getDescriptor());
			swap();
		} else if (elementType.getSize() == 2) {
			dup2();
			if (!isStaticLoggingField)
				super.loadThis();
			super.visitFieldInsn(getOpcode, logFieldOwner, logFieldName, logFieldTypeDesc);
			dupX2();
			pop();
			if (!isStaticLoggingField)
				super.loadThis();
			super.visitFieldInsn(getOpcode, logFieldOwner, logFieldName + "_fill", Type.INT_TYPE.getDescriptor());
			dupX2();
			pop();
		}
		generateCloneOf(elementType.getDescriptor());

		super.arrayStore(elementType);

		if (!isStaticLoggingField)
			super.loadThis();
		if (!isStaticLoggingField)
			super.dup();
		super.visitFieldInsn(getOpcode, logFieldOwner, logFieldName + "_fill", Type.INT_TYPE.getDescriptor());
		super.visitInsn(Opcodes.ICONST_1);
		super.visitInsn(Opcodes.IADD);
		super.visitFieldInsn(putOpcode, logFieldOwner, logFieldName + "_fill", Type.INT_TYPE.getDescriptor());
	}

}
