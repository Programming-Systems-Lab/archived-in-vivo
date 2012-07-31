package edu.columbia.cs.psl.invivo.replay;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import com.thoughtworks.xstream.XStream;

import edu.columbia.cs.psl.invivo.record.CloningUtils;
import edu.columbia.cs.psl.invivo.record.Constants;
import edu.columbia.cs.psl.invivo.record.xstream.StaticReflectionProvider;

public class ReplayRunner {
	public static String[]	logFiles;
	private static int		nextLog	= 0;

	public static void loadNextLog() {
		try {
			CloningUtils.exportLock.writeLock().lock();
			_loadNextLog();
			CloningUtils.exportLock.writeLock().unlock();
		} catch (Exception exi) {
			exi.printStackTrace();
		}
	}

	private static void _loadNextLog() {
		try {
			XStream xstream = new XStream(new StaticReflectionProvider());
			Object o = xstream.fromXML(new File(logFiles[nextLog]));
			nextLog++;
		} catch (Exception exi) {
			exi.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		if (args.length < 2) {
			System.err.println("Usage: ReplayRunner <mainClass> log [log2...logN] class_args [arg1...argM]");
			System.exit(-1);
		}
		String mainClass = args[0];
		logFiles = new String[args.length - 1];
		int class_args = args.length;
		for (int i = 1; i < args.length; i++) {
			if (!args[i].equals("class_args"))
				logFiles[i - 1] = args[i];
			else {
				class_args = i + 1;
				break;
			}
		}
		
		System.out.println("Available logs: " + Arrays.deepToString(logFiles));
		_loadNextLog();

		Class<?> toRun;
		try {
			toRun = Class.forName(mainClass);
			Method meth = toRun.getMethod("main", String[].class);
			String[] params = new String[args.length - class_args];
			if (class_args < args.length)
				System.arraycopy(args, class_args, params, 0, params.length);
		    meth.invoke(null, new Object[]{ params });
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
