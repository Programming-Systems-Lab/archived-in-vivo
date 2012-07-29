package edu.columbia.cs.psl.invivo.replay;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

public class ReplayRunner extends ClassLoader {
	public static void run(byte[] code, String className) {		
		ReplayRunner runner = new ReplayRunner();
		/*Path path = Paths.get("/home/nikhil/in-vivo/testcase-generation/replayed/edu/columbia/cs/psl/invivo/example/ReaderUserInvivoLog.class");
System.out.println(className);
		try {
			Class<?> instrumentedClass = runner.defineClass(className+"InvivoLog",Files.readAllBytes(path), 0, Files.readAllBytes(path).length);
		} catch (ClassFormatError e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/
		Class<?> replayClass = runner.defineClass(className, code, 0, code.length);
		
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
