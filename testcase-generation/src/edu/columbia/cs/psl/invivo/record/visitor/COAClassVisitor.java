package edu.columbia.cs.psl.invivo.record.visitor;

import java.util.HashMap;


import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.FieldNode;

import edu.columbia.cs.psl.invivo.record.Constants;

public class COAClassVisitor extends ClassVisitor implements Opcodes{

	private String className;
	private boolean isAClass = true;
	public COAClassVisitor(int api, ClassVisitor cv) {
		super(api, cv);
		// TODO Auto-generated constructor stub
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
		super.visitEnd();
		for(String fieldName : fields.keySet())
		{
			FieldNode fn = new FieldNode(Opcodes.ASM4, Opcodes.ACC_PRIVATE,
					Constants.BEEN_CLONED_PREFIX + fieldName,
					Type.BOOLEAN_TYPE.getDescriptor(),null,false);
			fn.accept(cv);
			
			FieldNode fn2 = new FieldNode(Opcodes.ASM4, Opcodes.ACC_PRIVATE,
					Constants.PREV_VALUE_PREFIX + fieldName,
					fields.get(fieldName),null,null);
			fn2.accept(cv);
		}
	}
	private HashMap<String, String> fields = new HashMap<String, String>(); //Name -> Desc
	public void addFieldMarkup(String name, String desc, HashMap<String, String> variablesToClear) {
		for(String vName : variablesToClear.keySet())
			fields.put(vName, variablesToClear.get(vName));
		//TODO also setup the new method to retrieve the list of replacements for the method
	}
}
