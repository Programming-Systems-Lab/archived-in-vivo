package edu.columbia.cs.psl.invivo.record;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.channels.FileChannel;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

import org.apache.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import edu.columbia.cs.psl.invivo.record.analysis.MutabilityAnalyzer;
import edu.columbia.cs.psl.invivo.record.struct.AnnotatedMethod;
import edu.columbia.cs.psl.invivo.record.visitor.CloningAdviceAdapter;
import edu.columbia.cs.psl.invivo.record.visitor.NonDeterministicLoggingClassVisitor;

public class Instrumenter {
	public static URLClassLoader						loader;
	private static Logger								logger				= Logger.getLogger(Instrumenter.class);
	public static HashMap<String, AnnotatedMethod>		annotatedMethods	= new HashMap<String, AnnotatedMethod>();
	public static HashMap<String, ClassNode>			instrumentedClasses	= new HashMap<String, ClassNode>();

	private static MutabilityAnalyzer					ma					= new MutabilityAnalyzer(annotatedMethods);
	private static HashMap<String, HashSet<MethodCall>>	methodCalls			= new HashMap<String, HashSet<MethodCall>>();
	private static final int							NUM_PASSES			= 2;
	private static final int							PASS_ANALYZE		= 0;
	private static final int							PASS_OUTPUT			= 1;

	private static int									pass_number			= 0;

	private static File									rootOutputDir;
	private static String								lastInstrumentedClass;

	public static AnnotatedMethod getAnnotatedMethod(String owner, String name, String desc) {
		String lookupKey = owner + "." + name + ":" + desc;
		return annotatedMethods.get(lookupKey);
	}

	private static void analyzeClass(InputStream inputStream) {
		try {
			ClassNode analysisResult = ma.analyzeClass(new ClassReader(inputStream));
			instrumentedClasses.put(analysisResult.name, analysisResult);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void finishedPass() {
		switch (pass_number) {
		case PASS_ANALYZE:
			ma.doneSupplyingClasses();
			break;
		case PASS_OUTPUT:
			generateLogOfLogClass();
			break;
		}
	}

	private static byte[] generateLogClass(String className) {
		ClassWriter cw = new InstrumenterClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES, loader);
		cw.visit(49, Opcodes.ACC_PUBLIC, className + Constants.LOG_CLASS_SUFFIX, null, "java/lang/Object", null);
		cw.visitSource(null, null);

		{
			MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, "<clinit>", "()V", null, null);
			GeneratorAdapter mvz = new GeneratorAdapter(mv, Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "<clinit>", "()V");
			mvz.visitCode();
			for (MethodCall call : methodCalls.get(className)) {
				mvz.push(Constants.DEFAULT_LOG_SIZE);
				mvz.newArray(Type.getMethodType(call.getMethodDesc()).getReturnType());
				mvz.putStatic(Type.getType("L" + className + Constants.LOG_CLASS_SUFFIX + ";"), call.getLogFieldName(),
						Type.getType("[" + Type.getMethodType(call.getMethodDesc()).getReturnType().getDescriptor()));

				Type[] argTypes = Type.getArgumentTypes(call.getMethodDesc());
				for (int i = 0; i < argTypes.length; i++) {
					if (argTypes[i].getSort() == Type.ARRAY) {
						mvz.push(Constants.DEFAULT_LOG_SIZE);
						mvz.newArray(argTypes[i]);
						mvz.putStatic(Type.getType("L" + className + Constants.LOG_CLASS_SUFFIX + ";"), call.getLogFieldName() + "_" + i,
								Type.getType("[" + argTypes[i].getDescriptor()));
					}
				}
			}
			mvz.visitMaxs(0, 0);
			mvz.returnValue();
			mvz.visitEnd();
		}
		{
			MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
			mv.visitVarInsn(Opcodes.ALOAD, 0);
			mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
			mv.visitInsn(Opcodes.RETURN);
			mv.visitMaxs(1, 1);
			mv.visitEnd();
		}

		{
			MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, "clearLog", "()V", null, null);
			GeneratorAdapter mvz = new GeneratorAdapter(mv, Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "clearLog", "()V");
			mvz.visitCode();
			for (MethodCall call : methodCalls.get(className)) {
				mvz.push(Constants.DEFAULT_LOG_SIZE);
				mvz.newArray(Type.getMethodType(call.getMethodDesc()).getReturnType());
				mvz.putStatic(Type.getType("L" + className + Constants.LOG_CLASS_SUFFIX + ";"), call.getLogFieldName(),
						Type.getType("[" + Type.getMethodType(call.getMethodDesc()).getReturnType().getDescriptor()));
				mvz.push(0);
				mvz.putStatic(Type.getType("L" + className + Constants.LOG_CLASS_SUFFIX + ";"), call.getLogFieldName() + "_fill", Type.INT_TYPE);

				Type[] argTypes = Type.getArgumentTypes(call.getMethodDesc());
				for (int i = 0; i < argTypes.length; i++) {
					if (argTypes[i].getSort() == Type.ARRAY) {
						mvz.push(Constants.DEFAULT_LOG_SIZE);
						mvz.newArray(argTypes[i]);
						mvz.putStatic(Type.getType("L" + className + Constants.LOG_CLASS_SUFFIX + ";"), call.getLogFieldName() + "_" + i,
								Type.getType("[" + argTypes[i].getDescriptor()));
						mvz.push(0);
						mvz.putStatic(Type.getType("L" + className + Constants.LOG_CLASS_SUFFIX + ";"), call.getLogFieldName() + "_" + i + "_fill",
								Type.INT_TYPE);
					}
				}
			}
			mvz.visitMaxs(0, 0);
			mvz.returnValue();
			mvz.visitEnd();
		}

		{
			MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, "clearLogFill", "()V", null, null);
			GeneratorAdapter mvz = new GeneratorAdapter(mv, Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "clearLogFill", "()V");
			mvz.visitCode();
			for (MethodCall call : methodCalls.get(className)) {
				mvz.push(0);
				mvz.putStatic(Type.getType("L" + className + Constants.LOG_CLASS_SUFFIX + ";"), call.getLogFieldName() + "_fill", Type.INT_TYPE);

				Type[] argTypes = Type.getArgumentTypes(call.getMethodDesc());
				for (int i = 0; i < argTypes.length; i++) {
					if (argTypes[i].getSort() == Type.ARRAY) {
						mvz.push(0);
						mvz.putStatic(Type.getType("L" + className + Constants.LOG_CLASS_SUFFIX + ";"), call.getLogFieldName() + "_" + i + "_fill",
								Type.INT_TYPE);
					}
				}
			}
			mvz.visitMaxs(0, 0);
			mvz.returnValue();
			mvz.visitEnd();
		}
	
		for (MethodCall call : methodCalls.get(className)) {
			int opcode = Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC;
			FieldNode fn = new FieldNode(Opcodes.ASM4, opcode, call.getLogFieldName(), "["
					+ Type.getMethodType(call.getMethodDesc()).getReturnType().getDescriptor(), null, null);
			fn.accept(cw);
			FieldNode fn2 = new FieldNode(Opcodes.ASM4, opcode, call.getLogFieldName() + "_fill", Type.INT_TYPE.getDescriptor(), null, 0);
			fn2.accept(cw);
			FieldNode fn3 = new FieldNode(Opcodes.ASM4, Opcodes.ACC_STATIC, call.getLogFieldName() + "_replayIndex", Type.INT_TYPE.getDescriptor(), null, 0);
			fn3.accept(cw);

			Type[] argTypes = Type.getArgumentTypes(call.getMethodDesc());
			for (int i = 0; i < argTypes.length; i++) {
				if (argTypes[i].getSort() == Type.ARRAY) {
					fn = new FieldNode(Opcodes.ASM4, opcode, call.getLogFieldName() + "_" + i, "[" + argTypes[i].getDescriptor(), null, null);
					fn.accept(cw);
					fn2 = new FieldNode(Opcodes.ASM4, opcode, call.getLogFieldName() + "_" + i + "_fill", Type.INT_TYPE.getDescriptor(), null, 0);
					fn2.accept(cw);
					fn3 = new FieldNode(Opcodes.ASM4, Opcodes.ACC_STATIC, call.getLogFieldName() + "_"+i+"_replayIndex", Type.INT_TYPE.getDescriptor(), null, 0);
					fn3.accept(cw);					
				}
			}
		}

		cw.visitEnd();
		return cw.toByteArray();
	}

	/*
	 * GETSTATIC edu/columbia/cs/psl/invivo/record/Instrumenter.instance :
	 * Ledu/columbia/cs/psl/invivo/record/Instrumenter; IFNONNULL L4 L0
	 * LINENUMBER 154 L0 NEW edu/columbia/cs/psl/invivo/record/Instrumenter DUP
	 * INVOKESPECIAL edu/columbia/cs/psl/invivo/record/Instrumenter.<init>()V
	 * PUTSTATIC edu/columbia/cs/psl/invivo/record/Instrumenter.instance :
	 * Ledu/columbia/cs/psl/invivo/record/Instrumenter; L1 GOTO L4 L2 LINENUMBER
	 * 156 L2 FRAME SAME1 java/lang/Exception ASTORE 0 L5 LINENUMBER 158 L5
	 * ACONST_NULL PUTSTATIC
	 * edu/columbia/cs/psl/invivo/record/Instrumenter.instance :
	 * Ledu/columbia/cs/psl/invivo/record/Instrumenter; L4 LINENUMBER 161 L4
	 * FRAME SAME
	 */
	private static void magic() {
		try {
			System.out.println("foo");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	private static void generateLogOfLogClass() {
		// if(1 ==1 ) //TODO let this run on some things
		// return;
		ClassWriter cv = new InstrumenterClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES, loader);
		// CheckClassAdapter cv = new CheckClassAdapter(cw);
		cv.visit(49, Opcodes.ACC_PUBLIC, Constants.LOG_DUMP_CLASS, null, "java/lang/Object", null);
		cv.visitSource(null, null);

		MethodVisitor mv = cv.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, "<clinit>", "()V", null, null);
		GeneratorAdapter mvz = new GeneratorAdapter(mv, Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "<clinit>", "()V");
		mvz.visitCode();

//		for (String clazz : methodCalls.keySet()) {
//			if (methodCalls.get(clazz).size() == 0)
//				continue;
//			mvz.visitFieldInsn(Opcodes.GETSTATIC, Constants.LOG_DUMP_CLASS, "log", Type.getDescriptor(HashMap.class));
//			mvz.visitLdcInsn(clazz);
//			mvz.visitTypeInsn(Opcodes.NEW, clazz + Constants.LOG_CLASS_SUFFIX);
//			mvz.visitInsn(Opcodes.DUP);
//			mvz.visitMethodInsn(Opcodes.INVOKESPECIAL, clazz + Constants.LOG_CLASS_SUFFIX, "<init>", "()V");
//			mvz.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/HashMap", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
//			mvz.visitInsn(Opcodes.POP);
////			mvz.visitFieldInsn(Opcodes.PUTSTATIC, Constants.LOG_DUMP_CLASS, clazz.replace("/", "_"), "L" + clazz + Constants.LOG_CLASS_SUFFIX + ";");
//		}
		mvz.returnValue();
		mvz.visitMaxs(0, 0);
		mvz.visitEnd();

		/*
		 * We need to generate a constructor for the class that does nothing, to
		 * allow for serialization/deserialization
		 */
		{
			mv = cv.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
			mv.visitCode();
			mv.visitVarInsn(Opcodes.ALOAD, 0);
			mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
			mv.visitInsn(Opcodes.RETURN);
			mv.visitMaxs(1, 1);
			mv.visitEnd();
		}
		{
			mv = cv.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "clearLog", "()V", null, null);
			mv.visitCode();
//			for (String clazz : methodCalls.keySet()) {
//				if (methodCalls.get(clazz).size() == 0)
//					continue;
//				mvz.visitFieldInsn(Opcodes.GETSTATIC, Constants.LOG_DUMP_CLASS, "log", Type.getDescriptor(HashMap.class));
//				mv.visitLdcInsn(clazz);
//				mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/HashMap", "get", "(Ljava/lang/Object;)Ljava/lang/Object;");
//				mv.visitMethodInsn(Opcodes.INVOKESTATIC, clazz + Constants.LOG_CLASS_SUFFIX, "clearLog", "()V");
//			}
			mv.visitInsn(Opcodes.RETURN);
			mv.visitMaxs(0, 0);
			mv.visitEnd();
		}

		FieldNode fn = new FieldNode(Opcodes.ASM4, Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC,"log", Type.getDescriptor(HashMap.class), null, null);
		fn.accept(cv);
		/*
		 * Create the variable
		 */
//		for (String clazz : methodCalls.keySet()) {
//			if (methodCalls.get(clazz).size() == 0)
//				continue;
//			int opcode = Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC;
//			FieldNode fn = new FieldNode(Opcodes.ASM4, opcode, clazz.replace("/", "_"), "L" + clazz + Constants.LOG_CLASS_SUFFIX + ";", null, null);
//			fn.accept(cv);
//		}
		cv.visitEnd();

		try {
			File outputDir = new File(rootOutputDir + File.separator + "bin" + File.separator
					+ Constants.LOG_DUMP_CLASS.substring(0, Constants.LOG_DUMP_CLASS.lastIndexOf("/")));
			outputDir.mkdirs();
			FileOutputStream fos = new FileOutputStream(outputDir + Constants.LOG_DUMP_CLASS.substring(Constants.LOG_DUMP_CLASS.lastIndexOf("/"))
					+ ".class");
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			bos.write(cv.toByteArray());
			bos.writeTo(fos);
			fos.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	private static byte[] instrumentClass(InputStream is) {
		try {
			ClassReader cr = new ClassReader(is);
			ClassWriter cw = new InstrumenterClassWriter(cr, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES, loader);
			NonDeterministicLoggingClassVisitor cv = new NonDeterministicLoggingClassVisitor(Opcodes.ASM4, cw);

			cr.accept(cv, ClassReader.EXPAND_FRAMES);
			methodCalls.put(cv.getClassName(), cv.getLoggedMethodCalls());
			lastInstrumentedClass = cv.getClassName();
			byte[] out = cw.toByteArray();
			// ClassReader cr2 = new ClassReader(out);
			// cr2.accept(new CheckClassAdapter(new ClassWriter(0)), 0);
			return out;
		} catch (Exception ex) {
			logger.error("Exception processing class:", ex);
			return null;
		}
	}

	public static void main(String[] args) {
		if (args.length <= 1) {
			System.err
					.println("Usage: java edu.columbia.cs.psl.invivo.record.Instrumenter [outputFolder] [inputfolder] [classpath]\n Paths can be classes, directories, or jar files");
			System.exit(-1);
		}
		String outputFolder = args[0];
		rootOutputDir = new File(outputFolder);
		if (!rootOutputDir.exists())
			rootOutputDir.mkdir();
		String inputFolder = args[1];
		// Setup the class loader
		URL[] urls = new URL[args.length - 2];
		for (int i = 2; i < args.length; i++) {
			File f = new File(args[i]);
			if (!f.exists()) {
				System.err.println("Unable to read path " + args[i]);
				System.exit(-1);
			}
			if (f.isDirectory() && !f.getAbsolutePath().endsWith("/"))
				f = new File(f.getAbsolutePath() + "/");
			try {
				urls[i - 2] = f.getCanonicalFile().toURI().toURL();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		loader = new URLClassLoader(urls, Instrumenter.class.getClassLoader());

		for (pass_number = 0; pass_number < NUM_PASSES; pass_number++) // Do
																		// each
																		// pass.
		{
			File f = new File(inputFolder);
			if (!f.exists()) {
				System.err.println("Unable to read path " + inputFolder);
				System.exit(-1);
			}
			if (f.isDirectory())
				processDirectory(f, rootOutputDir, true);
			else if (inputFolder.endsWith(".jar"))
				processJar(f, rootOutputDir);
			else if (inputFolder.endsWith(".class"))
				try {
					processClass(f.getName(), new FileInputStream(f), rootOutputDir);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			else {
				System.err.println("Unknown type for path " + inputFolder);
				System.exit(-1);
			}
			finishedPass();
		}
		// }

	}

	private static void processClass(String name, InputStream is, File outputDir) {
		switch (pass_number) {
		case PASS_ANALYZE:
			analyzeClass(is);
			break;
		case PASS_OUTPUT:
			try {
				{
					FileOutputStream fos = new FileOutputStream(outputDir.getPath() + File.separator + name);
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					bos.write(instrumentClass(is));
					bos.writeTo(fos);
					fos.close();
				}
				{
					File parentDir = new File(rootOutputDir + File.separator + "bin" + File.separator + lastInstrumentedClass).getParentFile();
					if (!parentDir.exists())
						parentDir.mkdirs();
					FileOutputStream fos = new FileOutputStream(rootOutputDir + File.separator + "bin" + File.separator + lastInstrumentedClass
							+ Constants.LOG_CLASS_SUFFIX + ".class");
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					bos.write(generateLogClass());
					bos.writeTo(fos);
					fos.close();
				}

			} catch (Exception ex) {
				ex.printStackTrace();
				System.exit(-1);
			}
		}
	}

	private static byte[] generateLogClass() {
		return generateLogClass(lastInstrumentedClass);
	}

	private static void processDirectory(File f, File parentOutputDir, boolean isFirstLevel) {
		File thisOutputDir;
		if (isFirstLevel) {
			thisOutputDir = parentOutputDir;
		} else {
			thisOutputDir = new File(parentOutputDir.getAbsolutePath() + File.separator + f.getName());
			if (pass_number == PASS_OUTPUT)
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
			else if (fi.getName().endsWith(".jar"))
				processJar(fi, thisOutputDir);
			else if (pass_number == PASS_OUTPUT) {
				File dest = new File(thisOutputDir.getPath() + File.separator + fi.getName());
				FileChannel source = null;
				FileChannel destination = null;

				try {
					source = new FileInputStream(fi).getChannel();
					destination = new FileOutputStream(dest).getChannel();
					destination.transferFrom(source, 0, source.size());
				} catch (Exception ex) {
					logger.error("Unable to copy file " + fi, ex);
					System.exit(-1);
				} finally {
					if (source != null) {
						try {
							source.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					if (destination != null) {
						try {
							destination.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}

			}
		}

	}

	private static void processJar(File f, File outputDir) {
		try {
			JarFile jar = new JarFile(f);
			JarOutputStream jos = null;
			if (pass_number == PASS_OUTPUT)
				jos = new JarOutputStream(new FileOutputStream(outputDir.getPath() + File.separator + f.getName()));
			Enumeration<JarEntry> entries = jar.entries();
			while (entries.hasMoreElements()) {
				JarEntry e = entries.nextElement();
				switch (pass_number) {
				case PASS_ANALYZE:
					if (e.getName().endsWith(".class")) {
						analyzeClass(jar.getInputStream(e));
					}
					break;
				case PASS_OUTPUT:
					if (e.getName().endsWith(".class") && !e.getName().startsWith("java") && !e.getName().startsWith("org/objenesis")
							&& !e.getName().startsWith("com/thoughtworks/xstream/") && !e.getName().startsWith("com/rits/cloning")
							&& !e.getName().startsWith("com/apple/java/Application")) {
						{
							JarEntry outEntry = new JarEntry(e.getName());
							jos.putNextEntry(outEntry);
							byte[] clazz = instrumentClass(jar.getInputStream(e));
							jos.write(clazz);
							jos.closeEntry();
						}
						{
							// JarEntry outEntry = new
							// JarEntry(e.getName().replace(".class",
							// Constants.LOG_CLASS_SUFFIX +".class"));
							// jos.putNextEntry(outEntry);
							File parentDir = new File(rootOutputDir + File.separator + "bin" + File.separator + e.getName());
							if (!parentDir.getParentFile().exists())
								parentDir.getParentFile().mkdirs();
							// System.out.println(e.getName());
							FileOutputStream fos = new FileOutputStream(rootOutputDir + File.separator + "bin" + File.separator
									+ e.getName().replace(".class", "") + Constants.LOG_CLASS_SUFFIX + ".class");
							ByteArrayOutputStream bos = new ByteArrayOutputStream();
							bos.write(generateLogClass());
							bos.writeTo(fos);
							fos.close();
							// jos.write(clazz);
							// jos.closeEntry();
						}

					} else {
						JarEntry outEntry = new JarEntry(e.getName());
						if (e.isDirectory()) {
							jos.putNextEntry(outEntry);
							jos.closeEntry();
						} else if (e.getName().startsWith("META-INF") && (e.getName().endsWith(".SF") || e.getName().endsWith(".RSA"))) {
							// don't copy this
						} else if (e.getName().equals("META-INF/MANIFEST.MF")) {
							String newManifest = "";
							Scanner s = new Scanner(jar.getInputStream(e));
							jos.putNextEntry(outEntry);

							String curPair = "";
							while (s.hasNextLine()) {
								String line = s.nextLine();
								if (line.equals("")) {
									curPair += "\n";
									if (!curPair.contains("SHA1-Digest:"))
										jos.write(curPair.getBytes());
									curPair = "";
								} else {
									curPair += line + "\n";
								}
							}
							jos.write("\n".getBytes());
							jos.closeEntry();
						} else {
							jos.putNextEntry(outEntry);
							InputStream is = jar.getInputStream(e);
							byte[] buffer = new byte[1024];
							while (true) {
								int count = is.read(buffer);
								if (count == -1)
									break;
								jos.write(buffer, 0, count);
							}
							jos.closeEntry();
						}
					}
				}

			}
			if (pass_number == PASS_OUTPUT) {
				jos.close();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Unable to process jar" + f, e);
			System.exit(-1);
		}

	}
}
