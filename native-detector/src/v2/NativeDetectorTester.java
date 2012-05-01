package v2;

import java.io.IOException;
import java.util.HashSet;

import org.apache.log4j.Logger;

public class NativeDetectorTester {
	
	private static Logger logger = Logger.getLogger(NativeDetectorTester.class);
	
	public static void main(String[] args) {
		NativeDetector engine = new NativeDetector("/Users/miriammelnick/Desktop/research/classes.jar");
		try {
		//	HashSet<Method> nativeInvokingMethods = findInvocationsOf(findNativeMethods(getAllClasses()));
			engine.getAllClasses();
		//	HashSet<String> allClasses = engine.getAllClasses();
			engine.findNativesFromClasses();
		//	engine.findNativeMethods(allClasses);
			logger.info("done with findNativeMethods");
			logger.info(engine.openMethodsWithClasses.size() + " open methods, \t" + engine.closedMethodsWithClasses.size() + " closed methods");
		//	HashSet<Method> nativeInvokingMethods = findInvocationsOf(nativeMethods);
		//	writeMethodsToFile(nativeInvokingMethods, new File("nativeInvokingMethods.txt"));
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}
}
