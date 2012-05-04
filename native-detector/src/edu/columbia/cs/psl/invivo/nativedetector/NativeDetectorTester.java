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
		NativeDetector engine = new NativeDetector("/Users/miriammelnick/Desktop/research/classes.jar");
		try {
			engine.getAllClasses(); // populates openClasses (21087)
			engine.logStats();

			engine.findAllInvokers();
			engine.logStats();
			
//			engine.getAllMethods();	// populates allMethods (179891)
//			engine.logStats();

			
			engine.selectNativeMethods();
			engine.logStats();
			
			engine.findNativeInvokers();
			engine.logStats();
			engine.writeClosedMethods("nativeInvokers.txt");
		
			logger.info("I think I'm done with NativeDetector");
			
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}
	
}
