package edu.columbia.cs.psl.invivo.record.visitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.AdviceAdapter;

import edu.columbia.cs.psl.invivo.record.Instrumenter;
import edu.columbia.cs.psl.invivo.record.struct.AnnotatedMethod;
import edu.columbia.cs.psl.invivo.record.struct.FieldExpression;
import edu.columbia.cs.psl.invivo.record.struct.MethodExpression;

public class MutatingFieldMethodVisitor extends CloningAdviceAdapter{
//	private class FullFieldReference{
//		private FullFieldReference parent;
//		private FullFieldReference child;
//		private Metho
//	}
	private ArrayList<FieldExpression> mutatedFieldExpressions = new ArrayList<FieldExpression>();
	private HashSet<MethodExpression> methodsWeCallIndirectPut = new HashSet<MethodExpression>();
	private AnnotatedMethod thisMethod;
	protected MutatingFieldMethodVisitor(int api, MethodVisitor mv, int access, String name, String desc, String owner) {
		super(api, mv, access, name, desc);
		thisMethod = Instrumenter.getAnnotatedMethod(owner, name, desc);
		
		mutatedFieldExpressions.addAll(thisMethod.getPutFieldInsns());
		
		
	}
	
//	private ArrayList<String> buildMethodsMutable(String parent, MethodExpression method)
//	{
//		for(MethodExpression e : method.getMethod().functionsThatICall)
//		{
//			if(e.getMethod().isMutatesFieldsDirectly())
//			{
//				
//			}
//			if(e.getMethod().isMutatesFields())
//			{
//				
//			}
//		}
//	}
	
	@Override
	protected void onMethodEnter() {
		super.onMethodEnter();
		//Soo... what fields is that exactly?
		
	}
	/**
	 * If this method directly changes fields, store a local variable with the original value at time of change
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
		// TODO Auto-generated method stub
		super.visitFieldInsn(opcode, owner, name, desc);
	}
}
