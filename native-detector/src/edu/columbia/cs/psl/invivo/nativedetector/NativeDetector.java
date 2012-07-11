package edu.columbia.cs.psl.invivo.nativedetector;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;

import com.sun.tools.javac.util.Pair;


/**
 * @author miriammelnick
 * Class to handle metadata and logic of native method detection. Input is a path to a jar containing
 * the Java Standard Library and output is a file containing all native methods and all methods that
 * invoke a native method.
 */
public class NativeDetector {

	
	/**
	 * Absolute location on disk of the jar file containing the Java Standard Library.
	 * String jarPath
	 */
	String jarPath;
	
	/**
	 * Set of all classes to be investigated.
	 * HashSet <String> openClasses
	 * @see NativeDetector#getAllClasses()
	 */
	 HashSet<String> allClasses = new HashSet<String>();
	
	/**
	 * Set of every method from every class. MethodInstances will be added through
	 * {@link NativeDetector#getAllMethods()}.
	 * static HashSet<MethodInstance> allMethods
	 * @see NativeDetector#getAllMethods()
	 * @see NativeDetector#findNativeInvokers()
	 */
	 static HashSet<MethodInstance> allMethods = new HashSet<MethodInstance>();
     static HashMap<String, MethodInstance> methodMap = new HashMap<String, MethodInstance>(); // methodname, methodinstance object

	 BufferedWriter bw;
	 HashMap<String, LinkedList<String>> dirtyMap = new HashMap<String, LinkedList<String>>(); // methodname, list of callers
	 HashMap<String, LinkedList<String>> unprocessedMap = new HashMap<String, LinkedList<String>>(); // methodname, list of callers
	 LinkedList<Pair<String, LinkedList<String>>> dirtyQueue = new LinkedList<Pair<String,LinkedList<String>>>();
	
	/**
	 * Constructor for a NativeDetector object
	 * @param jarURL			String				absolute path to classes.jar (java std lib jar)
	 * @see NativeDetector#allClasses
	 */
	public NativeDetector(String jarURL) {
		jarPath = jarURL;
	}

	
	/**
	 * Reads in the jar file and populates allClasses with class names.
	 * @see NativeDetector#jarPath
	 * @throws IOException			on jar file read failure
	 */
	public void getAllClasses() throws IOException {
		HashSet<String> classList = new HashSet<String>();
		JarFile classJar = new JarFile(jarPath);
		Enumeration<JarEntry> jarContents = classJar.entries();
		while (jarContents.hasMoreElements())
			classList.add(jarContents.nextElement().getName());				
		classJar.close();
		
		allClasses = cleanClasses(classList);
	}
	
	
	// gets rid of things that don't end in .class
	private HashSet<String> cleanClasses(HashSet<String> dirtyClasses) {
		int dirtyClassesSize = dirtyClasses.size();
		HashSet<String> cleanClasses = new HashSet<String>();
		String[] dirtyArray =  dirtyClasses.toArray(new String[dirtyClassesSize]);

		for (int i=0; i < dirtyClassesSize; i++) {
			String clazz = dirtyArray[i];
			if (clazz.endsWith(".class")) {
				clazz = clazz.substring(0, clazz.length()-6);   //remove ".class"
				cleanClasses.add(clazz);
			}
		}
		return cleanClasses;
	}
	
	

	
	/*
	 * (1) fetch a method x 
	 * 		if x is native
	 * 			add x to dirty hash
	 * 		else
	 * 			for each method y that x calls
	 * 				find/add y in called hash and add x to its list
	 */
	public void addLinksToChildren(MethodInstance mi) {
		String miName = mi.getFullName();
		if (mi.isNative()) {
			dirtyMap.put(miName, new LinkedList<String>());
		} else {
			MethodInstance real = methodMap.get(miName);
			for (String f : real.functionsICall) {
				if (unprocessedMap.containsKey(f)) {
					unprocessedMap.get(f).add(miName);
				} else {
					addPairToUnprocessedMap(f, miName);
				}
			}
		}
	}
	
	/* Takes fully qualified function names*/
	private void addPairToUnprocessedMap(String f, String caller) {
		LinkedList<String> list = new LinkedList<String>();
		list.add(caller);
		unprocessedMap.put(f, list);
	}

	/* Takes fully qualified function names*/
	private void addPairToDirtyMap(String f, String caller) {
		LinkedList<String> list = new LinkedList<String>();
		list.add(caller);
		dirtyMap.put(f, list);
	}
	
	/* (2) for each x in dirty hash
	 * 		add to queue
	 */
	public void makeQueue() {
		Set<Entry<String, LinkedList<String>>> dirtySet = dirtyMap.entrySet();
		Iterator<Entry<String, LinkedList<String>>> dirtySetIt = dirtySet.iterator();
		while (dirtySetIt.hasNext()) {
			addToQueue(dirtySetIt.next().getKey());
		}
	}
	
	private HashSet<String> processedMethods = new HashSet<String>();
	/* (3) for each x in queue
	 * 		[if in dirty, done]
	 * 		if x is not dirty
	 * 			for each y in x.list -- for each y that x calls
	 * 				add y to dirty with x in list
	 * 				queue y
	 * 
	 */
	public void processQueue() {
		while (dirtyQueue.size() != 0) {
			Pair<String, LinkedList<String>> dirtyItem = dirtyQueue.pop();
			String dirtyName = dirtyItem.fst;
			processedMethods.add(dirtyName);
			
			if (!unprocessedMap.containsKey(dirtyName)) {
				unprocessedMap.put(dirtyName, dirtyItem.snd);
			}
			LinkedList<String> callers = unprocessedMap.get(dirtyName); //mark these dirty
			for (String y: callers) {
				if (dirtyMap.containsKey(y)) {
					dirtyMap.get(y).add(dirtyName);
				} else {
					addPairToDirtyMap(y, dirtyName);
				}
				if(!processedMethods.contains(y))
				{
					processedMethods.add(y);
					addToQueue(y);
				}
			}
		}
	}
	
		
	private void addToQueue(String f) {
		assert(dirtyMap.containsKey(f));
		Pair<String, LinkedList<String>> pair = new Pair<String, LinkedList<String>>(f, dirtyMap.get(f));
		dirtyQueue.push(pair);
	}
	

	public void getAllMethods() throws IOException {
		int numClasses = allClasses.size();
		String[] listOfClasses = allClasses.toArray(new String[numClasses]);
		for (int i=0; i < numClasses; i++) { 
			String className = listOfClasses[i];
			ClassReader cr = new ClassReader(className);
			NDClassVisitor ccv = new NDClassVisitor(Opcodes.ASM4, null, className);
			cr.accept(ccv, 0);
		}
		for(String clazz : methodMap.keySet())
		{
			if(clazz.startsWith("java/io")
					|| clazz.startsWith("java/lang/Readable."))
			{
				MethodInstance mi = methodMap.get(clazz);
				mi.forceNative();
//				printWhatICall(mi, 0, new HashSet<String>(),mi.getFullName());
			}
		}
	}
	private static Logger logger = Logger.getLogger(NativeDetector.class);
	private void printWhatICall(MethodInstance mi, int level,HashSet<String> alreadyPrinted, String fallback)
	{
		String r = "";
		r += "|";
		for(int i = 0; i< level;i++)
			r += "--";
		if(mi == null){
			r+=("NULL!!!!!!!!!->" + fallback);
			logger.error(r);
			return;
			
		}

		logger.error(r + mi.getFullName() + (mi.isNative() ? " [N] " : "[]"));
		if(!alreadyPrinted.contains(mi.getFullName()))
		{
			alreadyPrinted.add(mi.getFullName());
			for(String s : mi.functionsICall)
				printWhatICall(methodMap.get(s), level+1,alreadyPrinted,s);
		}
	}
}
