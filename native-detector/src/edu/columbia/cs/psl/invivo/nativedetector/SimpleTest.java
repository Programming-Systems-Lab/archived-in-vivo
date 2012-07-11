package edu.columbia.cs.psl.invivo.nativedetector;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Set;

import org.apache.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;

public class SimpleTest {

	private static Logger logger = Logger.getLogger(SimpleTest.class);
	
	public static void main(String[] args) {
		try {
			smallTest();
			writeTest();
			preprocessingTest("java/awt/Container.getComponent:(I)Ljava/awt/Component;");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("done with tests");
	}
		
	
	private static void writeTest() {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter("/Users/miriam/git/in-vivo/native-detector/dirtymethods.txt"));
			
			bw.write("this is a test");
			bw.newLine();
			bw.write("and here is another");
			bw.close();
			logger.info("Done with writeTest");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}
	private static Set<String> smallTest() throws IOException {
		NativeDetector nd = new NativeDetector("/System/Library/Java/JavaVirtualMachines/1.6.0.jdk/Contents/Classes/classes.jar");
		/*
		 * a
		 * 	bb
		 * 		ccc 
		 */
		
		nd.bw = new BufferedWriter(new FileWriter("/Users/miriam/git/in-vivo/native-detector/ndoutput.txt"));
		MethodInstance a = new MethodInstance("methodA", "descA", "classA");
		a.setAccess(Opcodes.ACC_NATIVE);
		LinkedList<String> callsBB = new LinkedList<String>();
		callsBB.add(a.getFullName());
		
		MethodInstance bb = new MethodInstance("methodB", "descB", "classB");
		
		LinkedList<String> callsCCC = new LinkedList<String>();
		callsCCC.add(bb.getFullName());
		
		MethodInstance ccc = new MethodInstance("methodC", "descC", "classC");
		
		NativeDetector.methodMap.put(a.getFullName(), a);
		NativeDetector.methodMap.put(bb.getFullName(), bb);
		NativeDetector.methodMap.put(ccc.getFullName(), ccc);
		
		NativeDetector.methodMap.get(a.getFullName()).functionsICall.add(bb.getFullName());
		NativeDetector.methodMap.get(bb.getFullName()).functionsICall.add(ccc.getFullName());
		
		nd.unprocessedMap.put(bb.getFullName(), callsBB);
		logger.info("dM: " + nd.dirtyMap.size() + "; uM: " + nd.unprocessedMap.size() + "; q: " + nd.dirtyQueue.size() + "; uM=" + nd.unprocessedMap);
		logger.info("dM=" + nd.dirtyMap);
		
		nd.unprocessedMap.put(ccc.getFullName(), callsCCC);
		logger.info("dM: " + nd.dirtyMap.size() + "; uM: " + nd.unprocessedMap.size() + "; q: " + nd.dirtyQueue.size() + "; uM=" + nd.unprocessedMap);
		logger.info("dM=" + nd.dirtyMap);

		nd.addLinksToChildren(a);
		logger.info("dM: " + nd.dirtyMap.size() + "; uM: " + nd.unprocessedMap.size() + "; q: " + nd.dirtyQueue.size() + "; uM=" + nd.unprocessedMap);
		logger.info("dM=" + nd.dirtyMap);

		nd.makeQueue();
		logger.info("dM: " + nd.dirtyMap.size() + "; uM: " + nd.unprocessedMap.size() + "; q: " + nd.dirtyQueue.size() + "; uM=" + nd.unprocessedMap);
		logger.info("dM=" + nd.dirtyMap + "; q=" + nd.dirtyQueue);
		
		nd.processQueue();
		logger.info("dM: " + nd.dirtyMap.size() + "; uM: " + nd.unprocessedMap.size() + "; q: " + nd.dirtyQueue.size() + "; uM=" + nd.unprocessedMap);
		logger.info("dM=" + nd.dirtyMap + "; q=" + nd.dirtyQueue);
		
		logger.info("done with test.");
		
		
		logger.info(nd.dirtyMap.keySet());

		return nd.dirtyMap.keySet();
	}
	
	private static void preprocessingTest(String methodName) throws IOException {
		
		
		NativeDetector nd = new NativeDetector("/System/Library/Java/JavaVirtualMachines/1.6.0.jdk/Contents/Classes/classes.jar");

		MethodInstance mi = new MethodInstance(methodName);
		
		String className = methodName.split("\\.")[0];
		NDClassVisitor cv = new NDClassVisitor(Opcodes.ASM4, null, className);
		ClassReader cr = new ClassReader(className);
		cr.accept(cv, 0);
		logger.info(mi.functionsICall);
		nd.addLinksToChildren(mi);
		logger.info(mi.functionsICall);
		//nd.getAllClasses();
		//nd.getAllMethods();

		
	}
}