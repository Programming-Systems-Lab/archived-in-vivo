package edu.columbia.cs.psl.invivo.record.visitor;

import java.util.HashMap;
import java.util.LinkedList;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.JSRInlinerAdapter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.VarInsnNode;

import edu.columbia.cs.psl.invivo.record.Constants;
import edu.columbia.cs.psl.invivo.record.Instrumenter;
import edu.columbia.cs.psl.invivo.record.struct.Expression;
import edu.columbia.cs.psl.invivo.record.struct.FieldExpression;
import edu.columbia.cs.psl.invivo.record.struct.SimpleExpression;

public class MutatingFieldClassVisitor extends ClassVisitor {

	public MutatingFieldClassVisitor(int api, ClassVisitor cv) {
		super(api, cv);
	}

	private String className;

	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		// TODO Auto-generated method stub
		super.visit(version, access, name, signature, superName, interfaces);
		className = name;
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {

		MethodVisitor smv = super.visitMethod(access, name, desc, signature, exceptions);
		JSRInlinerAdapter  mv = new JSRInlinerAdapter(smv, access, name, desc, signature, exceptions);

		if (Instrumenter.getAnnotatedMethod(className, name, desc).isMutatesFieldsDirectly()) {
			for (FieldExpression f : Instrumenter.getAnnotatedMethod(className, name, desc).getPutFieldInsns()) {
//				System.out.println("\t" + f.getName());
//				System.out.println("\t\t" + f.getParent());
//				if(f.getParent() != null && f.getParent().getOpcode() == Opcodes.ALOAD && ((VarInsnNode) ((SimpleExpression) f.getParent()).getInsn()) == null )
//				{
//					System.out.println(f);
//				}

				if(f.getOwner().equals(className) && f.getOpcode() != Opcodes.PUTSTATIC)
//				if (f.getParent() != null && f.getParent().getOpcode() == Opcodes.ALOAD && ((VarInsnNode) ((SimpleExpression) f.getParent()).getInsn()).var == 0)
				{
					putExpressions.put(f.getName(), f);
				}
			}
		}
		if (Instrumenter.getAnnotatedMethod(className, name, desc).isMutatesFields())
			return new MutatingFieldMethodVisitor(access, mv, access, name, desc, className);
		else
			return mv;
	}

	private HashMap<String, FieldExpression> putExpressions = new HashMap<String, FieldExpression>();

	@Override
	public void visitEnd() {
		for (FieldExpression f : putExpressions.values()) {

			FieldNode fn = new FieldNode(Opcodes.ASM4, Opcodes.ACC_PUBLIC,
					Constants.BEEN_CLONED_PREFIX + f.getName(),
					Type.BOOLEAN_TYPE.getDescriptor(), null, null);
			fn.accept(cv);

			FieldNode fn2 = new FieldNode(Opcodes.ASM4, Opcodes.ACC_PUBLIC,
					Constants.PREV_VALUE_PREFIX + f.getName(),
					f.getDesc(), null, null);

			fn2.accept(cv);
		}
		super.visitEnd();
	}
}
