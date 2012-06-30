package edu.columbia.cs.psl.invivo.nativedetector;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;

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
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		NativeDetector engine = new NativeDetector("/System/Library/Java/JavaVirtualMachines/1.6.0.jdk/Contents/Classes/classes.jar");

		engine.getAllClasses();
		engine.getAllMethods();
		
		for (MethodInstance mi: NativeDetector.allMethods) {
			engine.addLinksToChildren(mi);
		}
		
		logger.info("dM: " + engine.dirtyMap.size() + "; uM: " + engine.unprocessedMap.size() + "; q: " + engine.dirtyQueue.size());
		
		engine.makeQueue();
		engine.processQueue();
		logger.info("writing to file");
		
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter("/Users/miriam/git/in-vivo/native-detector/dirtymethods.txt"));
			Iterator<String> it = engine.dirtyMap.keySet().iterator();
			int numDirties = engine.dirtyMap.keySet().size();
			int count = 0;
			while (it.hasNext()) {
				bw.write(it.next());
				bw.newLine();
				count++;
				logger.info(count + " of " + numDirties);
			}
			bw.close();
		} catch (Exception e) {
			logger.error("write out failure");
		}

		logger.info("done with test.");

		//logger.info(engine.dirtyMap.keySet());
		
//		try {
//			logger.info("calling getAllClasses()");
//			engine.getAllClasses(); // populates allClasses
//			logger.info("allM: " + engine.getAllMethodsSize() + ", allML: " + engine.getAllMethodLookupSize()+ ", open: " + engine.openMethods.size() + ", closed: " +engine.closedMethods.size());
////
//			logger.info("calling getAllMethods()");
//			engine.getAllMethods();	// populates allMethods and allMethodsLookup
//			logger.info("allM: " + engine.getAllMethodsSize() + ", allML: " + engine.getAllMethodLookupSize()+ ", open: " + engine.openMethods.size() + ", closed: " +engine.closedMethods.size());
//
//			int a = NativeDetector.allMethods.get(100).calls.size();
//			int b = NativeDetector.allMethods.get(500).calls.size();
//			int c = NativeDetector.allMethods.get(1000).calls.size();
////			logger.info("a couple spot checks: " + a + "  "+ b + "  " +c);
////			
//			logger.info("calling findAllInvokersIt()");
//			
//			engine.findAllInvokersIt();
//			logger.info(NativeDetector.numMethodCalls + " total method calls");
//			//engine.findAllInvokers();
////			logger.info("allM: " + engine.getAllMethodsSize() + ", allML: " + engine.getAllMethodLookupSize()+ ", open: " + engine.openMethods.size() + ", closed: " +engine.closedMethods.size());
////			
////			
//			int d = NativeDetector.allMethods.get(100).calls.size();
//			int e = NativeDetector.allMethods.get(500).calls.size();
//			int f = NativeDetector.allMethods.get(1000).calls.size();
//			logger.info("a couple spot checks: " + a + "  "+ b + "  " +c);
//			logger.info("a couple spot checks: " + d + "  "+ e + "  " +f);
//
//			
////			engine.selectNativeMethods(); //populates openMethods
////			engine.logStats();
////			
////			engine.findNativeInvokers();
////			engine.logStats();
////			engine.writeClosedMethods("nativeInvokers.txt");
////		
////			logger.info("I think I'm done with NativeDetector");
////			
//		} catch (IOException ioe) {
//			ioe.printStackTrace();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		return;
	}
	
}
