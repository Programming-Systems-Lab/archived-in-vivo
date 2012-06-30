package edu.columbia.cs.psl.invivo.nativedetector;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Set;

import org.apache.log4j.Logger;
import org.objectweb.asm.Opcodes;

public class SimpleTest {

	private static Logger logger = Logger.getLogger(SimpleTest.class);
	
	public static void main(String[] args) {
		smallTest();
//		writeTest();
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
	private static Set<String> smallTest() {
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
		
		nd.methodMap.put(a.getFullName(), a);
		nd.methodMap.put(bb.getFullName(), bb);
		nd.methodMap.put(ccc.getFullName(), ccc);
		
		nd.methodMap.get(a.getFullName()).functionsICall.add(bb.getFullName());
		nd.methodMap.get(bb.getFullName()).functionsICall.add(ccc.getFullName());
		
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
}
