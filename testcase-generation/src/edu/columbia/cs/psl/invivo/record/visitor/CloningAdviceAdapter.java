package edu.columbia.cs.psl.invivo.record.visitor;

import java.util.HashSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;
import org.objectweb.asm.tree.FieldNode;

import edu.columbia.cs.psl.invivo.record.CloningUtils;
import edu.columbia.cs.psl.invivo.record.Constants;
import edu.columbia.cs.psl.invivo.record.Instrumenter;
import edu.columbia.cs.psl.invivo.record.Log;
import edu.columbia.cs.psl.invivo.record.WallaceExportRunner;

public class CloningAdviceAdapter extends GeneratorAdapter implements Opcodes {

	private static final HashSet<String>	ignoredClasses		= new HashSet<String>();
	private static boolean					flexibleLog			= false;

	private static final HashSet<String>	immutableClasses	= new HashSet<String>();
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
		immutableClasses.add("Ljava/sql/ResultSet;");
		immutableClasses.add("Ljava/lang/Class;");
		immutableClasses.add("Z");
		immutableClasses.add("B");
		immutableClasses.add("C");
		immutableClasses.add("S");
		immutableClasses.add("I");
		immutableClasses.add("J");
		immutableClasses.add("F");
		immutableClasses.add("L");

	}
	private String							className;

	public CloningAdviceAdapter(int api, MethodVisitor mv, int access, String name, String desc, String classname) {
		super(api, mv, access, name, desc);
		this.className = classname;
	}

	public void fastCloneList(String fieldName, String fieldDesc) {
		/* Null check */
		super.visitVarInsn(Opcodes.ALOAD, 0);
		super.visitFieldInsn(Opcodes.GETFIELD, className, fieldName, "Ljava/util/ArrayList;");
		Label ifNull = new Label();
		super.visitJumpInsn(Opcodes.IFNULL, ifNull);
		Label notNull = new Label();
		super.visitLabel(notNull);

		/* Instantiation */
		super.visitVarInsn(Opcodes.ALOAD, 1);
		super.visitTypeInsn(Opcodes.NEW, "java/util/ArrayList");
		super.visitInsn(Opcodes.DUP);
		super.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V");
		super.visitFieldInsn(PUTFIELD, className, fieldName, "Ljava/util/ArrayList;");

		loadThis();
		super.visitFieldInsn(GETFIELD, className, fieldName, "Ljava/util/ArrayList;");
		super.visitMethodInsn(INVOKEVIRTUAL, "java/util/ArrayList", "iterator", "()Ljava/util/Iterator;");
		super.visitVarInsn(ASTORE, 3);
		Label l6 = new Label();
		super.visitJumpInsn(GOTO, l6);
		Label l7 = new Label();
		super.visitLabel(l7);
		// super.visitFrame(Opcodes.F_FULL, 4, new Object[] { className, className,
		// Opcodes.TOP, "java/util/Iterator" }, 0, new Object[] {});
		super.visitVarInsn(ALOAD, 3);
		super.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "next", "()Ljava/lang/Object;");
		super.visitTypeInsn(CHECKCAST, fieldDesc);
		super.visitVarInsn(ASTORE, 2);

		super.visitVarInsn(ALOAD, 1);
		super.visitFieldInsn(GETFIELD, className, fieldName, "Ljava/util/ArrayList;");
		super.visitVarInsn(ALOAD, 2);
		visitMethodInsn(INVOKEVIRTUAL, className, Constants.INNER_COPY_METHOD_NAME, "()" + fieldDesc);
		super.visitMethodInsn(INVOKEVIRTUAL, "java/util/ArrayList", "add", "(Ljava/lang/Object;)Z");
		super.visitInsn(POP);
		// super.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
		super.visitVarInsn(ALOAD, 3);
		super.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "hasNext", "()Z");
		super.visitJumpInsn(IFNE, l7);
		super.visitLabel(ifNull);
		// super.visitFrame(Opcodes.F_FULL, 2, new Object[] { className, className
		// }, 0, new Object[] {});
	}

	public void fastCloneMap(String fieldName, String keyDesc, String valueDesc) {
		/* Null check */
		loadThis();
		super.visitFieldInsn(GETFIELD, className, fieldName, "Ljava/util/HashMap;");
		Label l2 = new Label();
		super.visitJumpInsn(IFNULL, l2);
		Label l3 = new Label();
		super.visitLabel(l3);

		/* Instantiate the hashmap */
		super.visitVarInsn(ALOAD, 1);
		super.visitTypeInsn(NEW, "java/util/HashMap");
		super.visitInsn(DUP);
		super.visitMethodInsn(INVOKESPECIAL, "java/util/HashMap", "<init>", "()V");
		super.visitFieldInsn(PUTFIELD, className, fieldName, "Ljava/util/HashMap;");

		/* Copy the entries */
		loadThis();
		super.visitFieldInsn(GETFIELD, className, fieldName, "Ljava/util/HashMap;");
		super.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap", "entrySet", "()Ljava/util/Set;");
		super.visitMethodInsn(INVOKEINTERFACE, "java/util/Set", "iterator", "()Ljava/util/Iterator;");
		super.visitVarInsn(ASTORE, 3);
		Label l5 = new Label();
		super.visitJumpInsn(GOTO, l5);
		Label l6 = new Label();
		super.visitLabel(l6);
		// super.visitFrame(Opcodes.F_FULL, 4, new Object[] { className, className,
		// Opcodes.TOP, "java/util/Iterator" }, 0, new Object[] {});
		super.visitVarInsn(ALOAD, 3);
		super.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "next", "()Ljava/lang/Object;");
		super.visitTypeInsn(CHECKCAST, "java/util/Map$Entry");
		super.visitVarInsn(ASTORE, 2);

		super.visitVarInsn(ALOAD, 1);
		super.visitFieldInsn(GETFIELD, className, fieldName, "Ljava/util/HashMap;");
		super.visitVarInsn(ALOAD, 2);
		super.visitMethodInsn(INVOKEINTERFACE, "java/util/Map$Entry", "getKey", "()Ljava/lang/Object;");
		super.visitTypeInsn(CHECKCAST, keyDesc);
		/* Put in the checks here or the call to copy */
		super.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "toString", "()Ljava/lang/String;");
		super.visitVarInsn(ALOAD, 2);
		super.visitMethodInsn(INVOKEINTERFACE, "java/util/Map$Entry", "getValue", "()Ljava/lang/Object;");
		super.visitTypeInsn(CHECKCAST, valueDesc);
		/* Put in the checks here or the call to copy */
		super.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
		super.visitInsn(POP);
		super.visitLabel(l5);
		// super.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
		super.visitVarInsn(ALOAD, 3);
		super.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "hasNext", "()Z");
		super.visitJumpInsn(IFNE, l6);
		super.visitLabel(l2);
		// super.visitFrame(Opcodes.F_FULL, 2, new Object[] { className, className
		// }, 0, new Object[] {});
	}

	public static void fastCloneSet() {

	}

	public static void fastCloneQueue() {

	}

	protected void generateOuterCopyMethod() {
		super.visitTypeInsn(NEW, "java/util/IdentityHashMap");
		super.visitInsn(DUP);
		super.visitMethodInsn(INVOKESPECIAL, "java/util/IdentityHashMap", "<init>", "()V");
		super.visitFieldInsn(PUTSTATIC, "edu/columbia/cs/psl/invivo/record/CloningUtils", "cloneCache", "Ljava/util/IdentityHashMap;");
		loadThis();
		super.visitMethodInsn(INVOKEVIRTUAL, className, Constants.INNER_COPY_METHOD_NAME, "()L" + className + ";");
	}

	protected void generateCopyMethod() {
		if (Instrumenter.instrumentedClasses.containsKey(className)) {

			/* If what we are looking for is cached, just return that */

			super.visitFieldInsn(GETSTATIC, "edu/columbia/cs/psl/invivo/record/CloningUtils", "cloneCache", "Ljava/util/IdentityHashMap;");
			loadThis();
			super.visitMethodInsn(INVOKEVIRTUAL, "java/util/IdentityHashMap", "containsKey", "(Ljava/lang/Object;)Z");
			Label notCached = new Label();
			super.visitJumpInsn(IFEQ, notCached);
			super.visitFieldInsn(GETSTATIC, "edu/columbia/cs/psl/invivo/record/CloningUtils", "cloneCache", "Ljava/util/IdentityHashMap;");
			loadThis();
			super.visitMethodInsn(INVOKEVIRTUAL, "java/util/IdentityHashMap", "get", "(Ljava/lang/Object;)Ljava/lang/Object;");
			super.visitTypeInsn(CHECKCAST, className);
			super.visitInsn(ARETURN);
			super.visitLabel(notCached);

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
		} else {
			loadThis();
			cloneValAtTopOfStack("L" + className + ";");
		}
	}

	private boolean isCollection(String desc) {
		if (desc.contains("ArrayList") || desc.contains("HashMap"))
			return true;
		return false;
	}

	public void generateSetFieldsMethod() {

		/*
		 * 2) For each field do the following a) If its a primitive simply copy it b) If its an object, call the respective ._copy method iff its a
		 * class we have instrumented c) If its an array, create a loop and do steps a) and b) d) If its a collection, take care of that e) If nothing
		 * works, call the reflection cloning util
		 */
		int cloneVar = 1;
		int iteratorVar = this.newLocal(Type.getType(Integer.class));
		for (Object o : Instrumenter.instrumentedClasses.get(className).fields) {
			FieldNode f = (FieldNode) o;
			Type fieldType = Type.getType(f.desc);
			if (immutableClasses.contains(fieldType.getDescriptor())) {
				visitVarInsn(ALOAD, cloneVar);
				loadThis();
				visitFieldInsn(GETFIELD, className, f.name, fieldType.getDescriptor()); // Put
																						// L
																						// and
																						// ;
																						// in
																						// front
																						// and
																						// back
																						// of
																						// getname
				visitFieldInsn(PUTFIELD, className, f.name, fieldType.getDescriptor());
			} else if (fieldType.getSort() == Type.OBJECT && (Instrumenter.instrumentedClasses.containsKey(fieldType.getClassName()))) {
				loadThis();
				visitFieldInsn(GETFIELD, className, f.name, fieldType.getDescriptor());
				Label nullContinue = new Label();
				visitJumpInsn(IFNULL, nullContinue);
				Label nonNull = new Label();
				visitLabel(nonNull);

				visitVarInsn(ALOAD, cloneVar);
				loadThis();
				visitFieldInsn(GETFIELD, className, f.name, fieldType.getDescriptor());
				visitMethodInsn(INVOKEVIRTUAL, fieldType.getInternalName(), Constants.INNER_COPY_METHOD_NAME, "()" + fieldType.getDescriptor());
				visitFieldInsn(PUTFIELD, className, f.name, fieldType.getDescriptor());
				visitLabel(nullContinue);
			} else if (fieldType.getSort() == Type.ARRAY) {
				/* Check if non null */
				loadThis();
				visitFieldInsn(GETFIELD, className, f.name, fieldType.getDescriptor());
				Label nullContinue = new Label();
				visitJumpInsn(IFNULL, nullContinue);
				Label nonNull = new Label();
				visitLabel(nonNull);

				/* Instantiate new array */
				String arrayTypeDescriptor = fieldType.getDescriptor().replace("[L", "L");

				visitVarInsn(ALOAD, cloneVar);
				loadThis();
				visitFieldInsn(GETFIELD, className, f.name, fieldType.getDescriptor());
				visitInsn(ARRAYLENGTH);
				visitTypeInsn(ANEWARRAY, arrayTypeDescriptor);
				visitFieldInsn(PUTFIELD, className, f.name, fieldType.getDescriptor());

				/* Start copying */
				// TODO: Do a system.arraycopy if its an array of immutables
				if (immutableClasses.contains(fieldType.getElementType().getDescriptor())) {
					loadThis();
					super.visitFieldInsn(GETFIELD, className, f.name, fieldType.getDescriptor());
					super.visitInsn(ICONST_0);
					super.visitVarInsn(ALOAD, cloneVar);
					super.visitFieldInsn(GETFIELD, className, f.name, fieldType.getDescriptor());
					super.visitInsn(ICONST_0);
					loadThis();
					super.visitFieldInsn(GETFIELD, className, f.name, fieldType.getDescriptor());
					super.visitInsn(ARRAYLENGTH);
					super.visitMethodInsn(INVOKESTATIC, "java/lang/System", "arraycopy", "(Ljava/lang/Object;ILjava/lang/Object;II)V");
				} else {

					super.visitInsn(ICONST_0);
					super.visitVarInsn(ISTORE, iteratorVar);

					Label l7 = new Label();
					super.visitJumpInsn(GOTO, l7);
					Label l8 = new Label();
					super.visitLabel(l8);
					// super.visitFrame(Opcodes.F_APPEND, 2, new Object[] {
					// className, Opcodes.INTEGER }, 0, null);
					super.visitVarInsn(ALOAD, cloneVar);
					super.visitFieldInsn(GETFIELD, className, f.name, fieldType.getDescriptor());
					super.visitVarInsn(ILOAD, iteratorVar);
					loadThis();
					super.visitFieldInsn(GETFIELD, className, f.name, fieldType.getDescriptor());
					super.visitVarInsn(ILOAD, iteratorVar);
					super.visitInsn(AALOAD);

					generateCloneInner(arrayTypeDescriptor);

					super.visitInsn(AASTORE);
					super.visitIincInsn(2, 1);
					super.visitLabel(l7);
					// super.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
					super.visitVarInsn(ILOAD, iteratorVar);
					super.visitVarInsn(ALOAD, cloneVar);
					super.visitFieldInsn(GETFIELD, className, f.name, fieldType.getDescriptor());
					super.visitInsn(ARRAYLENGTH);
					super.visitJumpInsn(IF_ICMPLT, l8);

					Label doneCopying = new Label();
					super.visitLabel(doneCopying);
					// super.visitFrame(Opcodes.F_CHOP, 1, null, 0, null);
				}
				visitLabel(nullContinue);
			} else if (this.isCollection(className)) {
				if (className.contains("HashMap")) {
					String hashMapDesc = fieldType.getDescriptor();
					String entryDesc = hashMapDesc.substring(hashMapDesc.indexOf("<") + 1, hashMapDesc.lastIndexOf(">"));
					this.fastCloneMap(f.name, entryDesc.split(";")[0], entryDesc.split(";")[1]);
				} else if (className.contains("ArrayList")) {
					String hashMapDesc = fieldType.getDescriptor();
					String entryDesc = hashMapDesc.substring(hashMapDesc.indexOf("<") + 1, hashMapDesc.lastIndexOf(">"));
					this.fastCloneList(f.name, entryDesc);
				}
			} else {
				/* All else fails, just call the reflective cloning */
				super.visitVarInsn(ALOAD, cloneVar);
				super.visitFieldInsn(GETSTATIC, "edu/columbia/cs/psl/invivo/record/CloningUtils", "cloner", "Lcom/rits/cloning/Cloner;");
				loadThis();
				super.visitFieldInsn(GETFIELD, className, f.name, fieldType.getDescriptor());
				super.visitMethodInsn(INVOKEVIRTUAL, "com/rits/cloning/Cloner", "deepClone", "(Ljava/lang/Object;)Ljava/lang/Object;");
				super.visitTypeInsn(CHECKCAST, fieldType.getClassName().replace(".", "/"));
				super.visitFieldInsn(PUTFIELD, className, f.name, fieldType.getDescriptor());
			}
		}

		/*
		 * If the super class is instrumented, we should probably call that as well
		 */

		String parent = Instrumenter.instrumentedClasses.get(className).superName;
		if (Instrumenter.instrumentedClasses.containsKey(parent)) {
			loadThis();
			super.visitVarInsn(ALOAD, cloneVar);
			super.visitMethodInsn(INVOKESPECIAL, parent, Constants.SET_FIELDS_METHOD_NAME, "(L" + parent + ";)L" + parent + ";");
			super.visitInsn(POP);
		}

		/* We are done, put the result in the cache */
		super.visitFieldInsn(GETSTATIC, "edu/columbia/cs/psl/invivo/record/CloningUtils", "cloneCache", "Ljava/util/IdentityHashMap;");
		super.visitVarInsn(ALOAD, cloneVar);
		super.visitVarInsn(ALOAD, cloneVar);
		super.visitMethodInsn(INVOKEVIRTUAL, "java/util/IdentityHashMap", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
		super.visitInsn(POP);

		visitVarInsn(ALOAD, cloneVar);
	}

	/**
	 * Precondition: Current element at the top of the stack is the element we need cloned Post condition: Current element at the top of the stack is
	 * the cloned element (and non-cloned is removed)
	 */

	protected void cloneValAtTopOfStack(String typeOfField) {
		_generateClone(typeOfField, Constants.OUTER_COPY_METHOD_NAME, null);
	}

	protected void cloneValAtTopOfStack(String typeOfField, String debug) {
		_generateClone(typeOfField, Constants.OUTER_COPY_METHOD_NAME, debug);
	}

	protected void generateCloneInner(String typeOfField) {
		_generateClone(typeOfField, Constants.INNER_COPY_METHOD_NAME, null);
	}

	public void println(String toPrint) {
		visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
		visitLdcInsn(toPrint + " : ");
		super.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "print", "(Ljava/lang/String;)V");

		visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
		super.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "currentThread", "()Ljava/lang/Thread;");
		super.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Thread", "getName", "()Ljava/lang/String;");
		super.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V");
	}

	private void _generateClone(String typeOfField, String copyMethodToCall, String debug) {
		Type fieldType = Type.getType(typeOfField);

		if (
//				fieldType.getSort() == Type.ARRAY && fieldType.getElementType().getSort()
//				||
				(fieldType.getSort() != Type.ARRAY && (fieldType.getSort() != Type.OBJECT || immutableClasses.contains(typeOfField)))) {
//			println("reference> " + debug);
			//			println(debug);
			//			println("Doing nothing");
			return;
		}
		if (fieldType.getSort() == Type.ARRAY) {
			if (fieldType.getElementType().getSort() != Type.OBJECT || immutableClasses.contains(fieldType.getElementType().getDescriptor())) {
//				println("array> " + debug);

				// Just need to duplicate the array
				dup();
				Label nullContinue = new Label();
				ifNull(nullContinue);
				dup();
				visitInsn(ARRAYLENGTH);
				dup();
				newArray(Type.getType(fieldType.getDescriptor().substring(1)));
				dupX2();
				swap();
				push(0);
				dupX2();
				swap();
				super.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System", "arraycopy", "(Ljava/lang/Object;ILjava/lang/Object;II)V");
				visitLabel(nullContinue);

			} else {
//				println("heavy> " + debug);
				// Just use the reflective cloner
				visitLdcInsn(debug);
				invokeStatic(Type.getType(CloningUtils.class), Method.getMethod("Object clone(Object, String)"));
				checkCast(fieldType);
			}
		} else if (fieldType.getClassName().contains("InputStream") || fieldType.getClassName().contains("OutputStream")
				|| fieldType.getClassName().contains("Socket")) {
			//Do nothing
		} else {
//			println("heavy> " + debug);
			visitLdcInsn(debug);
			invokeStatic(Type.getType(CloningUtils.class), Method.getMethod("Object clone(Object, String)"));
			checkCast(fieldType);

		}
	}
	
	//	private static Object[] ar;
//		private void magic()
//		{
//			new WallaceExportRunner().
//		}
	protected void logValueAtTopOfStackToArray(String logFieldOwner, String logFieldName, String logFieldTypeDesc, Type elementType,
			boolean isStaticLoggingField, String debug) {
		int getOpcode = (isStaticLoggingField ? Opcodes.GETSTATIC : Opcodes.GETFIELD);
		int putOpcode = (isStaticLoggingField ? Opcodes.PUTSTATIC : Opcodes.PUTFIELD);
		Label monitorStart = new Label();
		Label monitorEndLabel = new Label();
		int monitorIndx = 0;

//		if (threadSafe) {
			newLocal(Type.getType(logFieldTypeDesc)); //Needed for some reason, unkown? Don't remove though, otherwise ASM messes stuff up
			newLocal(Type.getType(logFieldTypeDesc)); //Needed for some reason, unkown? Don't remove though, otherwise ASM messes stuff up
			newLocal(Type.getType(logFieldTypeDesc)); //Needed for some reason, unkown? Don't remove though, otherwise ASM messes stuff up
			newLocal(Type.getType(logFieldTypeDesc)); //Needed for some reason, unkown? Don't remove though, otherwise ASM messes stuff up
			monitorIndx = newLocal(Type.getType("Ljava/lang/Object;"));
			visitLabel(monitorStart);

			//Lock
			super.visitFieldInsn(Opcodes.GETSTATIC, logFieldOwner, "lock", "Ljava/lang/Object;");
			dup();
			super.visitVarInsn(ASTORE, monitorIndx);
			super.monitorEnter();
//		}
		//Also acquire a read lock for the export lock
		//		super.visitFieldInsn(GETSTATIC, Type.getInternalName(CloningUtils.class), "exportLock", Type.getDescriptor(ReadWriteLock.class));
		//		super.visitMethodInsn(INVOKEINTERFACE, Type.getInternalName(ReadWriteLock.class), "readLock", "()Ljava/util/concurrent/locks/Lock;");
		//		super.visitMethodInsn(INVOKEINTERFACE, Type.getInternalName(Lock.class), "lock", "()V");

		
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
		super.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(Log.class), "grow"+logFieldName, "()V");
		
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
		cloneValAtTopOfStack(elementType.getDescriptor(), debug);

		super.arrayStore(elementType);

		if (!isStaticLoggingField)
			super.loadThis();
		if (!isStaticLoggingField)
			super.dup();
		super.visitFieldInsn(getOpcode, logFieldOwner, logFieldName + "_fill", Type.INT_TYPE.getDescriptor());

		super.visitInsn(Opcodes.ICONST_1);
		super.visitInsn(Opcodes.IADD);
		super.visitFieldInsn(putOpcode, logFieldOwner, logFieldName + "_fill", Type.INT_TYPE.getDescriptor());
		//		println("Incremented fill for " + logFieldOwner+"."+logFieldName);
		//Release the export lock
		//		super.visitFieldInsn(GETSTATIC, Type.getInternalName(CloningUtils.class), "exportLock", Type.getDescriptor(ReadWriteLock.class));
		//		super.visitMethodInsn(INVOKEINTERFACE, Type.getInternalName(ReadWriteLock.class), "readLock", "()Ljava/util/concurrent/locks/Lock;");
		//		super.visitMethodInsn(INVOKEINTERFACE, Type.getInternalName(Lock.class), "unlock", "()V");

//		if (threadSafe) {
			//Unlock
			super.visitVarInsn(ALOAD, monitorIndx);
			super.monitorExit();
			visitLabel(monitorEndLabel);
			Label endLbl = new Label();
			super.visitFieldInsn(getOpcode, logFieldOwner, logFieldName + "_fill", Type.INT_TYPE.getDescriptor());
			super.visitLdcInsn(Constants.MAX_LOG_SIZE);
			super.ifCmp(Type.INT_TYPE, Opcodes.IFNE, endLbl);
			super.visitMethodInsn(INVOKESTATIC, Type.getInternalName(WallaceExportRunner.class), "_export", "()V");

			visitLabel(endLbl);
			super.visitLocalVariable(logFieldName + "_monitor", "Ljava/lang/Object;", null, monitorStart, monitorEndLabel, monitorIndx);
//		}

	}

	protected void onMethodEnter() {
		// TODO Auto-generated method stub
		
	}

}
