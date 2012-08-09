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
import org.objectweb.asm.commons.LocalVariablesSorter;
import org.objectweb.asm.commons.Method;
import org.objectweb.asm.tree.FieldNode;

import edu.columbia.cs.psl.invivo.record.CloningUtils;
import edu.columbia.cs.psl.invivo.record.Constants;
import edu.columbia.cs.psl.invivo.record.Instrumenter;
import edu.columbia.cs.psl.invivo.record.Log;
import edu.columbia.cs.psl.invivo.record.SerializableLog;
import edu.columbia.cs.psl.invivo.record.WallaceExportRunner;

public class CloningAdviceAdapter extends GeneratorAdapter implements Opcodes {

	private static final HashSet<String> ignoredClasses = new HashSet<String>();
	private static boolean flexibleLog = false;

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
	private String className;

	private LocalVariablesSorter lvsorter;

	public CloningAdviceAdapter(int api, MethodVisitor mv, int access, String name, String desc, String classname, LocalVariablesSorter lvsorter) {
		super(api, mv, access, name, desc);
		this.className = classname;
		this.lvsorter = lvsorter;
	}

	/**
	 * Precondition: Current element at the top of the stack is the element we
	 * need cloned Post condition: Current element at the top of the stack is
	 * the cloned element (and non-cloned is removed)
	 */

	protected void cloneValAtTopOfStack(String typeOfField) {
		_generateClone(typeOfField, Constants.OUTER_COPY_METHOD_NAME, null, false);
	}

	protected void cloneValAtTopOfStack(String typeOfField, String debug, boolean secondElHasArrayLen) {
		_generateClone(typeOfField, Constants.OUTER_COPY_METHOD_NAME, debug, secondElHasArrayLen);
	}

	protected void generateCloneInner(String typeOfField) {
		_generateClone(typeOfField, Constants.INNER_COPY_METHOD_NAME, null, false);
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

	private void _generateClone(String typeOfField, String copyMethodToCall, String debug, boolean secondElHasArrayLen) {
		Type fieldType = Type.getType(typeOfField);

		if (
		// fieldType.getSort() == Type.ARRAY &&
		// fieldType.getElementType().getSort()
		// ||
		fieldType.getSort() == Type.VOID || (fieldType.getSort() != Type.ARRAY && (fieldType.getSort() != Type.OBJECT || immutableClasses.contains(typeOfField)))) {
			// println("reference> " + debug);
			// println(debug);
			// println("Doing nothing");
			return;
		}
		if (fieldType.getSort() == Type.ARRAY) {
			if (fieldType.getElementType().getSort() != Type.OBJECT || immutableClasses.contains(fieldType.getElementType().getDescriptor())) {
				// println("array> " + debug);

				// Just need to duplicate the array
				dup();
				Label nullContinue = new Label();
				ifNull(nullContinue);
				if (secondElHasArrayLen) {
					swap();
					// pop();
					// swap();
					// dup();
					// visitFieldInsn(GETSTATIC, "java/lang/System", "out",
					// "Ljava/io/PrintStream;");
					// swap();
					// super.visitMethodInsn(INVOKEVIRTUAL,
					// "java/io/PrintStream", "println", "(I)V");
				} else {
					dup();
					visitInsn(ARRAYLENGTH);
				}
				dup();
				newArray(Type.getType(fieldType.getDescriptor().substring(1)));
				dupX2();
				swap();
				push(0);
				dupX2();
				swap();
				super.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System", "arraycopy", "(Ljava/lang/Object;ILjava/lang/Object;II)V");
				Label noNeedToPop = new Label();
				if (secondElHasArrayLen) {
					visitJumpInsn(GOTO, noNeedToPop);
					visitLabel(nullContinue);
					swap();
					pop();
				} else {
					visitLabel(nullContinue);
				}

				visitLabel(noNeedToPop);

			} else {
				// println("heavy> " + debug);
				// Just use the reflective cloner
				visitLdcInsn(debug);
				invokeStatic(Type.getType(CloningUtils.class), Method.getMethod("Object clone(Object, String)"));
				checkCast(fieldType);
			}
		} else if (fieldType.getClassName().contains("InputStream") || fieldType.getClassName().contains("OutputStream") || fieldType.getClassName().contains("Socket")) {
			// Do nothing
		} else {
			// println("heavy> " + debug);
			visitLdcInsn(debug);
			invokeStatic(Type.getType(CloningUtils.class), Method.getMethod("Object clone(Object, String)"));
			checkCast(fieldType);

		}
	}

	// private static Object[] ar;
	// private void magic()
	// {
	// new WallaceExportRunner().
	// }
	protected void logValueAtTopOfStackToArray(String logFieldOwner, String logFieldName, String logFieldTypeDesc, Type elementType, boolean isStaticLoggingField, String debug,
			boolean secondElHasArrayLen) {
		int getOpcode = (isStaticLoggingField ? Opcodes.GETSTATIC : Opcodes.GETFIELD);
		int putOpcode = (isStaticLoggingField ? Opcodes.PUTSTATIC : Opcodes.PUTFIELD);
		Label monitorStart = new Label();
		Label monitorEndLabel = new Label();
		int monitorIndx = 0;

		// if (threadSafe) {
		// newLocal(Type.getType(logFieldTypeDesc)); // Needed for some reason,
		// // unkown? Don't remove
		// // though, otherwise ASM
		// // messes stuff up
		// newLocal(Type.getType(logFieldTypeDesc)); // Needed for some reason,
		// // unkown? Don't remove
		// // though, otherwise ASM
		// // messes stuff up
		// newLocal(Type.getType(logFieldTypeDesc)); // Needed for some reason,
		// // unkown? Don't remove
		// // though, otherwise ASM
		// // messes stuff up
		// newLocal(Type.getType(logFieldTypeDesc)); // Needed for some reason,
		// // unkown? Don't remove
		// // though, otherwise ASM
		// // messes stuff up
		// newLocal(Type.getType(logFieldTypeDesc)); // Needed for some reason,
		// // unkown? Don't remove
		// // though, otherwise ASM
		// // messes stuff up
		// newLocal(Type.getType(logFieldTypeDesc)); // Needed for some reason,
		// // unkown? Don't remove
		// // though, otherwise ASM
		// // messes stuff up
		// lvsorter.newLocal(Type.getType("Ljava/lang/Object;"));
		// lvsorter.newLocal(Type.getType("Ljava/lang/Object;"));
		// lvsorter.newLocal(Type.getType("Ljava/lang/Object;"));
		// lvsorter.newLocal(Type.getType("Ljava/lang/Object;"));
		// lvsorter.newLocal(Type.getType("Ljava/lang/Object;"));
		// lvsorter.newLocal(Type.getType("Ljava/lang/Object;"));
		// lvsorter.newLocal(Type.getType("Ljava/lang/Object;"));
		// lvsorter.newLocal(Type.getType("Ljava/lang/Object;"));
		// lvsorter.newLocal(Type.getType("Ljava/lang/Object;"));
		// monitorIndx = lvsorter.newLocal(Type.getType("Ljava/lang/Object;"));
		// visitLabel(monitorStart);

		// Lock
		// super.visitFieldInsn(Opcodes.GETSTATIC,
		// Type.getInternalName(Log.class), "lock", "Ljava/lang/Object;");
		// dup();
		// super.visitVarInsn(ASTORE, monitorIndx);
		// super.monitorEnter();
		// }
		// Also acquire a read lock for the export lock
		super.visitFieldInsn(GETSTATIC, Type.getInternalName(Log.class), "logLock", Type.getDescriptor(Lock.class));
		super.visitMethodInsn(INVOKEINTERFACE, Type.getInternalName(Lock.class), "lock", "()V");

		// Grow the array if necessary

		super.visitFieldInsn(getOpcode, logFieldOwner, logFieldName + "_fill", Type.INT_TYPE.getDescriptor());
		super.visitFieldInsn(getOpcode, logFieldOwner, logFieldName, logFieldTypeDesc);
		super.arrayLength();
		Label labelForNoNeedToGrow = new Label();
		super.ifCmp(Type.INT_TYPE, Opcodes.IFNE, labelForNoNeedToGrow);
		// In this case, it's necessary to grow it
		// Create the new array and initialize its size

		int newArray = lvsorter.newLocal(Type.getType(logFieldTypeDesc));
		visitFieldInsn(getOpcode, logFieldOwner, logFieldName, logFieldTypeDesc);
		arrayLength();
		visitInsn(Opcodes.I2D);
		visitLdcInsn(Constants.LOG_GROWTH_RATE);
		visitInsn(Opcodes.DMUL);
		visitInsn(Opcodes.D2I);

		newArray(Type.getType(logFieldTypeDesc.substring(1))); // Bug in
																// ASM
																// prevents
																// us
																// from
																// doing
																// type.getElementType
		storeLocal(newArray, Type.getType(logFieldTypeDesc));
		visitFieldInsn(getOpcode, logFieldOwner, logFieldName, logFieldTypeDesc);
		visitInsn(Opcodes.ICONST_0);
		loadLocal(newArray);
		visitInsn(Opcodes.ICONST_0);
		visitFieldInsn(getOpcode, logFieldOwner, logFieldName, logFieldTypeDesc);
		arrayLength();
		visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System", "arraycopy", "(Ljava/lang/Object;ILjava/lang/Object;II)V");

		// array = newarray

		loadLocal(newArray);
		visitFieldInsn(putOpcode, logFieldOwner, logFieldName, logFieldTypeDesc);
		
		int newArray2 = lvsorter.newLocal(Type.getType("[Ljava/lang/String;"));
		visitFieldInsn(getOpcode, logFieldOwner, logFieldName+"_owners", "[Ljava/lang/String;");
		arrayLength();
		visitInsn(Opcodes.I2D);
		visitLdcInsn(Constants.LOG_GROWTH_RATE);
		visitInsn(Opcodes.DMUL);
		visitInsn(Opcodes.D2I);

		newArray(Type.getType("Ljava/lang/String;"));
		
		storeLocal(newArray2, Type.getType("[Ljava/lang/String;"));
		visitFieldInsn(getOpcode, logFieldOwner, logFieldName+"_owners", "[Ljava/lang/String;");
		visitInsn(Opcodes.ICONST_0);
		loadLocal(newArray2);
		visitInsn(Opcodes.ICONST_0);
		visitFieldInsn(getOpcode, logFieldOwner, logFieldName+"_owners", "[Ljava/lang/String;");
		arrayLength();
		visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System", "arraycopy", "(Ljava/lang/Object;ILjava/lang/Object;II)V");

		// array = newarray

		loadLocal(newArray2);
		visitFieldInsn(putOpcode, logFieldOwner, logFieldName+"_owners", "[Ljava/lang/String;");

		visitLabel(labelForNoNeedToGrow);
		// Load this into the end piece of the array
		if (elementType.getSize() == 1) {
			if (secondElHasArrayLen) {
				/*
				 * size buf
				 */
				dupX1();
				/*
				 * buf size buf
				 */
				visitFieldInsn(getOpcode, logFieldOwner, logFieldName, logFieldTypeDesc);
				dupX2();
				pop();
				/*
				 * buf logfield size buf
				 */
				visitFieldInsn(getOpcode, logFieldOwner, logFieldName + "_fill", Type.INT_TYPE.getDescriptor());
				dupX2();
				pop();
				/*
				 * buf logfield logsize size buf
				 */
			} else {
				dup();
				visitFieldInsn(getOpcode, logFieldOwner, logFieldName, logFieldTypeDesc);
				swap();
				visitFieldInsn(getOpcode, logFieldOwner, logFieldName + "_fill", Type.INT_TYPE.getDescriptor());
				swap();
			}
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
		cloneValAtTopOfStack(elementType.getDescriptor(), debug, secondElHasArrayLen);

		arrayStore(elementType);
		
		visitFieldInsn(getOpcode, logFieldOwner, logFieldName+"_owners", "[Ljava/lang/String;");
		visitFieldInsn(getOpcode, logFieldOwner, logFieldName + "_fill", Type.INT_TYPE.getDescriptor());

		visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "currentThread", "()Ljava/lang/Thread;");
		visitMethodInsn(INVOKEVIRTUAL, "java/lang/Thread", "getName", "()Ljava/lang/String;");
		arrayStore(Type.getType(String.class));
		visitFieldInsn(getOpcode, logFieldOwner, logFieldName + "_fill", Type.INT_TYPE.getDescriptor());

		super.visitInsn(Opcodes.ICONST_1);
		super.visitInsn(Opcodes.IADD);
		super.visitFieldInsn(putOpcode, logFieldOwner, logFieldName + "_fill", Type.INT_TYPE.getDescriptor());
		// println("Incremented fill for " + logFieldOwner+"."+logFieldName);
		// Release the export lock
		// super.visitFieldInsn(GETSTATIC,
		// Type.getInternalName(CloningUtils.class), "exportLock",
		// Type.getDescriptor(ReadWriteLock.class));
		// super.visitMethodInsn(INVOKEINTERFACE,
		// Type.getInternalName(ReadWriteLock.class), "readLock",
		// "()Ljava/util/concurrent/locks/Lock;");
		// super.visitMethodInsn(INVOKEINTERFACE,
		// Type.getInternalName(Lock.class), "unlock", "()V");

		// if (threadSafe) {
		// Unlock
		// super.visitVarInsn(ALOAD, monitorIndx);
		// super.monitorExit();
		// visitLabel(monitorEndLabel);
		Label endLbl = new Label();

//		if (elementType.getSort() == Type.ARRAY) {
//			super.visitInsn(DUP);
//			super.visitInsn(ARRAYLENGTH);
//		} else
			super.visitInsn(ICONST_1);
		// super.visitVarInsn(ALOAD, monitorIndx);
		// super.monitorEnter();
		super.visitFieldInsn(getOpcode, logFieldOwner, "logsize", Type.INT_TYPE.getDescriptor());
		super.visitInsn(IADD);
		super.visitInsn(DUP);
		super.visitFieldInsn(PUTSTATIC, logFieldOwner, "logsize", Type.INT_TYPE.getDescriptor());

		super.visitLdcInsn(Constants.MAX_LOG_SIZE);
		// super.visitInsn(ISUB);
		super.visitJumpInsn(IF_ICMPLE, endLbl);
		// super.ifCmp(Type.INT_TYPE, Opcodes.IFGE, endLbl);
		// super.visitVarInsn(ALOAD, monitorIndx);
		// super.monitorExit();
		if (logFieldOwner.equals(Type.getInternalName(SerializableLog.class)))
			super.visitMethodInsn(INVOKESTATIC, Type.getInternalName(WallaceExportRunner.class), "_exportSerializable", "()V");
		else
			super.visitMethodInsn(INVOKESTATIC, Type.getInternalName(WallaceExportRunner.class), "_export", "()V");
		// super.visitVarInsn(ALOAD, monitorIndx);
		// super.monitorEnter();
		super.visitFieldInsn(getOpcode, logFieldOwner, "logsize", Type.INT_TYPE.getDescriptor());
		super.visitLdcInsn(Constants.VERY_MAX_LOG_SIZE);
		super.visitJumpInsn(IF_ICMPLE, endLbl);

		// println("GOing to wait for " + logFieldOwner);
		// super.visitLabel(tryStart);

		super.visitFieldInsn(Opcodes.GETSTATIC, Type.getInternalName(Log.class), "lock", "Ljava/lang/Object;");
		super.visitLdcInsn(500L);
		super.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "wait", "(J)V");

		// super.visitLabel(tryEnd);

		// super.visitJumpInsn(GOTO, endLbl);
		// super.visitLabel(handlerStart);
		// int n = newLocal(Type.getType(InterruptedException.class));
		// super.visitVarInsn(ASTORE, n);
		// super.visitInsn(POP);
		visitLabel(endLbl);
//		super.visitVarInsn(ALOAD, monitorIndx);
//		super.monitorExit();
		super.visitFieldInsn(GETSTATIC, Type.getInternalName(Log.class), "logLock", Type.getDescriptor(Lock.class));
		super.visitMethodInsn(INVOKEINTERFACE, Type.getInternalName(Lock.class), "unlock", "()V");
		// super.visitLocalVariable(logFieldName + "_monitor",
		// "Ljava/lang/Object;", null, monitorStart, monitorEndLabel,
		// monitorIndx);
		// }

	}


}
