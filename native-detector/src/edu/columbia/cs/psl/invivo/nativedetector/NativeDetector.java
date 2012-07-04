package edu.columbia.cs.psl.invivo.nativedetector;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
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

	 BufferedWriter bw;
	 
	 
	/**
	 * Logger for NativeDetector class (log4j).
	 * private static Logger logger
	 * @see Logger
	 */
	private static Logger logger = Logger.getLogger(NativeDetector.class);
	
	/**
	 * Constructor for a NativeDetector object
	 * @param jarURL			String				absolute path to classes.jar (java std lib jar)
	 * @see NativeDetector#allClasses
	 */
	public NativeDetector(String jarURL) {
		jarPath = jarURL;
	}

	HashMap<String, LinkedList<String>> dirtyMap = new HashMap<String, LinkedList<String>>(); // methodname, list of callers
	HashMap<String, LinkedList<String>> unprocessedMap = new HashMap<String, LinkedList<String>>(); // methodname, list of callers
	LinkedList<Pair<String, LinkedList<String>>> dirtyQueue = new LinkedList<Pair<String,LinkedList<String>>>();
	static HashMap<String, MethodInstance> methodMap = new HashMap<String, MethodInstance>(); // methodname, methodinstance object
		
	
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
		logger.info("allClasses.size() = " + allClasses.size());
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
//		logger.info(miName);
//		logger.warn(miName + " " + mi.getAccess());
		if (mi.isNative()) {
//			logger.info("inside if");
//			logger.info(dirtyMap);
			dirtyMap.put(miName, new LinkedList<String>());
//			logger.info(dirtyMap);
		} else {
//			logger.info("inside else");
			MethodInstance real = methodMap.get(miName);
//			logger.info(real);
//			logger.info(real.functionsICall);
//			int count = 0;
			for (String f : real.functionsICall) {
//				count++;
//				logger.info("inside for loop: "+ count);
				if (unprocessedMap.containsKey(f)) {
//					logger.info("inside nested if");
//					logger.info("before: " +unprocessedMap.get(f).size());
					
					unprocessedMap.get(f).add(miName);
//					logger.info("after: " +unprocessedMap.get(f).size());

//					logger.info(unprocessedMap);
				} else {
//					logger.info("inside nested for");
//					logger.info(unprocessedMap);
//					logger.info("before: " +unprocessedMap.size());

					addPairToUnprocessedMap(f, miName);
//					logger.info("after: " +unprocessedMap.size());

//					logger.info(unprocessedMap);
					
				}
//				logger.info("done with iteration "+count);
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
		try {
			logger.info("writing");
			bw.write(f + "\t\t" + caller);
			bw.newLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	/* (2) for each x in dirty hash
	 * 		add to queue
	 */
	public void makeQueue() {
		Set<Entry<String, LinkedList<String>>> dirtySet = dirtyMap.entrySet();
		Iterator<Entry<String, LinkedList<String>>> dirtySetIt = dirtySet.iterator();
		while (dirtySetIt.hasNext()) {
			Entry<String, LinkedList<String>> dirtyItem = dirtySetIt.next();
			Pair<String, LinkedList<String>> dirtyPair = new Pair<String, LinkedList<String>>(dirtyItem.getKey(), dirtyItem.getValue());
			addToQueue(dirtyItem.getKey());
//			try {
//				bw.write(dirtyPair.fst + "\t\t" + dirtyPair.snd);
//				bw.newLine();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
		logger.info("queue size: " + dirtyQueue.size());
	}
	
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

				if (!unprocessedMap.containsKey(dirtyName)) {
					unprocessedMap.put(dirtyName, dirtyItem.snd);
				}
			
				LinkedList<String> callers = unprocessedMap.get(dirtyName); //mark these dirty
				logger.info(callers.size());
				for (String y: callers) {
					if (dirtyMap.containsKey(y)) {
						dirtyMap.get(y).add(dirtyName);
					} else {
						addPairToDirtyMap(y, dirtyName);
					}
				}
				
//				LinkedList<String> functionsICall = methodMap.get(dirtyName).functionsICall;
//				logger.info(functionsICall.size() + " functions I call");
//				for (String y: functionsICall) {
//					if (dirtyMap.containsKey(y)) {
//						logger.warn("dirtyMap contained "+ y);
//						dirtyMap.get(y).add(dirtyName);
//					} else {
//						logger.warn("dirtyMap did not contain "+ y);
//						addPairToDirtyMap(y, dirtyName);
//					}
//					addToQueue(y);
//					try {
//						logger.info("writing");
//						bw.write(y + " is dirty");
//						bw.newLine();
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
				logger.info("queue size: " + dirtyQueue.size());

		
		}
		
	}
	
		
	private void addToQueue(String f) {
		assert(dirtyMap.containsKey(f));
		Pair<String, LinkedList<String>> pair = new Pair<String, LinkedList<String>>(f, dirtyMap.get(f));
		dirtyQueue.push(pair);
		logger.info("queue size: " + dirtyQueue.size());
	}
	

	public void getAllMethods() throws IOException {
		int numClasses = allClasses.size();
		String[] listOfClasses = allClasses.toArray(new String[numClasses]);
		for (int i=0; i < numClasses; i++) { 
			String className = listOfClasses[i];
			ClassReader cr = new ClassReader(className);
			NDClassVisitor ccv = new NDClassVisitor(Opcodes.ASM4, null, className);
			cr.accept(ccv, 0);
			//allMethods.addAll(ccv.allMethods);
			//logger.info("methods: " + allMethods.size());
			logger.info("methods: " + methodMap.size());
		}
	}
}
