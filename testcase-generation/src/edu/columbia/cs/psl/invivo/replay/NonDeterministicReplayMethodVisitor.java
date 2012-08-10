package edu.columbia.cs.psl.invivo.replay;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

import org.apache.log4j.Logger;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AnalyzerAdapter;
import org.objectweb.asm.tree.MethodInsnNode;

import edu.columbia.cs.psl.invivo.record.CloningUtils;
import edu.columbia.cs.psl.invivo.record.Constants;
import edu.columbia.cs.psl.invivo.record.ExportedLog;
import edu.columbia.cs.psl.invivo.record.Instrumenter;
import edu.columbia.cs.psl.invivo.record.Log;
import edu.columbia.cs.psl.invivo.record.MethodCall;
import edu.columbia.cs.psl.invivo.record.visitor.CloningAdviceAdapter;
import edu.columbia.cs.psl.invivo.record.visitor.NonDeterministicLoggingMethodVisitor;

public class NonDeterministicReplayMethodVisitor extends CloningAdviceAdapter implements Constants {
	private static Logger			logger					= Logger.getLogger(NonDeterministicReplayMethodVisitor.class);
	private String					name;
	private String					desc;
	private String					classDesc;
	private int						pc;
	private boolean					isStatic;
	private boolean					constructor;
	private boolean					superInitialized;

	
	@Override
	public void visitCode() {
		super.visitCode();
		if (!constructor)
			superInitialized = true;
	}

	private boolean	isFirstConstructor;
	AnalyzerAdapter	analyzer;

	protected NonDeterministicReplayMethodVisitor(int api, MethodVisitor mv, int access, String name, String desc, String classDesc,
			boolean isFirstConstructor, AnalyzerAdapter analyzer) {
		super(api, mv, access, name, desc, classDesc, null);
		this.name = name;
		this.desc = desc;
		this.classDesc = classDesc;
		this.isStatic = (access & Opcodes.ACC_STATIC) != 0;
		this.constructor = "<init>".equals(name);
		this.isFirstConstructor = isFirstConstructor;
		this.analyzer = analyzer;
	}

	private NonDeterministicReplayClassVisitor	parent;

	public void setClassVisitor(NonDeterministicReplayClassVisitor coaClassVisitor) {
		this.parent = coaClassVisitor;
	}

	@Override
	public void visitEnd() {
		super.visitEnd();
		parent.addFieldMarkup(methodCallsToClear);
		parent.addCaptureMethodsToGenerate(captureMethodsToGenerate);
	}

	private int	lineNumber	= 0;

	@Override
	public void visitLineNumber(int line, Label start) {
		super.visitLineNumber(line, start);
		lineNumber = line;
	}

	private void loadReplayIndex(String className, String fieldName) {
		super.visitFieldInsn(GETSTATIC, className, fieldName + "_replayIndex", "Ljava/util/HashMap;");
		super.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Thread.class), "currentThread", "()Ljava/lang/Thread;");
		super.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(Thread.class), "getName", "()Ljava/lang/String;");
		super.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(HashMap.class), "containsKey", "(Ljava/lang/Object;)Z");
		Label exists = new Label();
		super.visitJumpInsn(Opcodes.IFNE, exists);
		super.visitFieldInsn(GETSTATIC, className, fieldName + "_replayIndex", "Ljava/util/HashMap;");
		super.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Thread.class), "currentThread", "()Ljava/lang/Thread;");
		super.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(Thread.class), "getName", "()Ljava/lang/String;");
		super.visitInsn(ICONST_0);
		super.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Integer.class), "valueOf", "(I)Ljava/lang/Integer;");
		super.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(HashMap.class), "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
		super.visitInsn(POP);
		super.visitLabel(exists);
		super.visitFieldInsn(GETSTATIC, className, fieldName + "_replayIndex", "Ljava/util/HashMap;");
		super.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Thread.class), "currentThread", "()Ljava/lang/Thread;");
		super.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(Thread.class), "getName", "()Ljava/lang/String;");
		super.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(HashMap.class), "get", "(Ljava/lang/Object;)Ljava/lang/Object;");
		super.visitTypeInsn(CHECKCAST, "java/lang/Integer");
		super.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(Integer.class), "intValue", "()I");
//				super.visitInsn(ICONST_0);
	}

	private void incrementReplayIndex(String className, String fieldName) {
		super.visitFieldInsn(GETSTATIC, className, fieldName + "_replayIndex", "Ljava/util/HashMap;");
		super.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Thread.class), "currentThread", "()Ljava/lang/Thread;");
		super.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(Thread.class), "getName", "()Ljava/lang/String;");
		loadReplayIndex(className, fieldName);
		super.visitInsn(ICONST_1);
		super.visitInsn(IADD);
		super.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Integer.class), "valueOf", "(I)Ljava/lang/Integer;");
		super.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(HashMap.class), "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
		super.visitInsn(POP);
	}

	private HashMap<String, MethodInsnNode>	captureMethodsToGenerate	= new HashMap<String, MethodInsnNode>();

	@Override
	public void visitMethodInsn(int opcode, String owner, String name, String desc) {

		try {
			MethodCall m = new MethodCall(this.name, this.desc, this.classDesc, pc, lineNumber, owner, name, desc, isStatic);
			Type returnType = Type.getMethodType(desc).getReturnType();
			
			if (opcode == INVOKESPECIAL && name.equals("<init>") && NonDeterministicLoggingMethodVisitor.nonDeterministicMethods.contains(owner + "." + name + ":" + desc)) {
				System.out.println(this.classDesc +"."+this.name);
				System.out.println(Replayer.instrumentedClasses.get(classDesc).superName);
				System.out.println(owner);
				System.out.println(analyzer.stack);
				if (!(owner.equals(Replayer.instrumentedClasses.get(classDesc).superName) && this.name.equals("<init>"))) {
					Type[] args = Type.getArgumentTypes(desc);
					for (int i = args.length - 1; i >= 0; i--) {
						Type t = args[i];
						if (t.getSize() == 2)
							mv.visitInsn(POP2);
						else
							mv.visitInsn(POP);
					}
					
					if (analyzer.stack != null && analyzer.stack.size() > 0
							&& analyzer.uninitializedTypes.containsKey(analyzer.stack.get(analyzer.stack.size() - 1))
							&& analyzer.uninitializedTypes.get(analyzer.stack.get(analyzer.stack.size() - 1)).equals(owner)) {
						mv.visitInsn(POP);
						if (analyzer.stack.size() > 0 && analyzer.uninitializedTypes.containsKey(analyzer.stack.get(analyzer.stack.size() - 1))
								&& analyzer.uninitializedTypes.get(analyzer.stack.get(analyzer.stack.size() - 1)).equals(owner))
							mv.visitInsn(POP);

						String replayClassName = MethodCall.getReplayClassName(Type.getType("L"+m.getMethodOwner()+";"));
						mv.visitFieldInsn(GETSTATIC, replayClassName, m.getLogFieldName(), "[Ljava/lang/Object;");

						Label fallThrough = new Label();
						loadReplayIndex(replayClassName, m.getLogFieldName());
						mv.visitInsn(DUP);

						mv.visitFieldInsn(GETSTATIC, replayClassName, m.getLogFieldName() + "_fill", "I");
						mv.visitJumpInsn(Opcodes.IF_ICMPNE, fallThrough);
						mv.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(ReplayRunner.class), "loadNextLog", "()V");
						mv.visitInsn(POP);
						loadReplayIndex(replayClassName, m.getLogFieldName());

						mv.visitLabel(fallThrough);
						//				arrayLoad(Type.getType("L"+m.getMethodOwner()+";"));
						mv.visitInsn(AALOAD);
						mv.visitTypeInsn(CHECKCAST, m.getMethodOwner());
						incrementReplayIndex(replayClassName, m.getLogFieldName());
					}

				} else {
					super.visitMethodInsn(opcode, owner, name, desc);
				}

			} else if ((!constructor || isFirstConstructor || superInitialized) && returnType.equals(Type.VOID_TYPE) && !name.equals("<init>")
					&& NonDeterministicLoggingMethodVisitor.nonDeterministicMethods.contains(owner + "." + name + ":" + desc)) {
				Type[] args = Type.getArgumentTypes(desc);
				for (int i = args.length - 1; i >= 0; i--) {
					Type t = args[i];
					if (t.getSize() == 2)
						mv.visitInsn(POP2);
					else
						mv.visitInsn(POP);
				}
				if (opcode != INVOKESTATIC)
					mv.visitInsn(POP);

				//				else
				//					super.visitMethodInsn(opcode, owner, name, desc);

			} else if ((!constructor || isFirstConstructor || superInitialized) && !returnType.equals(Type.VOID_TYPE)
					&& NonDeterministicLoggingMethodVisitor.nonDeterministicMethods.contains(owner + "." + name + ":" + desc)) {

				Label startOfPlayBack = new Label();

				super.visitFieldInsn(GETSTATIC, Type.getInternalName(Log.class), "logLock", Type.getDescriptor(Lock.class));
				super.visitMethodInsn(INVOKEINTERFACE, Type.getInternalName(Lock.class), "lock", "()V");

				logger.debug("Adding field in MV to list " + m.getLogFieldName());
				methodCallsToClear.add(m);
				Type[] args = Type.getArgumentTypes(desc);
				boolean hasArray = false;
				for (Type t : args)
					if (t.getSort() == Type.ARRAY)
						hasArray = true;

				if (hasArray) {

					Type[] targs = Type.getArgumentTypes(desc);
					for (int i = targs.length - 1; i >= 0; i--) {
						Type t = targs[i];
						if (t.getSort() == Type.ARRAY) {
							/*
							 * stack (grows down): dest (fill not incremented yet)
							 */
							String replayClassName = MethodCall.getReplayClassName(t);
							String replayFieldName = MethodCall.getLogFieldName(t);
							mv.visitFieldInsn(GETSTATIC, replayClassName, MethodCall.getLogFieldName(t), MethodCall.getLogFieldType(t)
									.getDescriptor());
							//							mv.visitFieldInsn(GETSTATIC,replayClassName, 
							//									MethodCall.getLogFieldName(t)+"_replayIndex", 
							//									"I");
							loadReplayIndex(replayClassName, replayFieldName);
							mv.visitInsn(DUP);
							mv.visitFieldInsn(GETSTATIC, replayClassName, MethodCall.getLogFieldName(t) + "_fill", "I");
							Label fallThrough = new Label();

							mv.visitJumpInsn(Opcodes.IF_ICMPNE, fallThrough);
							mv.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(ReplayRunner.class), "loadNextLog", "()V");
							pop();
							loadReplayIndex(replayClassName, replayFieldName);
							visitLabel(fallThrough);

							arrayLoad(t);

							/*
							 * stack (grows down): dest src
							 */
							swap();
							/*
							 * stack (grows down): src dest
							 */
							push(0);
							/*
							 * stack (grows down): src dest 0
							 */
							swap();
							/*
							 * stack (grows down): src 0 dest
							 */
							push(0);
							/*
							 * stack (grows down): src 0 dest 0
							 */

							mv.visitFieldInsn(GETSTATIC, replayClassName, MethodCall.getLogFieldName(t), MethodCall.getLogFieldType(t)
									.getDescriptor());
							loadReplayIndex(replayClassName, replayFieldName);
							arrayLoad(t);
							mv.visitTypeInsn(Opcodes.CHECKCAST, t.getInternalName());
							mv.visitInsn(ARRAYLENGTH);
							incrementReplayIndex(replayClassName, replayFieldName);
							/*
							 * stack: src (fill incremented) 0 dest 0 length
							 */
							mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "arraycopy", "(Ljava/lang/Object;ILjava/lang/Object;II)V");
							/*
							 * stack: dest popped
							 */
						} else {
							switch (t.getSize()) {
							case 2:
								mv.visitInsn(POP2);
								break;
							case 1:
							default:
								mv.visitInsn(POP);
								break;
							}
						}
					}

				} else {
					Type[] targs = Type.getArgumentTypes(desc);
					for (Type t : targs) {
						switch (t.getSize()) {
						case 2:
							visitInsn(POP2);
							break;
						case 1:
						default:
							visitInsn(POP);
							break;
						}
					}
				}

				if (opcode != INVOKESTATIC)
					mv.visitInsn(POP);

				if (returnType.getSort() == Type.VOID)
					mv.visitInsn(NOP);
				else {
					mv.visitFieldInsn(GETSTATIC, m.getReplayClassName(), m.getLogFieldName(), m.getLogFieldType().getDescriptor());

					loadReplayIndex(m.getReplayClassName(), m.getLogFieldName());
					mv.visitInsn(DUP);
					Label fallThrough = new Label();
					mv.visitFieldInsn(GETSTATIC, m.getReplayClassName(), m.getLogFieldName() + "_fill", "I");
					mv.visitJumpInsn(Opcodes.IF_ICMPNE, fallThrough);
					mv.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(ReplayRunner.class), "loadNextLog", "()V");
					mv.visitInsn(POP);
					loadReplayIndex(m.getReplayClassName(), m.getLogFieldName());

					mv.visitLabel(fallThrough);
					arrayLoad(m.getReturnType());
					incrementReplayIndex(m.getReplayClassName(), m.getLogFieldName());
				}
				//Unlock
				super.visitFieldInsn(GETSTATIC, Type.getInternalName(Log.class), "logLock", Type.getDescriptor(Lock.class));
				super.visitMethodInsn(INVOKEINTERFACE, Type.getInternalName(Lock.class), "unlock", "()V");

			} else {
				super.visitMethodInsn(opcode, owner, name, desc);
			}
			pc++;
		} catch (Exception ex) {
			logger.error("Unable to instrument method call", ex);
		}
	}

	private ArrayList<MethodCall>	methodCallsToClear	= new ArrayList<MethodCall>();

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
}
