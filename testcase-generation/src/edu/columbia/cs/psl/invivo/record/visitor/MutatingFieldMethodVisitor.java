package edu.columbia.cs.psl.invivo.record.visitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.AbstractMap.SimpleEntry;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.commons.Method;

import com.rits.cloning.Cloner;

import edu.columbia.cs.psl.invivo.record.Constants;
import edu.columbia.cs.psl.invivo.record.Instrumenter;
import edu.columbia.cs.psl.invivo.record.struct.AnnotatedMethod;
import edu.columbia.cs.psl.invivo.record.struct.Expression;
import edu.columbia.cs.psl.invivo.record.struct.FieldExpression;
import edu.columbia.cs.psl.invivo.record.struct.MethodExpression;

public class MutatingFieldMethodVisitor extends CloningAdviceAdapter {
	// private class FullFieldReference{
	// private FullFieldReference parent;
	// private FullFieldReference child;
	// private Metho
	// }
	private ArrayList<FieldExpression> mutatedFieldExpressions = new ArrayList<FieldExpression>();
	private HashSet<MethodExpression> methodsWeCallIndirectPut = new HashSet<MethodExpression>();
	private AnnotatedMethod thisMethod;
	private String owner;
	private String name;
	private int access;
	protected MutatingFieldMethodVisitor(int api, MethodVisitor mv, int access, String name, String desc, String owner) {
		super(api, mv, access, name, desc);
		thisMethod = Instrumenter.getAnnotatedMethod(owner, name, desc);
		this.owner = owner;
		this.name = name;
		this.access = access;
		mutatedFieldExpressions.addAll(thisMethod.getPutFieldInsns());

	}

	private ArrayList<SimpleEntry<Expression, FieldExpression>> buildMethodsMutable(String parent, MethodExpression method) {
		for (MethodExpression e : method.getMethod().functionsThatICall) {
			if (e.getMethod().isMutatesFieldsDirectly()) {

			}
			if (e.getMethod().isMutatesFields()) {

			}
		}
		return null;
	}

	@Override
	protected void onMethodEnter() {
		super.onMethodEnter();
		// Soo... what fields is that exactly?

	}

	/**
	 * If this method directly changes fields, store a local variable with the
	 * original value at time of change
	 * 
	 */
	@Override
	public void visitMethodInsn(int opcode, String owner, String name, String desc) {
		// TODO Auto-generated method stub
		super.visitMethodInsn(opcode, owner, name, desc);
	}

	/**
	 * When visiting a putfield, we need to log the value (duh)
	 */
	@Override
	public void visitFieldInsn(int opcode, String owner, String name, String desc) {
//		if (opcode == GETFIELD && desc.length() > 1) // Do this in the case of
//														// objects only
//		{
//			// variablesToClear.put(name,desc);
//			Label lblbForReadThrough = new Label();
//			dup();
//			super.visitFieldInsn(GETFIELD, owner, Constants.BEEN_CLONED_PREFIX + name, Type.BOOLEAN_TYPE.getDescriptor());
//			visitJumpInsn(IFNE, lblbForReadThrough);
//
//			dup();
//			dup();
//			super.visitFieldInsn(GETSTATIC, "edu/columbia/cs/psl/invivo/record/CloningUtils", "cloner", "Lcom/rits/cloning/Cloner;");
//			swap();
//			super.visitFieldInsn(opcode, owner, name, desc);
//			invokeVirtual(Type.getType(Cloner.class), Method.getMethod("Object deepClone(Object)"));
//			checkCast(Type.getType(desc));
//			super.visitFieldInsn(Opcodes.PUTFIELD, owner, Constants.PREV_VALUE_PREFIX + name, desc);
//
//			dup();
//			visitLdcInsn(true);
//			super.visitFieldInsn(Opcodes.PUTFIELD, owner, Constants.BEEN_CLONED_PREFIX + name, Type.BOOLEAN_TYPE.getDescriptor());
//
//			visitLabel(lblbForReadThrough);
//
//			super.visitFieldInsn(opcode, owner, name, desc);
//		}
		if(this.name.equals("<init>") || this.name.equals("<clinit>") || (this.access & Opcodes.ACC_STATIC) != 0)
		{
			super.visitFieldInsn(opcode, owner, name, desc);
		}
		else if (opcode == PUTFIELD && desc.length() == 1 ) // If we are going
																// to do
		// a putfield on a primitive do a simple copy
		{
			// variablesToClear.put(name,desc);
//			dup();
			
			super.visitFieldInsn(opcode, owner, name, desc);
		} else if (opcode == PUTFIELD && desc.length() > 1 && owner.equals(this.owner)) // Need a copy on a
		// putfield for objects too
		{
//			 variablesToClear.put(name,desc);
			Label lblbForReadThrough = new Label();

//			swap();
//			dup();
			
			loadThis();
			super.visitFieldInsn(GETFIELD, owner, Constants.BEEN_CLONED_PREFIX + name, Type.BOOLEAN_TYPE.getDescriptor());
			visitJumpInsn(IFNE, lblbForReadThrough);

//			dup();
//			dup();
			loadThis();
			loadThis();

			super.visitFieldInsn(GETFIELD, owner, name, desc);
			generateCloneOf(desc);
//			super.visitFieldInsn(GETSTATIC, "edu/columbia/cs/psl/invivo/record/CloningUtils", "cloner", "Lcom/rits/cloning/Cloner;");
//			swap();
//			invokeVirtual(Type.getType(Cloner.class), Method.getMethod("Object deepClone(Object)"));
//			checkCast(Type.getType(desc));
			super.visitFieldInsn(Opcodes.PUTFIELD, owner, Constants.PREV_VALUE_PREFIX + name, desc);

//			dup();
			loadThis();
			super.push(true);
			super.visitFieldInsn(Opcodes.PUTFIELD, owner, Constants.BEEN_CLONED_PREFIX + name, Type.BOOLEAN_TYPE.getDescriptor());

			visitLabel(lblbForReadThrough);
//			swap();
			super.visitFieldInsn(opcode, owner, name, desc);

		} else
			super.visitFieldInsn(opcode, owner, name, desc);

//		super.visitFieldInsn(opcode, owner, name, desc);
	}
}
