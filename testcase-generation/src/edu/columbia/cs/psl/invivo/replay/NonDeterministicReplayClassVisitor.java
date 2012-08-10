package edu.columbia.cs.psl.invivo.replay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.log4j.Logger;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AnalyzerAdapter;
import org.objectweb.asm.commons.JSRInlinerAdapter;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.util.CheckMethodAdapter;

import edu.columbia.cs.psl.invivo.record.Constants;
import edu.columbia.cs.psl.invivo.record.MethodCall;
import edu.columbia.cs.psl.invivo.record.visitor.CloningAdviceAdapter;

public class NonDeterministicReplayClassVisitor extends ClassVisitor implements Opcodes{

	private String className;
	private boolean isAClass = true;
	
	public NonDeterministicReplayClassVisitor(int api, ClassVisitor cv) {
		super(api, cv);

	}
	private static Logger logger = Logger.getLogger(NonDeterministicReplayClassVisitor.class);
	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		super.visit(version, access, name, signature, superName, interfaces);
		this.className = name;
		
		logger.debug("Visiting " + name+ " for instrumentation");
		if((access & Opcodes.ACC_INTERFACE) != 0)
			isAClass = false;
	}
	private boolean isFirstConstructor = true;
	@Override
	public MethodVisitor visitMethod(int acc, String name, String desc,
			String signature, String[] exceptions) {
		//TODO need an annotation to disable doing this to some apps
		if(isAClass)// && className.startsWith("edu"))
		{
			
			MethodVisitor smv = cv.visitMethod(acc, name, desc, signature, exceptions);
			JSRInlinerAdapter mv = new JSRInlinerAdapter(smv, acc, name, desc, signature, exceptions);

			AnalyzerAdapter analyzer = new AnalyzerAdapter(className, acc, name, desc, mv);
			CheckMethodAdapter cm = new CheckMethodAdapter(analyzer);
			NonDeterministicReplayMethodVisitor cloningMV = new NonDeterministicReplayMethodVisitor(Opcodes.ASM4, cm, acc, name, desc,className,isFirstConstructor, analyzer);
			if(name.equals("<init>"))
				isFirstConstructor = false;
			cloningMV.setClassVisitor(this);
			return cloningMV;
		}
		else
			return 	cv.visitMethod(acc, name, desc, signature,
					exceptions);
	}

	public HashSet<MethodCall> getLoggedMethodCalls() {
		return loggedMethodCalls;
	}
	private HashSet<MethodCall> loggedMethodCalls = new HashSet<MethodCall>();
	private HashMap<String, MethodInsnNode> captureMethodsToGenerate = new HashMap<String, MethodInsnNode>();
	public void addFieldMarkup(ArrayList<MethodCall> calls) {
		logger.debug("Received field markup from method visitor (" + calls.size() + ")");
		loggedMethodCalls.addAll(calls);
		//TODO also setup the new method to retrieve the list of replacements for the method
	}

	@Override
	public void visitEnd() {
		super.visitEnd();

	}
	public String getClassName() {
		return className;
	}

	public void addCaptureMethodsToGenerate(HashMap<String, MethodInsnNode> captureMethodsToGenerate) {
		this.captureMethodsToGenerate.putAll(captureMethodsToGenerate);
	}
}
