package edu.columbia.cs.psl.invivo.nativedetector;

public class InvocationTester {
	static String jarPath = "/Users/miriammelnick/Desktop/research/classes.jar";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		InvocationFinder ifinder = new InvocationFinder();
		ifinder.runAndWrite(jarPath, "java/io/PrintStream", "void println(String)", "invocationsOfprintln", false, false);
		System.out.println("done with InvocationTester.main");
	}

}
