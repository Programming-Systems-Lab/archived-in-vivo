package v2;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.Method;


public class NativeDetector {

	String jarPath;
//	HashMap<String,Method> openMethodMap = new HashMap<String,Method>();
//	HashMap<String,Method> closedMethodMap = new HashMap<String, Method>();
	
	HashSet<String>	openClasses = new HashSet<String>();
	@Deprecated
	HashSet<Method> openMethods = new HashSet<Method>();
	HashMap<Method,String> openMethodsWithClasses = new HashMap<Method,String>();
	@Deprecated
	HashSet<String> closedMethods = new HashSet<String>();
	HashMap<Method,String> closedMethodsWithClasses = new HashMap<Method,String>();

	//	HashSet<Method> closedMethods = new HashSet<Method>();
	private static Logger logger = Logger.getLogger(NativeDetector.class);
	
	
	public NativeDetector(String jarURL) {
		jarPath = jarURL;
	}

	void getAllClasses() throws IOException {
		HashSet<String> classList = new HashSet<String>();
		JarFile classJar = new JarFile(jarPath);
		Enumeration<JarEntry> jarContents = classJar.entries();
		while (jarContents.hasMoreElements())
			classList.add(jarContents.nextElement().getName());				
		classJar.close();

		openClasses = classList;
		logger.info("openClasses.size() = " + openClasses.size());
	}

	void findNativesFromClasses() throws IOException {
		Iterator<String> it = openClasses.iterator();
		while ( it.hasNext() ) {
			String curClassName= it.next();
			if (curClassName.endsWith(".class")){ 
				curClassName = curClassName.substring(0, curClassName.length()-6);   //remove ".class"

				ClassReader cr = new ClassReader(curClassName);
				InitialClassVisitor icv = new InitialClassVisitor(Opcodes.ASM4, null, curClassName);
				cr.accept(icv, 0);
				
				//done with processing class current
				// option a - use hashset
			//	this.openMethods.addAll(icv.methods);
				
				// option b - use hashmap
				Iterator<Method> methodIt = icv.methods.iterator();
				while (methodIt.hasNext()) {
					Method m = methodIt.next();
				//	logger.info("looking at method: " + m.getName());
					openMethodsWithClasses.put(m, curClassName);
				//	logger.error("length of openMethods: " + openMethodsWithClasses.size());
				}
				
				logger.info("done with " + curClassName + "\topenMethods:" + openMethodsWithClasses.size() + "\tclosedMethods:" + closedMethodsWithClasses.size());	
			}
		};
		
	}
	
	void findNativesFromMethods() throws Exception {
		Iterator<Entry<Method, String>> mapIt = openMethodsWithClasses.entrySet().iterator();
		
		while (mapIt.hasNext()) {
			Entry<Method,String> entry = mapIt.next();
			Method m = entry.getKey();
			String className = entry.getValue();
			if (!closedMethodsWithClasses.containsKey(m)) {
				InvocationFinder ifinder = new InvocationFinder();
				ifinder.findCallingMethodsInJar(jarPath, className, m.getDescriptor());
				HashMap<Method, String> invMap = ifinder.invokersWithClasses;
				openMethodsWithClasses.putAll(invMap);
				closedMethodsWithClasses.put(m, className);
				openMethodsWithClasses.remove(m);
			}
			// investigate next method m
			// is m already in closed methods?
			// add any new methods to openmethods
			// add m to closedmethods
			// remove m from openmethods
		}
	}
	
	void moveToClosed(Method m) {
		closedMethodsWithClasses.put(m, openMethodsWithClasses.get(m));
		openMethodsWithClasses.remove(m);
		//openMethods.remove(m);
		//closedMethods.add(m.getName());
	}

	void findOpenInvocations() {
		/** TODO
		 * while openMethods is not empty:
		 * for Method m at openMethods[0]:
		 * 		find all Methods d that invoke m
		 * 		for each d:
		 * 			if d is in openMethods: do nothing
		 * 			if d is in closedMethods: do nothing
		 * 			if d is in neither: add d to openMethods
		 * 		remove m from openMethods
		 * 		add m to closedMethods 
		 * done
		 * return closedMethods (not void)
		 */
		Iterator<Method> it = openMethods.iterator();

		while (it.hasNext()) {
			Method m = it.next();
			InvocationFinder ifinder = new InvocationFinder();
			//HashSet<Method> invokers = ifinder.findCallingMethodsInJar(jarPath, m.getClass(), targetMethodDeclaration)
			// TODO ifinder.run(jarPath, c.className, c.methodName)
		}
	}
	
	HashSet<Method> findInvocationsOf( HashSet<Method> nativeMethods) throws Exception {
		// TODO
		/*
		 * for each method m in nativeMethods
		 * find all invocations of m
		 */
		Iterator<Method> it = nativeMethods.iterator();
		HashSet<Method> inv = null;
		while (it.hasNext())
			try {
				{
					Method m = it.next();
					InvocationFinder ifinder = new InvocationFinder();
					ifinder.findCallingMethodsInJar(jarPath, m.getClass().getName(), m.getDescriptor());
					inv = ifinder.invokers;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return inv;
	}

	HashSet<Method> visitAllMethods(ClassVisitor cv) {
		// TODO Auto-generated method stub
		return null;
	}

	void writeMethodsToFile(Set<Method> methods, File f) {
		// TODO Auto-generated method stub
		// don't forget to close filewriter and bufferedwriter
		
	}

	/* HashSet<Method> readMethodsFromFile(File f) {
		// TODO Auto-generated method stub
		 * close filereader and bufferedwriter
		return null;
	}*/


}
