package edu.columbia.cs.psl.invivo.nativedetector;


import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.objectweb.asm.Opcodes;

public class SimpleTest {

	private static Logger logger = Logger.getLogger(SimpleTest.class);
	
	public static void main(String[] args) {
		NativeDetector nd = new NativeDetector("/System/Library/Java/JavaVirtualMachines/1.6.0.jdk/Contents/Classes/classes.jar");
		/*
		 * a
		 * 	bb
		 * 		ccc 
		 */
		
		MethodInstance a = new MethodInstance("methodA", "descA", "classA");
		a.setAccess(Opcodes.ACC_NATIVE);
		LinkedList<String> callsBB = new LinkedList<String>();
		callsBB.add(a.getFullName());
		
		MethodInstance bb = new MethodInstance("methodB", "descB", "classB");
		
		LinkedList<String> callsCCC = new LinkedList<String>();
		callsCCC.add(bb.getFullName());
		
		MethodInstance ccc = new MethodInstance("methodC", "descC", "classC");
		
		
		a.functionsICall.add(bb.getFullName());
		bb.functionsICall.add(ccc.getFullName());
		
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
		
	}
}
