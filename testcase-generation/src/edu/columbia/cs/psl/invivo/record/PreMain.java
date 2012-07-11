package edu.columbia.cs.psl.invivo.record;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;



public class PreMain {
	public static void premain(String args, Instrumentation inst) {
		ClassFileTransformer transformer = 
				new InvivoClassFileTransformer();
inst.addTransformer(transformer);
	}
}
