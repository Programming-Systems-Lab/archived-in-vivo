package edu.columbia.cs.psl.invivo.nativedetector;

import java.io.IOException;
import java.util.LinkedList;

import org.apache.log4j.Logger;

// TODO delete class
@SuppressWarnings("javadoc")
public class InvocationFindingTester {

	private static Logger logger = Logger.getLogger(InvocationFindingTester.class);
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		NativeDetector nd = new NativeDetector("/Users/miriammelnick/Desktop/research/classes.jar");
		
		nd.getAllClasses();
		logger.info("getAllClasses done");
		nd.logStats();
		
		nd.getAllMethods();
		logger.info("getAllMethods done");
		nd.logStats();
		
		nd.findAllInvokers();
		logger.info("findAllInvokers done");
		
		MethodInstance dummyPL = new MethodInstance("println", "(Ljava/lang/String;)V", "java/io/PrintStream");
		int index = nd.allMethods.indexOf(dummyPL);
		logger.info("println can be found at index "+ index);
		MethodInstance truePL = nd.allMethods.get(index);
		logger.info(truePL.getCallers().size() + " callers");
		LinkedList<MethodInstance> callers = truePL.getCallers();
		for (MethodInstance c : callers) {
			logger.info(c.getClazz()+" "+c.getMethod());
		}
		nd.openMethods.add(truePL);
		logger.info(nd.openMethods.size());
		assert(nd.openMethods.size()==1);
		nd.logStats();
		
		/*
		 * TODO FIXME
		 * bugs I see
		 * 	1) method is appearing in openMethods but not moving to closedMethods
		 * 2) the methods that call it are not being added to openMethods
		 * 3) npe after many methods are processed
		 */
		
		nd.findNativeInvokers();
		logger.info("findNativeInvokers done");
		nd.logStats();
		
		nd.writeClosedMethods("test.txt");

	}

}
