package edu.columbia.cs.psl.invivo.record.visitor;

import java.util.ArrayList;
import java.util.HashSet;

import org.apache.log4j.Logger;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.JSRInlinerAdapter;

import edu.columbia.cs.psl.invivo.record.MethodCall;

public class NonDeterministicLoggingClassVisitor extends ClassVisitor implements Opcodes{

	private String className;
	private boolean isAClass = true;
	
	public NonDeterministicLoggingClassVisitor(int api, ClassVisitor cv) {
		super(api, cv);

	}
	private static Logger logger = Logger.getLogger(NonDeterministicLoggingClassVisitor.class);
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
			MethodVisitor smv = cv.visitMethod(acc, name, desc, signature,
					exceptions);
			JSRInlinerAdapter  mv = new JSRInlinerAdapter(smv, acc, name, desc, signature, exceptions);

//			CheckMethodAdapter cmv = new CheckMethodAdapter(mv);
			NonDeterministicLoggingMethodVisitor cloningMV = new NonDeterministicLoggingMethodVisitor(Opcodes.ASM4, mv, acc, name, desc,className,isFirstConstructor);
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
	public void addFieldMarkup(ArrayList<MethodCall> calls) {
		logger.debug("Received field markup from method visitor (" + calls.size() + ")");
		loggedMethodCalls.addAll(calls);
		//TODO also setup the new method to retrieve the list of replacements for the method
	}
}
