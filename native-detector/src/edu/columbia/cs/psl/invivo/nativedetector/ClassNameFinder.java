package edu.columbia.cs.psl.invivo.nativedetector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ClassNameFinder {
	static String apiHome = "/Users/miriammelnick/Desktop/research/classes/java/io";
	static ArrayList<String> pathNames = new ArrayList<String>();
	static ArrayList<String> classNames = new ArrayList<String>();
	static ArrayList<String> properClassNames = new ArrayList<String>();
	
	 // TODO reimplement with JAR load
	public static void main(String[] args) throws IOException {
		ClassNameFinder.pathNames.add(apiHome);
		
		while (ClassNameFinder.pathNames.size() > 0) {
			ClassNameFinder.parseDirectory(ClassNameFinder.pathNames.get(0)); //pop the first item off the arraylist
			ClassNameFinder.pathNames.remove(0);					// finish that pop
		}

		ClassNameFinder.formatClasses();
		System.out.println("All classes parsed. " + ClassNameFinder.classNames.size() + " classes found.");
	}
	
	public static void parseDirectory(String path) {
		if (!path.contains("META-INF")) {
			File dir = new File(path);
			if (dir.isDirectory()) {
				String[] dirContents = dir.list();
				for (String sub: dirContents) {
					File child = new File(path +"/"+ sub);
					if (child.isDirectory())
						pathNames.add(child.getAbsolutePath());
					else
						classNames.add(child.getAbsolutePath());
				}
			}
		}
		
		return;
	}
	
	public static void formatClasses() {
		for (String clazz: classNames) {
			String className = clazz.substring(46);		
			if (className.endsWith(".class") && !className.contains("$")) {		// TODO do I sometimes need to keep these $ classes?
				className = className.substring(0,className.length()-6);	     //delete ".class"
			
		//		if (className.contains("$"))
		//			className = className.substring(0,className.indexOf("$"));
				if (className.endsWith(".prop"))
					className = className.substring(0,className.lastIndexOf("."));
				
			//	className = className.replace('/', '.');

				//System.out.println(className);
				properClassNames.add(className);
			}
		}
	}
	
}
