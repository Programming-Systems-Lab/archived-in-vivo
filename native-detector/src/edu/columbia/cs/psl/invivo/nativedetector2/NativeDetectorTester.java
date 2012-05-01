package edu.columbia.cs.psl.invivo.nativedetector2;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.objectweb.asm.commons.Method;

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
			while (!engine.openMethodsWithClasses.isEmpty()) {
				engine.findNativesFromMethod();
				logger.info(engine.openMethodsWithClasses.size() + " open methods, \t" + engine.closedMethodsWithClasses.size() + " closed methods");
				saveClosedMethods(engine, "nativeMethodOutput.txt");
			}
		//	HashSet<Method> nativeInvokingMethods = findInvocationsOf(nativeMethods);
			logger.info("I think I'm done with NativeDetector");
			
		//	writeMethodsToFile(nativeInvokingMethods, new File("nativeInvokingMethods.txt"));
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}
	
	public static void saveClosedMethods(NativeDetector engine, String fileName) throws FileNotFoundException {
		PrintWriter pw = new PrintWriter(fileName);
		Iterator<Entry<Method, String>> it = engine.closedMethodsWithClasses.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Method,String> entry = it.next();
			pw.write(entry.getValue() + "\t" + entry.getKey()+"\n");
		}
		pw.close();
		//open a new printwriter to that file
		//for each item in closedMethodsWithClasses
			// write that item to a new line
		//close all writers
	}
	
	public static void saveOpenMethods(NativeDetector engine, String fileName) throws FileNotFoundException {
		PrintWriter pw = new PrintWriter(fileName);
		Iterator<Entry<Method, String>> it = engine.openMethodsWithClasses.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Method,String> entry = it.next();
			pw.write(entry.getValue() + "\t" + entry.getKey()+"\n");
		}
		pw.close();
		//open a new printwriter to that file
		//for each item in closedMethodsWithClasses
			// write that item to a new line
		//close all writers
	}
	
}
