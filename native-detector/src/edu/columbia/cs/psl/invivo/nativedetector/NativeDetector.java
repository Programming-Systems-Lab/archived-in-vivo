package edu.columbia.cs.psl.invivo.nativedetector;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;


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
	 * List of all classes to be investigated.
	 * LinkedList<String> openClasses
	 * @see NativeDetector#getAllClasses()
	 */
	LinkedList<String>	openClasses = new LinkedList<String>();
	
	/**
	 * List of every method from every class. MethodInstances will be added through {@link NativeDetector#getAllMethods()} 
	 * and will be deleted through {@link NativeDetector#findNativeInvokers()}.
	 * private LinkedList<MethodInstance> allMethods
	 * @see NativeDetector#getAllMethods()
	 * @see NativeDetector#findNativeInvokers()
	 */
	LinkedList<MethodInstance> allMethods = new LinkedList<MethodInstance>();

//TODO uncomment	private LinkedList<MethodInstance> allMethods = new LinkedList<MethodInstance>();
	
	/**
	 * List of methods known to be/invoke a native. 
	 * When something leaves this list it should go immediately into {@link NativeDetector#closedMethods}.
	 * LinkedList<MethodInstance> openMethods
	 * @see NativeDetector#closedMethods
	 * @see NativeDetector#selectNativeMethods()
	 * @see NativeDetector#findNativeInvokers()
	 */
	LinkedList<MethodInstance> openMethods= new LinkedList<MethodInstance>();
	
	/**
	 * Set of MethodInstances that have already been investigated. 
	 * Everything in this list is or invokes a native.
	 * HashSet<MethodInstance> closedMethods
	 * @see NativeDetector#writeClosedMethods(String)
	 * @see NativeDetector#openMethods
	 * @see NativeDetector#selectNativeMethods()
	 * @see NativeDetector#findNativeInvokers()
	 * */
	HashSet<MethodInstance> closedMethods = new HashSet<MethodInstance>();
	
	
	/**
	 * Logger for NativeDetector class (log4j).
	 * private static Logger logger
	 * @see Logger
	 */
	private static Logger logger = Logger.getLogger(NativeDetector.class);
	
	/**
	 * Constructor for a NativeDetector object
	 * @param jarURL			String				absolute path to classes.jar (java std lib jar)
	 * @see NativeDetector#openClasses
	 */
	public NativeDetector(String jarURL) {
		jarPath = jarURL;
	}


	/**
	 * Record that caller calls callee. This uses the MethodInstances that are in allMethods, 
	 * but the parameters can be created with new MethodInstance().
	 * @see MethodInstance
	 * @param caller			MethodInstance		the method making the call
	 * @param callee			MethodInstance		the method being called
	 */
	public void addCaller(MethodInstance caller, MethodInstance callee) {
		
		MethodInstance listCaller = (allMethods.contains(caller)) ? allMethods.get(allMethods.indexOf(caller)) : caller;
		MethodInstance listCallee = (allMethods.contains(callee)) ? allMethods.get(allMethods.indexOf(callee)) : callee;
		listCallee.addCaller(listCaller);
		if (!openMethods.contains(listCaller))  // TODO is this logically right?
				openMethods.add(listCaller);
		
	}
	
	
	
	/**
	 * Reads in the jar file and populates openClasses with class names.
	 * @see NativeDetector#jarPath
	 * @throws IOException			on jar file read failure
	 */
	public void getAllClasses() throws IOException {
		LinkedList<String> classList = new LinkedList<String>();
		JarFile classJar = new JarFile(jarPath);
		Enumeration<JarEntry> jarContents = classJar.entries();
		while (jarContents.hasMoreElements())
			classList.add(jarContents.nextElement().getName());				
		classJar.close();

		openClasses = classList;
		logger.info("openClasses.size() = " + openClasses.size());
	}
	
	/**
	 * Reads in openClasses and adds all methods from each class to allMethods.
	 * Relies on openClasses already being populated.
	 * @see NativeDetector#openClasses
	 * @see NativeDetector#allMethods
	 * @see CompleteClassVisitor
	 * @throws IOException					on ClassReader failure
	 */
	public void getAllMethods() throws IOException{
		LinkedList<String> listOfClasses = new LinkedList<String>();
		listOfClasses.addAll(openClasses);
		while (!listOfClasses.isEmpty()) {
			
			String clazz = listOfClasses.pop();
			
			if (clazz.endsWith(".class")){ 
				clazz = clazz.substring(0, clazz.length()-6);   //remove ".class"
				ClassReader cr = new ClassReader(clazz);
				CompleteClassVisitor ccv = new CompleteClassVisitor(Opcodes.ASM4, null, clazz);
				cr.accept(ccv, 0);
				
				allMethods.addAll(ccv.allMethods);
			}
		}
	}
	
	// use callfindingclassvisitor
	/**
	 * TODO comment findAllInvokers
	 * @throws IOException 
	 */
	public void findAllInvokers() throws IOException {
		LinkedList<MethodInstance> listOfAllMethods = new LinkedList<MethodInstance>(); 
		listOfAllMethods.addAll(allMethods); //TODO is there a more efficient way to do this? cloning?
		while (!listOfAllMethods.isEmpty()) {
			MethodInstance mi = listOfAllMethods.pop();

			ClassReader cr = new ClassReader(mi.getClazz());
			CallFindingClassVisitor cfcv = new CallFindingClassVisitor(Opcodes.ASM4, null, mi.getClazz(), this);
			cr.accept(cfcv, 0);
		}
	}
	
	
	/**
	 * Checks access attribute of each methodinstance in allMethods. NOT recursive.
	 * @see NativeDetector#allMethods
	 * @see MethodInstance#getAccess()
	 */
	public void selectNativeMethods() {
		while (!allMethods.isEmpty()) {
			MethodInstance mi = allMethods.pop();
			if ((mi.getAccess() & Opcodes.ACC_NATIVE) != 0) {
				logger.info(mi.getClazz()+"."+mi.getMethod()+" appears to be native");
				if ((!openMethods.contains(mi)) && (!closedMethods.contains(mi))) {
					logStats();
					openMethods.add(mi);
				}
			}
		}
	}
	
	
	/**
	 * Finds all functions that call native methods.
	 * Requires openMethods to be populated with (all of the) native methods. This handles recursion.
	 * In each iteration, adds any new methods to openMethods and moves the just-investigated one to closedMethods.
	 * @see NativeDetector#openMethods
	 * @see NativeDetector#closedMethods
	 * @see MethodInstance#getCallers()
	 */
	public void findNativeInvokers() {
		while (!openMethods.isEmpty()) {
			MethodInstance nativeMethod = openMethods.pop();
			logStats();
			LinkedList<MethodInstance> callers = nativeMethod.getCallers();
			logger.info(callers.size() + " callers of " + nativeMethod.getClazz() + "." + nativeMethod.getMethod().getName());
			while (!callers.isEmpty()) {
				MethodInstance caller = callers.pop();
				if ((!closedMethods.contains(caller)) && (!openMethods.contains(caller))) {
					openMethods.add(caller);
					assert(!closedMethods.contains(nativeMethod));
				}
			}
			closedMethods.add(nativeMethod);
		}
	}
	
	
	/**
	 * Saves contents of closedMethods to a file.
	 * @param filename				String			name of output file (including extension)
	 * @throws FileNotFoundException				on file write error
	 * @see PrintWriter#write(String)
	 * @see MethodInstance
	 * @see NativeDetector#closedMethods
	 * 
	 */
	public void writeClosedMethods(String filename) throws FileNotFoundException {
		PrintWriter out = new PrintWriter(filename);
		Iterator<MethodInstance> it = closedMethods.iterator();
		while (it.hasNext()) {
			MethodInstance mi = it.next();
			out.write(mi.getClazz() + "\t\t" + mi.getMethod() + "\n");
		}
		out.close();
		logger.info("done with writeClosedMethods");
	}

	/**
	 * Debugging method to display how many methods are in each category.
	 * @see NativeDetector#allMethods
	 * @see NativeDetector#openMethods
	 * @see NativeDetector#closedMethods
	 */
	public void logStats() {
		String semids = ";  ";
		logger.info("allMethods: " + allMethods.size() + semids + "openMethods: " + openMethods.size() + semids + "closedMethods: " + closedMethods.size());		

	}
	
	/**
	 * Returns the exact MethodInstance that is in allMethods. 
	 * Ensures that attributes are assigned to the correct instantiations.
	 * @param clazz			String			name of class owning method
	 * @param name			String			name of method
	 * @param desc			String			method descriptor
	 * @return				MethodInstance		the corresponding MethodInstance from allMethods
	 * @see NativeDetector#allMethods
	 */
	public MethodInstance getMethodInstance(String clazz, String name, String desc) {
		MethodInstance dummy = new MethodInstance(name, desc, clazz);
		if (allMethods.contains(dummy)) {
			return allMethods.get(allMethods.indexOf(dummy));
		}
		return null;
	}

}
