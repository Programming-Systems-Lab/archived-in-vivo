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
				fieldType.getSort() == Type.VOID || 
				(fieldType.getSort() != Type.ARRAY && (fieldType.getSort() != Type.OBJECT || immutableClasses.contains(typeOfField)))) {
//			println("reference> " + debug);
//						println(debug);
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
//		super.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(Log.class), "grow"+logFieldName, "()V");
	
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

			if(elementType.getSort() == Type.ARRAY)
			{
				super.visitInsn(DUP);
				super.visitInsn(ARRAYLENGTH);
			}
			else
				super.visitInsn(ICONST_1);
			super.visitFieldInsn(getOpcode, logFieldOwner, "logsize", Type.INT_TYPE.getDescriptor());
			super.visitInsn(IADD);
			super.visitInsn(DUP);
			super.visitFieldInsn(PUTSTATIC, logFieldOwner, "logsize", Type.INT_TYPE.getDescriptor());
			super.visitLdcInsn(Constants.MAX_LOG_SIZE);
//			super.visitInsn(ISUB);
			super.visitJumpInsn(IF_ICMPGE, endLbl);
//			super.ifCmp(Type.INT_TYPE, Opcodes.IFGE, endLbl);
			super.visitMethodInsn(INVOKESTATIC, Type.getInternalName(WallaceExportRunner.class), "_export", "()V");

			visitLabel(endLbl);
			super.visitLocalVariable(logFieldName + "_monitor", "Ljava/lang/Object;", null, monitorStart, monitorEndLabel, monitorIndx);
//		}

	}
}
