package edu.columbia.cs.psl.invivo.replay;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
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
			System.err.println("Usage: ReplayRunner <mainClass> log [log2...logN]");
			System.exit(-1);
		}
		String mainClass = args[0];
		logFiles = new String[args.length - 1];
		for (int i = 1; i < args.length; i++)
			logFiles[i - 1] = args[i];
		System.out.println("Available logs: " + Arrays.deepToString(logFiles));
		_loadNextLog();

		Class<?> toRun;
		try {
			toRun = Class.forName(mainClass);
			toRun.getMethod("main", new Class<?>[] { args.getClass() }).invoke(null, new Object[] { new String[0] });
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
