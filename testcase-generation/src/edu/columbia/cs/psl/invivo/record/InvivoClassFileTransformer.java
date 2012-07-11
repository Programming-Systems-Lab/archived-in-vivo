package edu.columbia.cs.psl.invivo.record;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import org.apache.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import edu.columbia.cs.psl.invivo.record.visitor.COAClassVisitor;

public class InvivoClassFileTransformer implements ClassFileTransformer {
	private static Logger	logger	= Logger.getLogger(InvivoClassFileTransformer.class);

	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
			byte[] classfileBuffer) throws IllegalClassFormatException {
		String name = className.replace("/", ".");
		if (!name.startsWith("java")) {

			
			ClassReader cr = new ClassReader(classfileBuffer);
			ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
			try {
				COAClassVisitor cv = new COAClassVisitor(Opcodes.ASM4, cw);
				cr.accept(cv, ClassReader.EXPAND_FRAMES);
				File f = new File("debug/");
				if (!f.exists())
					f.mkdir();
				FileOutputStream fos = new FileOutputStream("debug/" + name + ".class");
				ByteArrayOutputStream bos = new ByteArrayOutputStream(cw.toByteArray().length);
				bos.write(cw.toByteArray());
				bos.writeTo(fos);
				fos.close();

			} catch (Exception ex) {
				logger.error("Error generating modified class " + name, ex);
			}
			return cw.toByteArray();
		}
		return classfileBuffer;
	}

}
