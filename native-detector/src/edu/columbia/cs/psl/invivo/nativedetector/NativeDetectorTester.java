package edu.columbia.cs.psl.invivo.nativedetector;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

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
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		NativeDetector engine = new NativeDetector("/System/Library/Java/JavaVirtualMachines/1.6.0.jdk/Contents/Classes/classes.jar");

		engine.getAllClasses();
		engine.getAllMethods();
		engine.bw = new BufferedWriter(new FileWriter("/Users/miriam/git/in-vivo/native-detector/nd-output.txt"));
		for (MethodInstance mi: NativeDetector.methodMap.values()) {
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
			engine.bw.close();
		} catch (Exception e) {
			logger.error("write out failure");
		}
		logger.info("done with test.");
		engine.bw.close();

		return;
	}
	
}
