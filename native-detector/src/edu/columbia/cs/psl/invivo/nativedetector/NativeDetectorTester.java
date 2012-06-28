package edu.columbia.cs.psl.invivo.nativedetector;

import java.io.IOException;

import org.apache.log4j.Logger;

/**
 * @author miriammelnick
 * Main test method for NativeDetector code.
 * Run this.
 */
public class NativeDetectorTester {
	
	/**
	 * A logger for NativeDetectorTester (log4j)
	 * private static Logger logger
	 * @see Logger
	 */
	private static Logger logger = Logger.getLogger(NativeDetectorTester.class);
	
	/**
	 * Main testing function. Requires no command line arguments.
	 * @param args			String[]
	 */
	public static void main(String[] args) {
		NativeDetector engine = new NativeDetector("/System/Library/Java/JavaVirtualMachines/1.6.0.jdk/Contents/Classes/classes.jar");
		try {
			logger.info("calling getAllClasses()");
			engine.getAllClasses(); // populates allClasses
			logger.info("allM: " + engine.getAllMethodsSize() + ", allML: " + engine.getAllMethodLookupSize()+ ", open: " + engine.openMethods.size() + ", closed: " +engine.closedMethods.size());
//
			logger.info("calling getAllMethods()");
			engine.getAllMethods();	// populates allMethods and allMethodsLookup
			logger.info("allM: " + engine.getAllMethodsSize() + ", allML: " + engine.getAllMethodLookupSize()+ ", open: " + engine.openMethods.size() + ", closed: " +engine.closedMethods.size());

			int a = NativeDetector.allMethods.get(100).calls.size();
			int b = NativeDetector.allMethods.get(500).calls.size();
			int c = NativeDetector.allMethods.get(1000).calls.size();
//			logger.info("a couple spot checks: " + a + "  "+ b + "  " +c);
//			
			logger.info("calling findAllInvokersIt()");
			
			engine.findAllInvokersIt();
			logger.info(NativeDetector.numMethodCalls + " total method calls");
			//engine.findAllInvokers();
//			logger.info("allM: " + engine.getAllMethodsSize() + ", allML: " + engine.getAllMethodLookupSize()+ ", open: " + engine.openMethods.size() + ", closed: " +engine.closedMethods.size());
//			
//			
			int d = NativeDetector.allMethods.get(100).calls.size();
			int e = NativeDetector.allMethods.get(500).calls.size();
			int f = NativeDetector.allMethods.get(1000).calls.size();
			logger.info("a couple spot checks: " + a + "  "+ b + "  " +c);
			logger.info("a couple spot checks: " + d + "  "+ e + "  " +f);

			
//			engine.selectNativeMethods(); //populates openMethods
//			engine.logStats();
//			
//			engine.findNativeInvokers();
//			engine.logStats();
//			engine.writeClosedMethods("nativeInvokers.txt");
//		
//			logger.info("I think I'm done with NativeDetector");
//			
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}
	
}
