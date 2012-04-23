package v2;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.Method;


public class NativeDetector {

	static String jarPath = "/Users/miriammelnick/Desktop/research/classes.jar";
	static String[] forbiddenSuffixes = {".properties", ".res", ".wav", ".gif", ".png", ".xsd"};
	
	public static void main(String[] args) {
	//	HashSet<Method> allMethods = readMethodsFromFile(new File(""));
		try {
			HashSet<String> allClasses = getAllClasses();
			HashSet<Method> nativeMethods = findNativeMethods(allClasses);
			System.out.println(nativeMethods.size());
			System.out.flush();
			HashSet<Method> nativeInvokingMethods = findInvocationsOf(nativeMethods);
			writeMethodsToFile(nativeInvokingMethods, new File("nativeInvokingMethods.txt"));
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return;
	}

	private static HashSet<String> getAllClasses() throws IOException {
		HashSet<String> classList = new HashSet<String>();
		JarFile classJar = new JarFile(jarPath);
		Enumeration<JarEntry> jarContents = classJar.entries();
		while (jarContents.hasMoreElements())
			classList.add(jarContents.nextElement().getName());				
		classJar.close();

		return classList;
	}

	private static HashSet<Method> findNativeMethods(HashSet<String> allClasses) throws IOException {
		Iterator<String> it = allClasses.iterator();
		String current = null;
		while (it.hasNext()) {
			current = it.next();
			if (current.endsWith(".class")) {
				current = current.substring(0, current.length()-6);   //remove ".class"
				System.out.println("processing " + current);
				ClassReader cr = new ClassReader(current);
				cr.accept(new FirstClassVisitor(Opcodes.ASM4, null, current), 0);
				System.out.println(FirstClassVisitor.methods.size());
			}			
		}
		return FirstClassVisitor.methods;
	}

	private static HashSet<Method> findInvocationsOf( HashSet<Method> nativeMethods) {
		// TODO Auto-generated method stub
		return null;
	}

	private HashSet<Method> visitAllMethods(ClassVisitor cv) {
		// TODO Auto-generated method stub
		return null;
	}

	private static void writeMethodsToFile(Set<Method> methods, File f) {
		// TODO Auto-generated method stub
		// don't forget to close filewriter and bufferedwriter
		
	}

	/*private static HashSet<Method> readMethodsFromFile(File f) {
		// TODO Auto-generated method stub
		 * close filereader and bufferedwriter
		return null;
	}*/


}
