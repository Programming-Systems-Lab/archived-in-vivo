package edu.columbia.cs.psl.invivo.record.visitor;

import java.util.ArrayList;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.tree.FieldNode;

import edu.columbia.cs.psl.invivo.record.Constants;
import edu.columbia.cs.psl.invivo.record.MethodCall;

public class COAClassVisitor extends ClassVisitor implements Opcodes{

	private String className;
	private boolean isAClass = true;
	public COAClassVisitor(int api, ClassVisitor cv) {
		super(api, cv);

	}

	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		super.visit(version, access, name, signature, superName, interfaces);
		this.className = name;
		if((access & Opcodes.ACC_INTERFACE) != 0)
			isAClass = false;
	}

	@Override
	public MethodVisitor visitMethod(int acc, String name, String desc,
			String signature, String[] exceptions) {
		//TODO need an annotation to disable doing this to some apps
		if(isAClass && className.startsWith("edu"))
		{
			MethodVisitor mv = cv.visitMethod(acc, name, desc, signature,
					exceptions);
			COAMethodVisitor cloningMV = new COAMethodVisitor(Opcodes.ASM4, mv, acc, name, desc,className);
			cloningMV.setClassVisitor(this);
			return cloningMV;
		}
		else
			return 	cv.visitMethod(acc, name, desc, signature,
					exceptions);
	}

	@Override
	public void visitEnd() {
		//TODO need an annotation to disable doing this to some apps
		if(isAClass && className.startsWith("edu"))
		{
			MethodVisitor mva = cv.visitMethod(Opcodes.ACC_PRIVATE, Constants.ARRAY_INIT_METHOD, "()V", null, null);
			GeneratorAdapter mv = new GeneratorAdapter(mva, Opcodes.ACC_PRIVATE, Constants.ARRAY_INIT_METHOD, "()V");
			mv.visitCode();
			for(MethodCall call : loggedMethodCalls)
			{
				if(call.isStatic()) mv.loadThis();
				mv.push(Constants.DEFAULT_LOG_SIZE);
				mv.newArray(Type.getMethodType(call.getMethodDesc()).getReturnType());
				if(call.isStatic())
					mv.putStatic(Type.getType(className), call.getLogFieldName(), Type.getType("["+Type.getMethodType(call.getMethodDesc()).getReturnType().getDescriptor()));
				else
					mv.putField(Type.getType(className), call.getLogFieldName(), Type.getType("["+Type.getMethodType(call.getMethodDesc()).getReturnType().getDescriptor()));
			}
			mv.visitMaxs(0, 0);
			mv.returnValue();
			mv.visitEnd();
			for(MethodCall call : loggedMethodCalls)
			{
				int opcode = Opcodes.ACC_PRIVATE;
				if(call.isStatic())
					opcode = opcode | Opcodes.ACC_STATIC;
				FieldNode fn = new FieldNode(Opcodes.ASM4, opcode,
						call.getLogFieldName(),
						"["+Type.getMethodType(call.getMethodDesc()).getReturnType().getDescriptor(),null,null);
				fn.accept(cv);
				FieldNode fn2 = new FieldNode(Opcodes.ASM4, opcode,
						call.getLogFieldName()+"_fill",
						Type.INT_TYPE.getDescriptor(),null,0);
				fn2.accept(cv);
			}
		}
		super.visitEnd();
	}
	private ArrayList<MethodCall> loggedMethodCalls = new ArrayList<MethodCall>();
	public void addFieldMarkup(ArrayList<MethodCall> calls) {
		loggedMethodCalls.addAll(calls);
		//TODO also setup the new method to retrieve the list of replacements for the method
	}
}
