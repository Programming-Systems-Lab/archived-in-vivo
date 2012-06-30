package edu.columbia.cs.psl.invivo.nativedetector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
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

//	@Deprecated
//	public static int numMethodCalls = 0;
//	
//	@Deprecated
//	static int loamCount = 0;
//	
//	
	
	
	/**
	 * Absolute location on disk of the jar file containing the Java Standard Library.
	 * String jarPath
	 */
	String jarPath;
	
	/**
	 * List of all classes to be investigated.
	 * LinkedList<String> openClasses
	 * @see NativeDetector#getAllClasses()
	 */
	 HashSet<String>	allClasses = new HashSet<String>();
	
	/**
	 * List of every method from every class. MethodInstances will be added through {@link NativeDetector#getAllMethods()} 
	 * and will be deleted through {@link NativeDetector#findNativeInvokers()}.
	 * private LinkedList<MethodInstance> allMethods
	 * @see NativeDetector#getAllMethods()
	 * @see NativeDetector#findNativeInvokers()
	 */
	 static HashSet<MethodInstance> allMethods = new HashSet<MethodInstance>();
	 //public static HashMap<String,MethodInstance> allMethodLookup = new HashMap<String,MethodInstance>();
//	 
//	 static HashMap<MethodInstance, MethodInstance> allMethodsLookup = new HashMap<MethodInstance, MethodInstance>();
//	
//	/**
//	 * List of methods known to be/invoke a native. 
//	 * When something leaves this list it should go immediately into {@link NativeDetector#closedMethods}.
//	 * LinkedList<MethodInstance> openMethods
//	 * @see NativeDetector#closedMethods
//	 * @see NativeDetector#selectNativeMethods()
//	 * @see NativeDetector#findNativeInvokers()
//	 */
//	LinkedList<MethodInstance> openMethods= new LinkedList<MethodInstance>();
//	
//	/**
//	 * Set of MethodInstances that have already been investigated. 
//	 * Everything in this list is or invokes a native.
//	 * HashSet<MethodInstance> closedMethods
//	 * @see NativeDetector#writeClosedMethods(String)
//	 * @see NativeDetector#openMethods
//	 * @see NativeDetector#selectNativeMethods()
//	 * @see NativeDetector#findNativeInvokers()
//	 * */
//	HashSet<MethodInstance> closedMethods = new HashSet<MethodInstance>();
//	
//	
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
	
	static HashMap<String,ArrayList<String>> dirtyList = new HashMap<String, ArrayList<String>>();
	static HashMap<String,ArrayList<String>> unknownList = new HashMap<String, ArrayList<String>>();

	
	
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
	
	

//	
//// maybeAddToDirtyList {
//	// is foo already in the dirty table?
//		// yes -> do nothing (remove foo from open list)
//		// no -> addToDirtyList(foo)
//// }
//
//// maybeAddToUnknownList {
//	// is foo already in the unknown table?
//		// yes -> do nothing (remove foo from open list)
//		// no -> addToUnknownList(foo)
//// }
//
//// addToDirtyList {
//	// dirtylist.add(foo, [])
//// }
//	
//// addToUnknownList {
//	// unknownlist.add(foo, [])
//// }
//	
//// addDirtyCaller (a, foo){
//	// if a is in dirty list
//		// do nothing
//	// if a is in unknown list
//		// dirtylist.add(a, unknownlist.get(a)
//// }
//	
//// addUnknownCaller (a, foo){
//	// unknownlist.get(a).add(foo)
//// }
//	
//	


////parse classes into methods {
//	// for each method foo in the class
//		// is foo native or in dirty list?
//			// maybeAddToDirtyList(foo)
//		// else (foo not native && not in dirty list)
//			// maybeAddToUnknownList(foo)
//			// for each function A that foo calls
//				// addUnknownCaller(A, foo)		
////}
//
//	public LinkedList<MethodInstance> getMethodsInClass(String className) throws IOException {
//		ClassReader cr = new ClassReader(className);
//		CompleteClassVisitor cv = new CompleteClassVisitor(Opcodes.ASM4, null, className);
//		cr.accept(cv, 0);
//		return cv.allMethods;
//	}
//	
//	public void addMethodsToRightList(LinkedList<MethodInstance> methodList) {
//		for(MethodInstance mi : methodList) {
//			if (mi.isNative()) {
//				addToDirtyList(mi.getMethod().toString());
//			}
//		}
//		
//	}
//	
//	public int addToUnknownList(String unknownFxn) {
//		if (dirtyList.containsKey(unknownFxn)) {
//			logger.error(unknownFxn + " already in dirtyList");
//			return 1;
//		} else if (!unknownList.containsKey(unknownFxn)) {
//			unknownList.put(unknownFxn, new ArrayList<String>());
//			return 0;
//		}
//		return 0;
//	}
//	
//	public int addToDirtyList(String dirtyFxn) {
//		if (!dirtyList.containsKey(dirtyFxn)) {
//			dirtyList.put(dirtyFxn, new ArrayList<String>());
//		}
//		return 0;
//	}
//	
//	public int addUnknownCaller(String fxn, String caller) {
//		if (unknownList.containsKey(fxn)) {
//			unknownList.get(fxn).add(caller);
//			return 0;
//		} else if (!dirtyList.containsKey(fxn)){
//			logger.error("[addUnknownCaller] neither list contains " + fxn);
//			return 1;
//		}
//		return 2;
//	}
//	
//	public int addDirtyCaller(String fxn, String caller) {
//		
//		if (unknownList.containsKey(fxn)) {
//			ArrayList<String> callerList = unknownList.get(fxn);
//			if (!callerList.contains(caller)) {
//				callerList.add(caller);
//			}
//			dirtyList.put(fxn, callerList);
//			return 0;
//		} else if (!dirtyList.containsKey(fxn)) {
//			logger.error("[addDirtyCaller] neither list contains " + fxn);
//			return 1;
//		}
//		return 2;
//	}
	
	
	

	
	
	
	
	/*
	 * (1) fetch a method x 
	 * 		if x is native
	 * 			add x to dirty hash
	 * 		else
	 * 			for each method y that x calls
	 * 				find/add y in called hash and add x to its list
	 */
	public void addLinksToChildren(MethodInstance mi) {
		if ((mi.getAccess() != 0) && mi.isNative()) {
			dirtyMap.put(mi.getFullName(), new LinkedList<String>());
		} else {
			logger.info(mi.functionsICall.size());
			for (String f : mi.functionsICall) {
				if (unprocessedMap.containsKey(f)) {
					unprocessedMap.get(f).add(mi.getFullName());
				} else {
					addPairToUnprocessedMap(f, mi.getFullName());
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
		logger.info(dirtyMap);
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
			dirtyQueue.add(dirtyPair);
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
		while (!dirtyQueue.isEmpty()) {
		
			Pair<String, LinkedList<String>> dirtyItem = dirtyQueue.pop();
			logger.info("queue size: " + dirtyQueue.size());
			logger.info(dirtyItem);
			String dirtyName = dirtyItem.fst;
			
			LinkedList<String> functionsICall = methodMap.get(dirtyName).functionsICall;
			logger.info(functionsICall);
			//if (!dirtyMap.containsKey(dirtyItem.fst)) {
				logger.info("inside if");
				for (String y: functionsICall) {
					logger.info("inside for");
					if (dirtyMap.containsKey(y)) {
						dirtyMap.get(y).add(dirtyItem.fst);
					} else {
						addPairToDirtyMap(y, dirtyItem.fst);
					}
					addToQueue(y);
			//	}
			}
			logger.info("queue size: " + dirtyQueue.size());

		}
	}
	
	
		
	private void addToQueue(String f) {
		assert(dirtyMap.containsKey(f));
		Pair<String, LinkedList<String>> pair = new Pair<String, LinkedList<String>>(f, dirtyMap.get(f));
		dirtyQueue.push(pair);
		logger.info("queue size: " + dirtyQueue.size());
	}
	
//	
//	
//	
//	
//	
//	
//		// is foo already in the dirty table?
//			// for each method A that foo calls
//				// addDirtyCaller(A, foo)
//				// maybeAddToDirtyList(A)
//				// put A in the unknown table
//				// 
//		// is foo already in the unknown table?
//	
//		// is foo native?
//			// if native
//				// put foo in dirty list
//			// else
//				// put foo in unknown list
//				// if 
//	
//	// maybeAddToDirtyList(x)
//		// if x is already in dirty list
//			// return error code for already-in-dirty-list
//		// else
//			// addToDirtyList(x)
//
//	// addToDirtyList(x) (assumes x not in dirty list)
//		// 
//	
//	// addToUnknownList(x)
//		
//	// addDirtyCaller(x, y)
//		
//	// returns fully qualified names of all dirty methods
//	public ArrayList<String> getAllDirtyMethods() throws IOException {
//		LinkedList<String> listOfClasses = new LinkedList<String>();
//		listOfClasses.addAll(allClasses);
//		while (!listOfClasses.isEmpty()) {
//			String className = listOfClasses.pop();
//			
//			ClassReader cr = new ClassReader(className);
//			CompleteClassVisitor cv = new CompleteClassVisitor(Opcodes.ASM4, null, className);
//			cr.accept(cv, 0);
//		}
//		return allClasses; // TODO fix
//	}
//	
//	
////	public void makeAllMethodsList() throws IOException {
////		// read in each class
////		// make a list of each method
////		// append that to the big list
////		
////		LinkedList<String> listOfClasses = new LinkedList<String>();
////		listOfClasses.addAll(allClasses);
////		while (!listOfClasses.isEmpty()) {
////			String className = listOfClasses.pop();
////			
////			ClassReader cr = new ClassReader(className);
////			CompleteClassVisitor cv = new CompleteClassVisitor(Opcodes.ASM4, null, className);
////			cr.accept(cv, 0);
////			
////		}
////	}
//	
	public void getAllMethods() throws IOException {
		int numClasses = allClasses.size();
		String[] listOfClasses = allClasses.toArray(new String[numClasses]);
		for (int i=0; i < numClasses; i++) { 
			String className = listOfClasses[i];
			ClassReader cr = new ClassReader(className);
			CompleteClassVisitor ccv = new CompleteClassVisitor(Opcodes.ASM4, null, className);
			cr.accept(ccv, 0);
			allMethods.addAll(ccv.allMethods);
			logger.info("methods: " + allMethods.size());
		}
	}
	
	
//	/**
//	 * Reads in openClasses and adds all methods from each class to allMethods.
//	 * Relies on openClasses already being populated.
//	 * @see NativeDetector#allClasses
//	 * @see NativeDetector#allMethods
//	 * @see CompleteClassVisitor
//	 * @throws IOException					on ClassReader failure
//	 */
//	@Deprecated
//	public void getAllMethods() throws IOException{
//		LinkedList<String> listOfClasses = new LinkedList<String>();
//		listOfClasses.addAll(allClasses);
//		while (!listOfClasses.isEmpty()) { //for each class
//			
//			String clazz = listOfClasses.pop();
//			
//			if (true){ 
//				ClassReader cr = new ClassReader(clazz);
//				CompleteClassVisitor ccv = new CompleteClassVisitor(Opcodes.ASM4, null, clazz);
//				cr.accept(ccv, 0);
//				
////				allMethods.addAll(ccv.allMethods);
//				for(MethodInstance mi : ccv.allMethods) // for each method mi in the class clazz
//				{
//					if(allMethodsLookup.containsKey(mi)) // if our hashmap already knows about mi
//					{
//						allMethods.add(allMethodsLookup.get(mi)); // put the functions it calls into allMethods
//					}
//					else // if this is a new mi
//					{
//						allMethodsLookup.put(mi, mi); // add it to the lookup table as the identity (it calls itself)
//						allMethods.add(mi); // and add it to allMethods
//					}
//				}
//			}
//		}
//		
//		//Follow the chain down to recursively add all callers
//		for(MethodInstance mi : allMethods) // for each method mi in allMethods
//		{
//			if((mi.getAccess() & Opcodes.ACC_NATIVE) != 0) // if mi is a native method
//			{
////		MethodInstance mi = getMethodInstance("java/net/NetworkInterface", "getByIndex", "(int)Ljava/net/NetworkInterface;");
//				mi.setCallers(findAllCallersOf(mi,new HashSet<MethodInstance>()));
//			}
//		}
//	}
//	
//	
//	/**
//	 * Find all callers of this function AND its descendants. (If a method has already been expanded, it will not be returned).
//	 * This is for expanding a node... to do a general search see {@link #findAllInvokers()}.
//	 * Note: The graph of which functions call which others should already be stored in memory (in the MethodInstance objects).
//	 * 
//	 * @param mi
//	 * @param expanded 			the relevant methodinstances that have already been expanded
//	 * @return
//	 */
//	private LinkedList<Integer> findAllCallersOf(int mi, HashSet<Integer> expanded)
//	{
//		if(expanded.contains(mi))	//if we've already expanded mi
//			return new LinkedList<Integer>(); // then there are no new callers, return empty list
//		LinkedList<Integer> ret = new LinkedList<Integer>(); // if we haven't already expanded mi, make a new list
//		expanded.add(mi); // we're doing it now so add it to the expanded list
//
//		// now do the actual expansion
//	//	LinkedList<MethodInstance> callers = mi.getCallers(); //get the list of fxns that call mi
//		LinkedList<Integer> callers = MethodInstance.getCallersOf(mi); // get the list of indices of functions that call mi
//		ret.addAll(callers); //and add them to the list
//		for(int caller : callers) // for each caller in that list
//		{
//			ret.addAll(findAllCallersOf(caller,expanded)); // invoke this function recursively on it
//		}
//		return ret; // when we're done, return all new callers of mi and its descendants.
//	}
//	
//	public void findAllInvokersIt() throws IOException {
//		int numClasses = allClasses.size();
//		for (int i=0; i < numClasses; i++) {
//			String className = allClasses.get(i);
//			logger.info(className);
//
//			ClassReader cr = new ClassReader(className);
//			CallFindingClassVisitor cfcv = new CallFindingClassVisitor(Opcodes.ASM4, null, className);
//			cr.accept(cfcv, 0);
//		}
//	}
//	
//	public void findAllInvokersStack() throws IOException {
//		Stack<MethodInstance> methodStack = new Stack<MethodInstance>();
//		methodStack.addAll(allMethods);
//		int methodStackSize = methodStack.size();
//		while (methodStackSize > 0) { //TODO is this efficient?
//			MethodInstance callee = methodStack.pop();
//			ClassReader cr = new ClassReader(callee.getClazz());
//			CallFindingClassVisitor cfcv = new CallFindingClassVisitor(Opcodes.ASM4, null, callee.getClazz());
//			cr.accept(cfcv, 0);
//			methodStackSize--;
//			logger.info(methodStackSize);
//		}
//		logger.info("done with findAllInvokersStack");
//	}
//	
//	/**
//	 * Go through allMethods, find each method invocation, add caller to callee. Should be called before {@link #findAllCallersOf(MethodInstance, HashSet)}.
//	 * This is for general search... to expand a node see {@link #findAllCallersOf(MethodInstance, HashSet)}.
//	 * @throws IOException 
//	 */
//	public void findAllInvokers() throws IOException {
//		LinkedList<MethodInstance> listOfAllMethods = new LinkedList<MethodInstance>(); 
//		int loamLength = 0;
//	//	loamCount++;
//		listOfAllMethods.addAll(allMethods); //TODO is there a more efficient way to do this? cloning?
//		loamLength += allMethods.size(); //slow line. //TODO find faster way
//		while (!listOfAllMethods.isEmpty()) {
//			MethodInstance mi = listOfAllMethods.pop();
//			loamLength--;			
//			logger.info("lOAM:" + loamLength+ ", open: " + openMethods.size() + ", closed: " + closedMethods.size());
//			ClassReader cr = new ClassReader(mi.getClazz());
//			CallFindingClassVisitor cfcv = new CallFindingClassVisitor(Opcodes.ASM4, null, mi.getClazz());
//			cr.accept(cfcv, 0);
//			//
//////			logger.info("lOAM:" + loamLength + ", allM: " + getAllMethodsSize() + ", allML: " + getAllMethodLookupSize()+ ", open: " + openMethods.size() + ", closed: " + closedMethods.size());
////
////			MethodInstance mi = listOfAllMethods.pop();
////			loamLength--;
////
////			ClassReader cr = new ClassReader(mi.getClazz());
////			CallFindingClassVisitor cfcv = new CallFindingClassVisitor(Opcodes.ASM4, null, mi.getClazz());
////			cr.accept(cfcv, 0);
//		}
//	//	loamCount--; //TODO why is this heap overflowing?
//	}
//	
//
//	
//
////	
////	/**
////	 * Saves contents of closedMethods to a file.
////	 * @param filename				String			name of output file (including extension)
////	 * @throws FileNotFoundException				on file write error
////	 * @see PrintWriter#write(String)
////	 * @see MethodInstance
////	 * @see NativeDetector#closedMethods
////	 * 
////	 */
////	public void writeClosedMethods(String filename) throws FileNotFoundException {
////		PrintWriter out = new PrintWriter(filename);
//////		Iterator<MethodInstance> it = closedMethods.iterator();
//////		while (it.hasNext()) {
//////			MethodInstance mi = it.next();
//////			out.write(mi.getClazz() + "\t\t" + mi.getMethod() + "\n");
//////		}
////		for(MethodInstance mi : allMethods)
////		{
////			if((mi.getAccess() & Opcodes.ACC_NATIVE) != 0)
////			{
////				out.write(mi.getClazz() + "\t\t" + mi.getMethod() + "\n");
////				for(MethodInstance child : mi.getCallers())
////				{
////					out.write("\t\t"+child.getClazz() + "\t\t" + child.getMethod()+"\n");
////				}
////			}
////		}
////		out.close();
////		logger.info("done with writeClosedMethods");
////	}
//
////	
////	/**
////	 * Debugging method to display how many methods are in each category.
////	 * @see NativeDetector#allMethods
////	 * @see NativeDetector#openMethods
////	 * @see NativeDetector#closedMethods
////	 */
////	public void logStats() {
////		String semids = ";  ";
////		Exception ex = new Exception();
////		StackTraceElement[] el = ex.getStackTrace();
////		String id = el[1].getClassName() + ". " + el[1].getMethodName() + ":"+el[1].getLineNumber();
////		logger.info(id + "   allMethods: " + allMethods.size() + semids + "openMethods: " + openMethods.size() + semids + "closedMethods: " + closedMethods.size());		
////
////	}
//	
//	/**
//	 * Returns the exact MethodInstance that is in allMethods. 
//	 * Ensures that attributes are assigned to the correct instantiations.
//	 * @param clazz			String			name of class owning method
//	 * @param name			String			name of method
//	 * @param desc			String			method descriptor
//	 * @return				MethodInstance		the corresponding MethodInstance from allMethods
//	 * @see NativeDetector#allMethods
//	 */
//	public static MethodInstance getMethodInstance(String clazz, String name, String desc) {
//		MethodInstance dummy = new MethodInstance(name, desc, clazz);
//
//		MethodInstance ret = allMethodsLookup.get(dummy);
//		if(ret == null)
//		{
//			allMethodsLookup.put(dummy, dummy);
//			ret = dummy;
//		}
//		return ret;
//	}
//	
//	public int getAllMethodsSize() {
//		return allMethods.size();
//	}
//	public int getAllMethodLookupSize() {
//		return allMethodsLookup.size();
//	}

}
