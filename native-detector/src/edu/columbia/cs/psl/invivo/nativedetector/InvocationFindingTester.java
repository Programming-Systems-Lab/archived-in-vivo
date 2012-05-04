package edu.columbia.cs.psl.invivo.nativedetector;

import java.io.IOException;

// TODO delete class
@SuppressWarnings("javadoc")
public class InvocationFindingTester {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		NativeDetector nd = new NativeDetector("/Users/miriammelnick/Desktop/research/classes.jar");
		nd.getAllClasses();
		nd.getAllMethods();
		nd.openMethods.add(new MethodInstance("println", "(Ljava/lang/String;)V", "System.out"));
		nd.logStats();
		nd.findNativeInvokers();
		nd.writeClosedMethods("test.txt");

	}

}
