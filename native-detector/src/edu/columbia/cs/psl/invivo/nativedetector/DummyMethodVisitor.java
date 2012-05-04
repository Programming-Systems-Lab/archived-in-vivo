package edu.columbia.cs.psl.invivo.nativedetector;

import org.objectweb.asm.MethodVisitor;

/**
 * @author miriammelnick
 * A MethodVisitor subclass that simply invokes the superclass. 
 * Useful when visitMethod is non-empty in ClassVisitor.
 * @see CompleteClassVisitor
 */
public class DummyMethodVisitor extends MethodVisitor{

	/**
	 * Constructor for DummyMethodVisitor. Simply invokes superclass constructor.
	 * @param api				int				Generally Opcodes.ASM4
	 * @param mv				MethodVisitor	MethodVisitor to extend (can be null)
	 */
	public DummyMethodVisitor(int api, MethodVisitor mv) {
		super(api, mv);
	}
}
