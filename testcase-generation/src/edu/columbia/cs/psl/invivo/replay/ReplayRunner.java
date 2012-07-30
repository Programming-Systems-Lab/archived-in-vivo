package edu.columbia.cs.psl.invivo.replay;

import java.lang.reflect.InvocationTargetException;

public class ReplayRunner extends ClassLoader {
	public static void run(byte[] code, String className) {		
		ReplayRunner runner = new ReplayRunner();
		Path path = Paths.get("/home/nikhil/in-vivo/testcase-generation/instrumented/edu/columbia/cs/psl/invivo/example/ReaderUserInvivoLog.class");
		try {
			Class<?> instrumentedClass = runner.defineClass("edu.columbia.cs.psl.invivo.example.ReaderUserInvivoLog",Files.readAllBytes(path), 0, Files.readAllBytes(path).length);
		} catch (ClassFormatError e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		Class<?> replayClass = runner.defineClass("edu.columbia.cs.psl.invivo.example.ReaderUser", code, 0, code.length);
		
		try {
			replayClass.getMethods()[0].invoke(null, new Object[] { null });
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
