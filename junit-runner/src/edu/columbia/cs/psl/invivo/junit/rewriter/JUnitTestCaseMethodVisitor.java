package edu.columbia.cs.psl.invivo.junit.rewriter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.commons.GeneratorAdapter;

import edu.columbia.cs.psl.invivo.junit.rewriter.JUnitInvivoMethodDescription.VariableReplacement;

public class JUnitTestCaseMethodVisitor extends AdviceAdapter {
	private JUnitInvivoMethodDescription jdesc;
	private int access;
	private final Type[] argumentTypes;
	
	private int movedFirstLocal = 0;
	private boolean changed = false;
	public JUnitTestCaseMethodVisitor(int api, MethodVisitor mv, int access, String name, String desc, JUnitInvivoMethodDescription jdesc) {
		super(api, mv, access, name, desc);
		this.jdesc = jdesc;
		this.access = access;
        this.argumentTypes = Type.getArgumentTypes(desc);
        for(VariableReplacement r: jdesc.replacements)
        {
        	changed = true;
        	movedFirstLocal += r.type.getSize();
        }
	}
	
	private HashSet<Integer> removedLocalVariables = new HashSet<Integer>();
	@Override
	public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
			super.visitLocalVariable(name, desc, signature, start, end, index + (name.equals("this") ? 0 : movedFirstLocal));
	}
   private int getArgIndex(final int arg) {
        int index = (access & Opcodes.ACC_STATIC) == 0 ? 1 : 0;
        for (int i = 0; i < arg; i++) {
            index += argumentTypes[i].getSize();
        }
        return index;
    }
	@Override
	public void visitMaxs(int maxStack, int maxLocals) {
		// TODO Auto-generated method stub
		super.visitMaxs(maxStack, movedFirstLocal + maxLocals);
	}
   private int getFirstLocal()
   {
	   int index = (access & Opcodes.ACC_STATIC) == 0 ? 1 : 0;
       for (int i = 0; i < argumentTypes.length; i++) {
           index += argumentTypes[i].getSize();
       }
       return index;
   }
   @Override
public void visitEnd() {
	   end = new Label();
		visitLabel(end);
		for(VariableReplacement s : jdesc.replacements)
		{
			super.visitLocalVariable("_arg_"+s.from, s.type.getDescriptor(), null, start, end, s.argIndx);
		}
	   //	   commit();
}
   @Override
public void visitCode() {
	   onMethodEnter();
	super.visitCode();
}
   int firstLocal;
   private Label start;
   private Label end;
	protected void onMethodEnter() {
		start = new Label();
		visitLabel(start);
		firstLocal = getFirstLocal();
//		System.out.println("First local is at " + firstLocal);
		if(argumentTypes.length > 0)
		for(VariableReplacement s : jdesc.replacements)
		{
			
//			mv.visitVarInsn(argumentTypes[s.argIndx].getOpcode(Opcodes.ILOAD), getArgIndex(s.argIndx));
//			mv.visitVarInsn(argumentTypes[s.argIndx].getOpcode(Opcodes.ISTORE), s.indx + firstLocal);
//			removedLocalVariables.add(s.indx);
			//This should do following: 1 - Load the arg, 2 - Store to the local variable
//			mv.visitVarInsn(s.type.getOpcode(Opcodes.ILOAD), s.indx);
//			super.push(sType);
		}
	}
	@Override
	public void visitVarInsn(int opcode, int var) {
//		if(opcode >= Opcodes.ISTORE && removedLocalVariables.contains(var)) //catch all stores that go into removed variables
//			removeLastPush();
//		else
//		System.out.println("visiting " + (var + ( ((access & Opcodes.ACC_STATIC) == 0 && var == 0)? 0 : firstLocal)) + " instead of " +var);
			super.visitVarInsn(opcode, var + ( ((access & Opcodes.ACC_STATIC) == 0 && var == 0)? 0 : firstLocal));
	}
	@Override
	  public void visitFrame(
		        final int type,
		        final int nLocal,
		        final Object[] local,
		        final int nStack,
		        final Object[] stack)
		    {
		  System.out.println(">>>>Visit frame");
		        if (type != Opcodes.F_NEW) { // uncompressed frame
		            throw new IllegalStateException("ClassReader.accept() should be called with EXPAND_FRAMES flag");
		        }

		        if (!changed) { // optimization for the case where mapping = identity
		            mv.visitFrame(type, nLocal, local, nStack, stack);
		            return;
		        }
		        // copies types from 'local' to 'newLocals'
		        // 'newLocals' already contains the variables added with 'newLocal'
		        Object[] newLocals = new Object[nLocal + movedFirstLocal];
		        int index = 0;
		        for(VariableReplacement vr : jdesc.replacements)
		        {
		        	newLocals[index] = vr.type;
		        	index++;
		        }
		        for(Object o : local)
		        {
		        	newLocals[index] = o;
		        	index++;
		        }
		        System.out.println("Reporting max nLocals as " + index +" instead of " + nLocal);
		        // visits remapped frame
		        mv.visitFrame(type, index, newLocals, nStack, stack);

		    }
}
