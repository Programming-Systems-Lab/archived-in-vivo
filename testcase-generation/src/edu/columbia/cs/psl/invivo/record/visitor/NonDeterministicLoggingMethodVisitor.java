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

public class NonDeterministicLoggingMethodVisitor extends CloningAdviceAdapter implements Constants {
	private static Logger			logger					= Logger.getLogger(NonDeterministicLoggingMethodVisitor.class);
	private String					name;
	private String					desc;
	private String					classDesc;
	private int						pc;
	private static HashSet<String>	nonDeterministicMethods	= new HashSet<String>();
	private boolean					isStatic;
	private boolean					constructor;
	private boolean					superInitialized;

	static {
		File f = new File("nondeterministic-methods.txt");
		Scanner s;
		try {
			s = new Scanner(f);
			while (s.hasNextLine())
				nonDeterministicMethods.add(s.nextLine());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void visitCode() {
		super.visitCode();
		if (!constructor)
			superInitialized = true;
	}

	private boolean	isFirstConstructor;

	protected NonDeterministicLoggingMethodVisitor(int api, MethodVisitor mv, int access, String name, String desc, String classDesc,
			boolean isFirstConstructor) {
		super(api, mv, access, name, desc);
		this.name = name;
		this.desc = desc;
		this.classDesc = classDesc;
		this.isStatic = (access & Opcodes.ACC_STATIC) != 0;
		this.constructor = "<init>".equals(name);
		this.isFirstConstructor = isFirstConstructor;
	}

	private NonDeterministicLoggingClassVisitor	parent;

	public void setClassVisitor(NonDeterministicLoggingClassVisitor coaClassVisitor) {
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

	private int	lineNumber	= 0;

	@Override
	public void visitLineNumber(int line, Label start) {
		super.visitLineNumber(line, start);
		lineNumber = line;
	}

	@Override
	public void visitMethodInsn(int opcode, String owner, String name, String desc) {
		try {
			MethodCall m = new MethodCall(this.name, this.desc, this.classDesc, pc, lineNumber, owner, name, desc, isStatic);
			Type returnType = Type.getMethodType(desc).getReturnType();
			if ((!constructor || isFirstConstructor || superInitialized) && !returnType.equals(Type.VOID_TYPE)
					&& nonDeterministicMethods.contains(owner + "." + name + ":" + desc)) {
				logger.debug("Adding field in MV to list " + m.getLogFieldName());
				methodCallsToClear.add(m);
				super.visitMethodInsn(opcode, owner, name, desc);
				logValueAtTopOfStackToArray(Constants.LOG_DUMP_CLASS, m.getLogFieldName(), m.getLogFieldType().getDescriptor(), returnType, true);
			} else
				super.visitMethodInsn(opcode, owner, name, desc);
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
