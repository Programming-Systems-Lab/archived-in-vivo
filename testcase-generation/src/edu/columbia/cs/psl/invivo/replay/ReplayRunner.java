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
	public static int		nextLog	= 0;

	public static void loadNextLog() {
		try {
			Class<?> logger = Class.forName(Constants.LOG_DUMP_CLASS.replace("/", "."));
			logger.getMethod("clearLog").invoke(null);
			XStream xstream = new XStream(new StaticReflectionProvider());
			CloningUtils.exportLock.writeLock().lock();
			Object o = xstream.fromXML(new File(logFiles[nextLog]));
			logger.getMethod("clearReplayIndices").invoke(null);
			CloningUtils.exportLock.writeLock().unlock();
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
		loadNextLog();

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
