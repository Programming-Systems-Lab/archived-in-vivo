package mrm.nativedetector;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.log4j.Logger;


public class Engine {

	private String jarPath;
	private static Logger logger = Logger.getLogger(Engine.class);

	public Engine(String jarURL) {
		jarPath = jarURL;
	}
	
	public ArrayList<String> getClassNames() throws IOException {
		ArrayList<String> classList = new ArrayList<String>();
		JarFile classJar = new JarFile(jarPath);
		Enumeration<JarEntry> jarContents = classJar.entries();
		while (jarContents.hasMoreElements())
			classList.add(jarContents.nextElement().getName());				
		classJar.close();
		
		ArrayList<String> allClasses = cleanClasses(classList);
		logger.info("allClasses.size() = " + allClasses.size());
		return allClasses;
	}
	
	
	// gets rid of things that don't end in .class
	private ArrayList<String> cleanClasses(ArrayList<String> dirtyClasses) {
		int dirtyClassesSize = dirtyClasses.size();
		ArrayList<String> cleanClasses = new ArrayList<String>();
		for (int i=0; i < dirtyClassesSize; i++) {
			String className = dirtyClasses.get(i);
			if (className.endsWith(".class")) {
				className = className.substring(0, className.length()-6);   //remove ".class"
				cleanClasses.add(className);
			}
		}
		return cleanClasses;
		
	}
	
	// counts methods inside a list of classes
	public int countMethodsInClasses(ArrayList<String> classList) {
		int classCount = classList.size();
		for (int i = 0; i < classCount; i++) {
			String className = classList.get(i);
			
		}
		
		return 0;
	}
	
	
	//open jar file
	
	//make a class visitor.
		// assign each class to an index
	
	//make a method visitor
		// assign each method to an index
	
	//make a map of all methods called by A (indices only)
	
	
	
}
