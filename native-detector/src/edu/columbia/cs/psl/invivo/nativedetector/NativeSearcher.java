package edu.columbia.cs.psl.invivo.nativedetector;

import java.io.IOException;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;

public class NativeSearcher {

	
	public static void main(String[] args) throws IOException {
		ClassNameFinder.main(null);
		int cnfLength = ClassNameFinder.properClassNames.size();
		for (int i = 0; i < cnfLength; i++) {
			String className = ClassNameFinder.properClassNames.get(i);
			ClassReader cr = new ClassReader(className);
			cr.accept(new CallFindingClassVisitor(Opcodes.ASM4, null, className), 0);
		}
	
	}
	
}
