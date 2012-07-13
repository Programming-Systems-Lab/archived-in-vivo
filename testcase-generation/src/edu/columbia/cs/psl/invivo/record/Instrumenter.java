package edu.columbia.cs.psl.invivo.record;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import edu.columbia.cs.psl.invivo.record.visitor.COAClassVisitor;

public class Instrumenter {
	public static void main(String[] args) {
		if (args.length <= 1) {
			System.err
					.println("Usage: java edu.columbia.cs.psl.invivo.record.Instrumenter [outputFolder] [path1 to instrument] [path2] ... [pathn]\n Paths can be classes, directories, or jar files");
			System.exit(-1);
		}
		String outputFolder = args[0];
		File outputDir = new File(outputFolder);
		if (!outputDir.exists())
			outputDir.mkdir();
		for (int i = 1; i < args.length; i++) {
			File f = new File(args[i]);
			if (!f.exists()) {
				System.err.println("Unable to read path " + args[i]);
				System.exit(-1);
			}
			if (f.isDirectory())
				processDirectory(f, outputDir, true);
			else if (args[i].endsWith(".jar"))
				processJar(f,outputDir);
			else if (args[i].endsWith(".class"))
				try {
					processClass(f.getName(), new FileInputStream(f), outputDir);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			else {
				System.err.println("Unknown type for path " + args[i]);
				System.exit(-1);
			}
		}
	}

	private static void processClass(String name, InputStream is, File outputDir) {
		System.out.println("PRocessing " + name);
		try {
			ClassReader cr = new ClassReader(is);
			ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
			COAClassVisitor cv = new COAClassVisitor(Opcodes.ASM4, cw);
			cr.accept(cv, ClassReader.EXPAND_FRAMES);

			FileOutputStream fos = new FileOutputStream(outputDir.getPath() + File.separator + name);
			ByteArrayOutputStream bos = new ByteArrayOutputStream(cw.toByteArray().length);
			bos.write(cw.toByteArray());
			bos.writeTo(fos);
			fos.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	private static void processJar(File f, File outputDir) {
		try {
			JarFile jar = new JarFile(f);
			Enumeration<JarEntry> entries = jar.entries();
			while (entries.hasMoreElements()) {
				JarEntry e = entries.nextElement();
				if (e.getName().endsWith(".class"))
				{
					String path = e.getName().substring(0,e.getName().lastIndexOf(File.separator));
					String clazz = e.getName().substring(e.getName().lastIndexOf(File.separator) + 1);
					File dir = new File(outputDir+File.separator+path);
					if(!dir.exists())
						dir.mkdirs();
					processClass(clazz, jar.getInputStream(e), dir);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void processDirectory(File f, File parentOutputDir, boolean isFirstLevel) {
		File thisOutputDir;
		if (isFirstLevel) {
			thisOutputDir = parentOutputDir;

		} else {
			thisOutputDir = new File(parentOutputDir.getAbsolutePath() + File.separator + f.getName());
			thisOutputDir.mkdir();
		}
		for (File fi : f.listFiles()) {
			if (fi.isDirectory())
				processDirectory(fi, thisOutputDir, false);
			else if (fi.getName().endsWith(".class"))
				try {
					processClass(fi.getName(), new FileInputStream(fi), thisOutputDir);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			else if(fi.getName().endsWith(".lib"))
				processJar(fi, thisOutputDir);
		}
		
	}
}
