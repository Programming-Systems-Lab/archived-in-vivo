package edu.columbia.cs.psl.invivo.replay;

import java.net.URLClassLoader;

import org.apache.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

public class ReplayerClassWriter extends ClassWriter {
	private static Logger logger = Logger.getLogger(ReplayerClassWriter.class);
	private ClassLoader loader;
	
	public ReplayerClassWriter(ClassReader classReader, int flags) {
		super(classReader, flags);
		// TODO Auto-generated constructor stub
	}
	
	public ReplayerClassWriter(ClassReader classReader, int flags, ClassLoader loader) {
		super(classReader, flags);
		this.loader = loader;
	}

}
