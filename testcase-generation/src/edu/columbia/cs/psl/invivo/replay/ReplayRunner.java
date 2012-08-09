package edu.columbia.cs.psl.invivo.replay;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import org.objectweb.asm.Type;

import com.thoughtworks.xstream.XStream;

import edu.columbia.cs.psl.invivo.record.CloningUtils;
import edu.columbia.cs.psl.invivo.record.Constants;
import edu.columbia.cs.psl.invivo.record.ExportedLog;
import edu.columbia.cs.psl.invivo.record.ExportedSerializableLog;
import edu.columbia.cs.psl.invivo.record.Log;
import edu.columbia.cs.psl.invivo.record.xstream.StaticReflectionProvider;

public class ReplayRunner {
	public static String[] logFiles;
	public static String[] serializableLogFiles;

	private static int nextLog = 0;
	private static int nextSerializableLog = 0;

	public static void loadNextLog(String logClass) {
		try {
			Log.logLock.lock();
			_loadNextLog(logClass);
			Log.logLock.unlock();
		} catch (Exception exi) {
			exi.printStackTrace();
		}
	}

	private static void _loadNextLog(String logClass) {
		try {
			if (logClass.contains("Serializable")) {
				ObjectInputStream is = new ObjectInputStream(new FileInputStream(serializableLogFiles[nextSerializableLog]));
				ExportedSerializableLog el = (ExportedSerializableLog) is.readObject();
				nextSerializableLog++;
			} else {
				XStream xstream = new XStream(new StaticReflectionProvider());
				Object o = xstream.fromXML(new File(logFiles[nextLog]));
				nextLog++;
			}
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
		serializableLogFiles = new String[args.length - 1];
		int class_args = args.length;
		int nLogs = 0;
		int nSerializableLogs = 0;

		for (int i = 1; i < args.length; i++) {
			if (!args[i].equals("class_args"))
				if (args[i].contains("_serializable_")) {
					serializableLogFiles[nSerializableLogs] = args[i];
					nSerializableLogs++;
				} else {
					logFiles[nLogs] = args[i];
					nLogs++;
				}
			else {
				class_args = i + 1;
				break;
			}
		}

		System.out.println("Available logs: " + Arrays.deepToString(logFiles));
		_loadNextLog(Type.getDescriptor(ExportedLog.class));
		_loadNextLog(Type.getDescriptor(ExportedSerializableLog.class));
		Class<?> toRun;
		try {
			toRun = Class.forName(mainClass);
			Method meth = toRun.getMethod("main", String[].class);
			String[] params = new String[args.length - class_args];
			if (class_args < args.length)
				System.arraycopy(args, class_args, params, 0, params.length);
			meth.invoke(null, new Object[] { params });
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
