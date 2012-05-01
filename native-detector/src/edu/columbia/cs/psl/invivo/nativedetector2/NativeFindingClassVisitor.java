package edu.columbia.cs.psl.invivo.nativedetector2;

import java.io.BufferedOutputStream;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class NativeFindingClassVisitor extends ClassVisitor {
	String owner;
	FileWriter fw;
	BufferedWriter bw;
	FileOutputStream fos;
	BufferedOutputStream bos;
	private static Logger logger = Logger.getLogger(NativeFindingClassVisitor.class);

	
	public NativeFindingClassVisitor(int api, ClassVisitor cv, String className) throws IOException {
		super(api, cv);
		this.owner = className;
		this.fw = new FileWriter("nativeMethods.txt", true);
		this.bw = new BufferedWriter(this.fw);
		
	}
	@Override
	public void visitEnd() {
		try {
			this.bw.close();
			this.fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.visitEnd();
	}
	@Override
	public MethodVisitor visitMethod(int access, String name, String desc,
			String signature, String[] exceptions) {
		try {
			if ((access & Opcodes.ACC_NATIVE) == 0)
			{
				//System.out.println(name);
				logger.info("\t"+name);
				this.bw.write(this.owner+"\t"+name +"\t"+ desc+"\n");
			}
		} catch (FileNotFoundException e) {
			System.err.println(this.owner + " " + access + " " + name + " " + desc + " " + signature);
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println(this.owner + " " + access + " " + name + " " + desc + " " + signature);
			e.printStackTrace();
		}

		return super.visitMethod(access, name, desc, signature, exceptions);
	}
}
