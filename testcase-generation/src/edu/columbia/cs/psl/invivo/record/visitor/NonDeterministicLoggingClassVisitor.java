package edu.columbia.cs.psl.invivo.record.visitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.log4j.Logger;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.JSRInlinerAdapter;
import org.objectweb.asm.commons.LocalVariablesSorter;
import org.objectweb.asm.tree.MethodInsnNode;

import edu.columbia.cs.psl.invivo.record.Constants;
import edu.columbia.cs.psl.invivo.record.MethodCall;

public class NonDeterministicLoggingClassVisitor extends ClassVisitor implements Opcodes {

	private String className;
	private boolean isAClass = true;

	public NonDeterministicLoggingClassVisitor(int api, ClassVisitor cv) {
		super(api, cv);

	}

	private static Logger logger = Logger.getLogger(NonDeterministicLoggingClassVisitor.class);

	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		super.visit(version, access, name, signature, superName, interfaces);
		this.className = name;

		logger.debug("Visiting " + name + " for instrumentation");
		if ((access & Opcodes.ACC_INTERFACE) != 0)
			isAClass = false;
	}

	private boolean isFirstConstructor = true;

	@Override
	public MethodVisitor visitMethod(int acc, String name, String desc, String signature, String[] exceptions) {
		// TODO need an annotation to disable doing this to some apps
		if (isAClass && !name.equals(Constants.INNER_COPY_METHOD_NAME) && !name.equals(Constants.OUTER_COPY_METHOD_NAME) && !name.equals(Constants.SET_FIELDS_METHOD_NAME)
				&& !className.startsWith("org/apache/juli")
				)
		{
			MethodVisitor smv = cv.visitMethod(acc, name, desc, signature, exceptions);
			JSRInlinerAdapter mv = new JSRInlinerAdapter(smv, acc, name, desc, signature, exceptions);
			LocalVariablesSorter sorter  = new LocalVariablesSorter(acc, desc, mv);
			// CheckMethodAdapter cmv = new CheckMethodAdapter(mv);
			NonDeterministicLoggingMethodVisitor cloningMV = new NonDeterministicLoggingMethodVisitor(Opcodes.ASM4, sorter, acc, name, desc, className, isFirstConstructor);
			if (name.equals("<init>"))
				isFirstConstructor = false;
			cloningMV.setClassVisitor(this);
			return cloningMV;
		} else
			return cv.visitMethod(acc, name, desc, signature, exceptions);
	}

	public HashSet<MethodCall> getLoggedMethodCalls() {
		return loggedMethodCalls;
	}

	private HashSet<MethodCall> loggedMethodCalls = new HashSet<MethodCall>();
	private HashMap<String, MethodInsnNode> captureMethodsToGenerate = new HashMap<String, MethodInsnNode>();

	public void addFieldMarkup(Collection<MethodCall> calls) {
		logger.debug("Received field markup from method visitor (" + calls.size() + ")");
		loggedMethodCalls.addAll(calls);
		// TODO also setup the new method to retrieve the list of replacements
		// for the method
	}

	@Override
	public void visitEnd() {
		super.visitEnd();
		for (String logFieldName : captureMethodsToGenerate.keySet()) {
			MethodInsnNode mi = captureMethodsToGenerate.get(logFieldName);
			String methodDesc = mi.desc;

			String captureDesc = mi.desc;
			
			int opcode = Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC;
			if(mi.getOpcode() == Opcodes.INVOKESPECIAL && !mi.name.equals("<init>"))
				opcode = Opcodes.ACC_PRIVATE;
			else if (mi.getOpcode() != Opcodes.INVOKESTATIC) {
				// Need to put owner of the method on the top of the args list
				captureDesc = "(L" + mi.owner + ";";
				for (Type t : Type.getArgumentTypes(mi.desc))
					captureDesc += t.getDescriptor();
				captureDesc += ")" + Type.getReturnType(mi.desc).getDescriptor();
			}
			MethodVisitor mv = super.visitMethod(opcode, logFieldName + "_capture", captureDesc, null, null);
			CloningAdviceAdapter caa = new CloningAdviceAdapter(Opcodes.ASM4, mv, opcode, logFieldName + "_capture", captureDesc, className);
			Type[] args = Type.getArgumentTypes(captureDesc);
			if(opcode == Opcodes.ACC_PRIVATE)
				caa.loadThis();
			for (int i = 0; i < args.length; i++) {
				caa.loadArg(i);
			}
			mv.visitMethodInsn(mi.getOpcode(), mi.owner, mi.name, mi.desc);
			for (int i = 0; i < args.length; i++) {
				if (args[i].getSort() == Type.ARRAY) {
					caa.loadArg(i);
					//- (mi.getOpcode() == Opcodes.INVOKESTATIC ? 0 : 1)
					caa.logValueAtTopOfStackToArray(className + Constants.LOG_CLASS_SUFFIX, logFieldName + "_" + (i - (mi.getOpcode() == Opcodes.INVOKESTATIC || opcode == Opcodes.ACC_PRIVATE ? 0 : 1)), "[" + args[i].getDescriptor(),
							args[i], true, mi.name+"_"+i+"\t"+args[i].getDescriptor());
					if (args[i].getSize() == 1)
						caa.pop();
					else
						caa.pop2();
				}
			}
			caa.returnValue();
			mv.visitMaxs(0, 0);
			mv.visitEnd();
		}
		
		
		if (isAClass) {
			//TODO re-enable this
//			{
//				MethodVisitor mv = this.visitMethod(Opcodes.ACC_PUBLIC, Constants.INNER_COPY_METHOD_NAME, "()L" + className + ";", null, null);
//				CloningAdviceAdapter cloningAdapter = new CloningAdviceAdapter(Opcodes.ASM4, mv, Opcodes.ACC_PUBLIC, Constants.INNER_COPY_METHOD_NAME, "()L" + className + ";", className);
//				cloningAdapter.generateCopyMethod();
//				mv.visitMaxs(0, 0);
//				cloningAdapter.returnValue();
//				mv.visitEnd();
//			}
//			{
//				MethodVisitor mv = this.visitMethod(Opcodes.ACC_PUBLIC, Constants.OUTER_COPY_METHOD_NAME, "()L" + className + ";", null, null);
//				CloningAdviceAdapter cloningAdapter = new CloningAdviceAdapter(Opcodes.ASM4, mv, Opcodes.ACC_PUBLIC, Constants.OUTER_COPY_METHOD_NAME, "()L" + className + ";", className);
//				cloningAdapter.generateOuterCopyMethod();
//				mv.visitMaxs(0, 0);
//				cloningAdapter.returnValue();
//				mv.visitEnd();
//			}
//			{
//				MethodVisitor mv = this.visitMethod(Opcodes.ACC_PUBLIC, Constants.SET_FIELDS_METHOD_NAME, "(L" + className + ";)L" + className + ";", null, null);
//				CloningAdviceAdapter cloningAdapter = new CloningAdviceAdapter(Opcodes.ASM4, mv, Opcodes.ACC_PUBLIC, Constants.SET_FIELDS_METHOD_NAME, "(L" + className + ";)L" + className + ";",
//						className);
//				cloningAdapter.generateSetFieldsMethod();
//				mv.visitMaxs(0, 0);
//				cloningAdapter.returnValue();
//				mv.visitEnd();
//			}
		}
	}

	public String getClassName() {
		return className;
	}

	public void addCaptureMethodsToGenerate(HashMap<String, MethodInsnNode> captureMethodsToGenerate) {
		this.captureMethodsToGenerate.putAll(captureMethodsToGenerate);
	}

}
