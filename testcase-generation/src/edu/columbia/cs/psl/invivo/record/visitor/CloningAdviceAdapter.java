package edu.columbia.cs.psl.invivo.record.visitor;

import java.lang.reflect.Field;
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

public class CloningAdviceAdapter extends AdviceAdapter {

	private static final HashSet<String> immutableClasses = new HashSet<String>();
	static {
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

	protected CloningAdviceAdapter(int api, MethodVisitor mv, int access,
			String name, String desc, String classname) {
		super(api, mv, access, name, desc);
		this.className = className;
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
		mv.visitFrame(Opcodes.F_FULL, 4, new Object[] {className, className, Opcodes.TOP, "java/util/Iterator"}, 0, new Object[] {});
		mv.visitVarInsn(ALOAD, 3);
		mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "next", "()Ljava/lang/Object;");
		mv.visitTypeInsn(CHECKCAST, fieldDesc);
		mv.visitVarInsn(ASTORE, 2);
		
		mv.visitVarInsn(ALOAD, 1);
		mv.visitFieldInsn(GETFIELD, className, fieldName, "Ljava/util/ArrayList;");
		mv.visitVarInsn(ALOAD, 2);
		visitMethodInsn(INVOKEVIRTUAL, className, "_copy", "()" + fieldDesc);
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/ArrayList", "add", "(Ljava/lang/Object;)Z");
		mv.visitInsn(POP);
		mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
		mv.visitVarInsn(ALOAD, 3);
		mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "hasNext", "()Z");
		mv.visitJumpInsn(IFNE, l7);
		mv.visitLabel(ifNull);
		mv.visitFrame(Opcodes.F_FULL, 2, new Object[] {className, className}, 0, new Object[] {});
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
		mv.visitFrame(Opcodes.F_FULL, 4, new Object[] {className, className, Opcodes.TOP, "java/util/Iterator"}, 0, new Object[] {});
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
		mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
		mv.visitVarInsn(ALOAD, 3);
		mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "hasNext", "()Z");
		mv.visitJumpInsn(IFNE, l6);
		mv.visitLabel(l2);
		mv.visitFrame(Opcodes.F_FULL, 2, new Object[] {className, className}, 0, new Object[] {});
	}
	
	public static void fastCloneSet() {
		
	}
	
	public static void fastCloneQueue() {
		
	}
	
	protected void generateCopyMethod() {
		Class<?> thisClass = null;
		try {
			thisClass = Instrumenter.loader.loadClass(className.replace("/",
					"."));
		} catch (ClassNotFoundException e) {
			// We have not instrumented this class!
			e.printStackTrace();
		}

		/* TODO: Get the caching to work later */

		/* 1) Call the clone constructor */
		visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "clone",
				"()Ljava/lang/Object;");
		visitTypeInsn(CHECKCAST, className);
		visitVarInsn(ASTORE, 1);

		/*
		 * 2) For each field do the following 
		 * a) If its a primitive simply copy it 
		 * b) If its an object, call the respective ._copy method 
		 * c) If its an array, create a loop and do steps a) and b) 
		 * d) If its a collection, take care of that e) If nothing works, call the
		 * reflection cloning util
		 */

		for (Field f : thisClass.getFields()) {
			Type fieldType = Type.getType(f.getName());

			if (immutableClasses.contains(f.getType().getName())) {
				visitVarInsn(ALOAD, 1);
				loadThis();
				visitFieldInsn(GETFIELD, className, f.getName(), f.getType()
						.getName()); // Put L and ; in front and back of getname
				visitFieldInsn(PUTFIELD, className, f.getName(), f.getType()
						.getName());
			} else if (fieldType.getSort() == Type.OBJECT) {
				visitVarInsn(ALOAD, 0);
				visitFieldInsn(GETFIELD, className, f.getName(), f.getType()
						.getName());
				Label nullContinue = new Label();
				visitJumpInsn(IFNULL, nullContinue);
				Label nonNull = new Label();
				visitLabel(nonNull);
				
				visitVarInsn(ALOAD, 1);
				loadThis();
				visitFieldInsn(GETFIELD, className, f.getName(), f.getType()
						.getName());
				visitMethodInsn(INVOKEVIRTUAL, className, "_copy", "()"
						+ f.getType().getName());
				visitFieldInsn(PUTFIELD, className, f.getName(), f.getType()
						.getName());
				visitLabel(nullContinue);
				visitFrame(Opcodes.F_SAME, 0, null, 0, null);
			} else if (fieldType.getSort() == Type.ARRAY) {
				/* Check if non null */
				loadThis();
				visitFieldInsn(GETFIELD, className, f.getName(), f.getType().getName());
				Label nullContinue = new Label();
				visitJumpInsn(IFNULL, nullContinue);
				Label nonNull = new Label();
				visitLabel(nonNull);

				/* Instantiate new array */
				visitVarInsn(ALOAD, 1);
				loadThis();
				visitFieldInsn(GETFIELD, className, f.getName(), f.getType().getName());
				visitInsn(ARRAYLENGTH);
				visitTypeInsn(ANEWARRAY, f.getType().getName().substring(2).replace(";", ""));
				visitFieldInsn(PUTFIELD, className, f.getName(), f.getType().getName());
				
				/* Start copying */
				mv.visitInsn(ICONST_0);
				mv.visitVarInsn(ISTORE, 2);
				
				Label l7 = new Label();
				mv.visitJumpInsn(GOTO, l7);
				Label l8 = new Label();
				mv.visitLabel(l8);
				mv.visitFrame(Opcodes.F_APPEND,2, new Object[] {"edu/columbia/cs/psl/invivo/bench/ComplexObject", Opcodes.INTEGER}, 0, null);
				mv.visitVarInsn(ALOAD, 1);
				mv.visitFieldInsn(GETFIELD, className, f.getName(), f.getType().getName());
				mv.visitVarInsn(ILOAD, 2);
				loadThis();
				mv.visitFieldInsn(GETFIELD, className, f.getName(), f.getType().getName());
				mv.visitVarInsn(ILOAD, 2);
				mv.visitInsn(AALOAD);
				visitMethodInsn(INVOKEVIRTUAL, className, "_copy", "()"
						+ f.getType().getName());
				mv.visitInsn(AASTORE);
				mv.visitIincInsn(2, 1);
				mv.visitLabel(l7);
				mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
				mv.visitVarInsn(ILOAD, 2);
				mv.visitVarInsn(ALOAD, 1);
				mv.visitFieldInsn(GETFIELD, className, f.getName(), f.getType().getName());
				mv.visitInsn(ARRAYLENGTH);
				mv.visitJumpInsn(IF_ICMPLT, l8);
				
				Label doneCopying = new Label();
				mv.visitLabel(doneCopying);
				mv.visitFrame(Opcodes.F_CHOP,1, null, 0, null);
			}
		}

		loadThis();
		visitFieldInsn(GETFIELD, className, "the field you want to visit",
				"type of that field");
	}

	/**
	 * Precondition: Current element at the top of the stack is the element we
	 * need cloned Post condition: Current element at the top of the stack is
	 * the cloned element (and non-cloned is removed)
	 */

	protected void generateCloneOf(String typeOfField) {
		Type fieldType = Type.getType(typeOfField);
		/* If we are simply working with primitives then move on */
		if (fieldType.getSort() != Type.OBJECT
				|| immutableClasses.contains(typeOfField)) {
			return;
		}

		// super.visitFieldInsn(GETSTATIC,
		// "edu/columbia/cs/psl/invivo/record/CloningUtils", "cloner",
		// "Lcom/rits/cloning/Cloner;");
		// swap();
		// invokeVirtual(Type.getType(Cloner.class),
		// Method.getMethod("Object deepClone(Object)"));
		// checkCast(Type.getType(typeOfField));
		// visitInsn(Opcodes.POP);

	}

	protected void logValueAtTopOfStackToArray(String logFieldOwner,
			String logFieldName, String logFieldTypeDesc, Type elementType,
			boolean isStaticLoggingField) {
		int getOpcode = (isStaticLoggingField ? Opcodes.GETSTATIC
				: Opcodes.GETFIELD);
		int putOpcode = (isStaticLoggingField ? Opcodes.PUTSTATIC
				: Opcodes.PUTFIELD);

		// Grow the array if necessary

		if (!isStaticLoggingField)
			loadThis();
		super.visitFieldInsn(getOpcode, logFieldOwner, logFieldName + "_fill",
				Type.INT_TYPE.getDescriptor());
		if (!isStaticLoggingField)
			loadThis();
		super.visitFieldInsn(getOpcode, logFieldOwner, logFieldName,
				logFieldTypeDesc);
		super.arrayLength();
		Label labelForNoNeedToGrow = new Label();
		super.ifCmp(Type.INT_TYPE, Opcodes.IFNE, labelForNoNeedToGrow);
		// In this case, it's necessary to grow it

		// Create the new array and initialize its size
		int newArray = newLocal(Type.getType(logFieldTypeDesc));
		if (!isStaticLoggingField)
			loadThis();
		super.visitFieldInsn(getOpcode, logFieldOwner, logFieldName,
				logFieldTypeDesc);
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
		super.visitFieldInsn(getOpcode, logFieldOwner, logFieldName,
				logFieldTypeDesc);
		super.visitInsn(Opcodes.ICONST_0);
		super.loadLocal(newArray);
		super.visitInsn(Opcodes.ICONST_0);
		if (!isStaticLoggingField)
			super.loadThis();
		super.visitFieldInsn(getOpcode, logFieldOwner, logFieldName,
				logFieldTypeDesc);
		super.arrayLength();
		super.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System",
				"arraycopy", "(Ljava/lang/Object;ILjava/lang/Object;II)V");

		// array = newarray
		if (!isStaticLoggingField)
			super.loadThis();
		super.loadLocal(newArray);
		super.visitFieldInsn(putOpcode, logFieldOwner, logFieldName,
				logFieldTypeDesc);

		visitLabel(labelForNoNeedToGrow);

		// Load this into the end piece of the array
		if (elementType.getSize() == 1) {
			dup();
			if (!isStaticLoggingField)
				super.loadThis();
			super.visitFieldInsn(getOpcode, logFieldOwner, logFieldName,
					logFieldTypeDesc);
			swap();
			if (!isStaticLoggingField)
				super.loadThis();
			super.visitFieldInsn(getOpcode, logFieldOwner, logFieldName
					+ "_fill", Type.INT_TYPE.getDescriptor());
			swap();
		} else if (elementType.getSize() == 2) {
			dup2();
			if (!isStaticLoggingField)
				super.loadThis();
			super.visitFieldInsn(getOpcode, logFieldOwner, logFieldName,
					logFieldTypeDesc);
			dupX2();
			pop();
			if (!isStaticLoggingField)
				super.loadThis();
			super.visitFieldInsn(getOpcode, logFieldOwner, logFieldName
					+ "_fill", Type.INT_TYPE.getDescriptor());
			dupX2();
			pop();
		}
		if (elementType.getSort() == Type.OBJECT
				&& !elementType.getInternalName().equals("java/lang/String")) {
			super.visitFieldInsn(GETSTATIC,
					"edu/columbia/cs/psl/invivo/record/CloningUtils", "cloner",
					"Lcom/rits/cloning/Cloner;");
			swap();
			invokeVirtual(Type.getType(Cloner.class),
					Method.getMethod("Object deepClone(Object)"));
			checkCast(elementType);
		}

		super.arrayStore(elementType);

		if (!isStaticLoggingField)
			super.loadThis();
		if (!isStaticLoggingField)
			super.dup();
		super.visitFieldInsn(getOpcode, logFieldOwner, logFieldName + "_fill",
				Type.INT_TYPE.getDescriptor());
		super.visitInsn(Opcodes.ICONST_1);
		super.visitInsn(Opcodes.IADD);
		super.visitFieldInsn(putOpcode, logFieldOwner, logFieldName + "_fill",
				Type.INT_TYPE.getDescriptor());
	}
}
