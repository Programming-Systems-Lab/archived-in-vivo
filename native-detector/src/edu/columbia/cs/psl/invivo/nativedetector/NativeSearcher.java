package edu.columbia.cs.psl.invivo.nativedetector;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.Method;

public class NativeSearcher {

	static String jarPath = "/Users/miriammelnick/Desktop/research/classes.jar";
	
	public static void main(String[] args) throws IOException {
//		ClassNameFinder.main(null);
//		int cnfLength = ClassNameFinder.properClassNames.size();
//		for (int i = 0; i < cnfLength; i++) {
//			String className = ClassNameFinder.properClassNames.get(i);
//			ClassReader cr = new ClassReader(className);
//			cr.accept(new NativeFindingClassVisitor(Opcodes.ASM4, null, className), 0);
//		}
//		System.out.println("All native methods written to file.");

		System.out.println("Next step: import native methods and find all calls to them.");
		FileReader fr = new FileReader("nativeMethods.txt");
		BufferedReader br = new BufferedReader(fr);
		for (int i = 0; i < 10; i++) {
			//if ((i % 10) == 0)
			String inputLine = br.readLine();
			System.out.println(inputLine);
			NativeRecursiveFinder nrf = new NativeRecursiveFinder();
			nrf.getNativeMethods(inputLine);
		}
		System.out.println("done. check invocation.txt"); 
		
		
	}
	
}
