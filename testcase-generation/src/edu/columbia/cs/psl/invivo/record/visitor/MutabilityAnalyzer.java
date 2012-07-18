package edu.columbia.cs.psl.invivo.record.visitor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Stack;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import edu.columbia.cs.psl.invivo.record.struct.ConstantInsnAdapter;
import edu.columbia.cs.psl.invivo.record.struct.IReadableInstance;
import edu.columbia.cs.psl.invivo.record.struct.MethodInstance;
import edu.columbia.cs.psl.invivo.record.struct.FieldInvocation;
import edu.columbia.cs.psl.invivo.record.struct.MethodInvocation;

public class MutabilityAnalyzer implements Opcodes {
	HashMap<String, MethodInstance> lookupCache = new HashMap<String, MethodInstance>();

	public void Analyze(ClassReader cr) {
		ClassNode cn = new ClassNode();
		cr.accept(cn, ClassReader.SKIP_DEBUG);

		for (Object o : cn.methods) {
			MethodNode mn = (MethodNode) o;
			MethodInstance mi = findOrAddMethod(cn.name, mn);
			ListIterator i = mn.instructions.iterator();
			while (i.hasNext()) {
				AbstractInsnNode n = (AbstractInsnNode) i.next();
				if (n.getType() == AbstractInsnNode.FIELD_INSN) // Field
																// Instruction
				{
					FieldInsnNode fn = (FieldInsnNode) n;
					if (n.getOpcode() == Opcodes.PUTSTATIC) {
						// This instruction is changing a static field. Previous
						// instruction is the value to set to
						FieldInvocation pi = new FieldInvocation(fn.name, fn.owner, fn.desc, n.getOpcode());
						mi.getPutFieldInsns().add(pi);
					} else if (n.getOpcode() == Opcodes.PUTFIELD) {

						// This instruction is changing a field.
						// Previous instruction will have the value that we are
						// setting to
						FieldInvocation pi = new FieldInvocation(fn.name, fn.owner, fn.desc, n.getOpcode());
						pi.setParent(parentInstructionOf(mn, pi, mn.instructions.iterator(i.previousIndex())));
						printParents(pi, 0);
					}
				} else if (n.getType() == AbstractInsnNode.METHOD_INSN) // Method
																		// invocation
				{

				} else if (n.getType() == AbstractInsnNode.INVOKE_DYNAMIC_INSN) // Invoke
																				// dynamic
				{

				}
			}
		}
	}

	private void printParents(IReadableInstance ir, int indent) {
		for (int i = 0; i < indent; i++)
			System.out.print(" ");
		System.out.println(ir);
		if (ir.getParent() != null)
			printParents(ir.getParent(), indent + 1);
	}

	private IReadableInstance parentInstructionOf(MethodNode mn, IReadableInstance insnToFindParentOf, ListIterator<?> i) {
		int nToSkip = insnToFindParentOf.getStackElementsToSkip();
		while (nToSkip > 0 ) {
			AbstractInsnNode n = (AbstractInsnNode) i.previous();
			switch (n.getType()) {
			case AbstractInsnNode.METHOD_INSN:
				MethodInsnNode min = (MethodInsnNode) n;
				MethodInvocation mi = new MethodInvocation(findOrAddMethod(min.owner, min.name, min.desc, min.getOpcode() == Opcodes.INVOKESTATIC ? Opcodes.ACC_STATIC : 0), min.getOpcode());
				System.out.println("Encountered " + mi + ", skipping " + mi.getStackElementsToSkip());
				nToSkip--;
				nToSkip += mi.getStackElementsToSkip();
				System.out.println("NTos" + nToSkip);
				if (nToSkip == 0) {
					mi.setParent(parentInstructionOf(mn, mi, mn.instructions.iterator(i.previousIndex() + 1)));
					return mi;
				}
				break;
			case AbstractInsnNode.INVOKE_DYNAMIC_INSN:
				// TODO
				break;
			case AbstractInsnNode.FIELD_INSN:
				FieldInsnNode fn = (FieldInsnNode) n;
				if (n.getOpcode() == Opcodes.GETFIELD) {
					FieldInvocation fi = new FieldInvocation(fn.name, fn.owner, fn.desc, n.getOpcode());
					System.out.println("Encoutnered" + fi);
					if (nToSkip == 0) {
						fi.setParent(parentInstructionOf(mn, fi, mn.instructions.iterator(i.previousIndex() + 1)));
						return fi;
					}
					nToSkip--;
					nToSkip += 2;
				} else if (n.getOpcode() == Opcodes.GETSTATIC) {
					FieldInvocation fi = new FieldInvocation(fn.name, fn.owner, fn.desc, n.getOpcode());
					if (nToSkip == 0) {
						fi.setParent(parentInstructionOf(mn, fi, mn.instructions.iterator(i.previousIndex() + 1)));
						return fi;
					}
					nToSkip--;
					nToSkip += 1;
				}
				break;
			case AbstractInsnNode.INT_INSN:
			case AbstractInsnNode.LDC_INSN:
			case AbstractInsnNode.VAR_INSN:
				switch (n.getOpcode()) {
				case Opcodes.ILOAD:
				case Opcodes.LLOAD:
				case Opcodes.FLOAD:
				case Opcodes.DLOAD:
				case Opcodes.ALOAD:
				case BIPUSH:
				case SIPUSH:
				case Opcodes.LDC:
					nToSkip--;
					if (nToSkip == 0) {
						return new ConstantInsnAdapter(n);
					}
					break;
				case ISTORE:
				case LSTORE:
				case FSTORE:
				case DSTORE:
				case ASTORE:
					nToSkip--;
					nToSkip++;
					break;
				case LALOAD:
				case FALOAD:
				case AALOAD:
				case BALOAD:
				case CALOAD:
				case SALOAD:
					nToSkip--;
					nToSkip += 2;
					break;
				case AASTORE:
				case IASTORE:
				case FASTORE:
				case DASTORE:
				case BASTORE:
				case CASTORE:
				case SASTORE:
					nToSkip += 3;
					break;
				case NEWARRAY:
					nToSkip--;
					nToSkip++;
				default:
					System.out.println("Unknown opcode " + n.getOpcode());
					break;
				}
				break;
			case AbstractInsnNode.INSN:
				int s;
				switch (n.getOpcode()) {
				// case ATHROW: // 1 before n/a after
				// popValue();
				// onMethodExit(opcode);
				// break;
				//
				// case LRETURN: // 2 before n/a after
				// case DRETURN: // 2 before n/a after
				// popValue();
				// popValue();
				// onMethodExit(opcode);
				// break;

				case NOP:
				case LNEG:
				case DNEG:
				case FNEG:
				case INEG:
				case L2D:
				case D2L:
				case F2I:
				case I2B:
				case I2C:
				case I2S:
				case I2F:
				case F2L: // 1 before 2 after
				case F2D:
				case I2L:
				case I2D:

				case L2I: // 2 before 1 after
				case L2F: // 2 before 1 after
				case D2I: // 2 before 1 after
				case D2F: // 2 before 1 after
				case ARRAYLENGTH:
				case SWAP:
					nToSkip--;
					nToSkip++;
					break;

				case IADD:
				case FADD:
				case ISUB:
				case LSHL: // 3 before 2 after
				case LSHR: // 3 before 2 after
				case LUSHR: // 3 before 2 after
				case LSUB:
				case LMUL:
				case LDIV:
				case LREM:
				case LADD:
				case LAND:
				case LOR:
				case LXOR:
				case DADD:
				case DMUL:
				case DSUB:
				case DDIV:
				case DREM:

				case FSUB:
				case FMUL:
				case FDIV:
				case FREM:
				case FCMPL: // 2 before 1 after
				case FCMPG: // 2 before 1 after
				case IMUL:
				case IDIV:
				case IREM:
				case ISHL:
				case ISHR:
				case IUSHR:
				case IAND:
				case IOR:
				case IXOR:

				case IALOAD: // remove 2 add 1
				case FALOAD: // remove 2 add 1
				case AALOAD: // remove 2 add 1
				case BALOAD: // remove 2 add 1
				case CALOAD: // remove 2 add 1
				case SALOAD: // remove 2 add 1
				case LALOAD: // remove 2 add 2
				case DALOAD: // remove 2 add 2

				case LCMP: // 4 before 1 after
				case DCMPL:
				case DCMPG:
				case DUP:
				case DUP_X1:
				case DUP_X2:

				case DUP2: // is this wrong to assume that dup2 is only used on
							// longs and not 2 shorts?
				case DUP2_X1:
				case DUP2_X2:
					nToSkip--;
					nToSkip += 2;
					break;

				case ACONST_NULL:
				case ICONST_M1:
				case ICONST_0:
				case ICONST_1:
				case ICONST_2:
				case ICONST_3:
				case ICONST_4:
				case ICONST_5:
				case FCONST_0:
				case FCONST_1:
				case FCONST_2:
				case LCONST_0:
				case LCONST_1:
				case DCONST_0:
				case DCONST_1:
//				case POP:
//				case MONITORENTER:
//				case MONITOREXIT:
//				case POP2:
					if (nToSkip == 0) {
						return new ConstantInsnAdapter(n);
					}
					nToSkip--;
					break;

				case LASTORE:
				case DASTORE:
				case IASTORE:
				case FASTORE:
				case AASTORE:
				case BASTORE:
				case CASTORE:
				case SASTORE:
					nToSkip--;
					nToSkip += 3;
					break;
				}
				break;
			}

		}
		return null;
	}

	private MethodInstance findOrAddMethod(String owner, String name, String desc, int access) {
		String lookupKey = owner + "." + name + ":" + desc;
		if (!lookupCache.containsKey(lookupKey))
			lookupCache.put(lookupKey, new MethodInstance(name, desc, owner, access));
		return lookupCache.get(lookupKey);
	}

	private MethodInstance findOrAddMethod(String owner, MethodNode mn) {
		return findOrAddMethod(owner, mn.name, mn.desc, mn.access);
	}

	public static void main(String[] args) {
		try {
			ClassReader cr = new ClassReader("edu.columbia.cs.psl.invivo.sample.SimpleClass");
			MutabilityAnalyzer ma = new MutabilityAnalyzer();
			ma.Analyze(cr);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
