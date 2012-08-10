package edu.columbia.cs.psl.wallace;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;

import com.thoughtworks.xstream.XStream;

import edu.columbia.cs.psl.wallace.xstream.StaticReflectionProvider;

public class WallaceExportRunner extends Thread {
	// static Class<?> logger;
	static {
		// try {
		// // System.err.println("Loading log class");
		// logger = Class.forName(Constants.LOG_DUMP_CLASS.replace("/", "."));
		// // System.err.println("Loaded");
		// } catch (ClassNotFoundException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}

	@Override
	public void run() {
		// System.err.println("Export runner started");
		while (1 == 1) {
			// System.out.println("Exporting");
			// System.err.println("Exporting");
			// export(); //TODO uncomment
			// System.err.println("Exported");
			try {
				// Thread.sleep(60000); //1 minute
				// Thread.sleep(300000); //5 minutes
				Thread.sleep(10000); // 10 seconds
				// Thread.sleep(5000); //5 seconds
				// Thread.sleep(1000); //1 seconds
				// System.out.println("Waking up checking flag");
				if (shouldExport == 1)
					export();
				if (shouldExportSerializable == 1)
					exportSerializable();
				if (shouldExport == 1)
					export();

			} catch (InterruptedException e) {
				if (shouldExport == 1)
					export();
				if (shouldExportSerializable == 1)
					exportSerializable();
				if (shouldExport == 1)
					export();
			}
		}
	}

	static WallaceExportRunner inst = new WallaceExportRunner();

	public WallaceExportRunner() {
		setDaemon(true);
		setPriority(Thread.MAX_PRIORITY);
	}

	private static ExportedLog log = new ExportedLog();

	public static void export() {
		shouldExport = 0;
		try {
			XStream xstream = new XStream(new StaticReflectionProvider());
			String xml = "";
			// System.out.println("Waiting for the lock");
			Log.logLock.lock();
			ExportedLog.aLog = Log.aLog;
			ExportedLog.aLog_owners = Log.aLog_owners;
			ExportedLog.aLog_fill = Log.aLog_fill;
			Log.logsize = 0;
			Log.aLog = new Object[Constants.DEFAULT_LOG_SIZE];
			Log.aLog_fill = 0;
			Log.logLock.unlock();
//			System.err.println("Serializing");
			try {
				xml = xstream.toXML(log);
			} catch (Exception ex) {
				System.err.println("NPE" + ex.getMessage());
			}
			// System.err.println("Clearing");
			ExportedLog.clearLog();
			// System.err.println("Cleared");

			// CloningUtils.exportLock.writeLock().unlock();
			File output = new File("wallace_" + System.currentTimeMillis() + ".log");
			FileWriter fw = new FileWriter(output);
			fw.write(xml);
			fw.close();
			// synchronized (Log.lock) {
			// Log.lock.notifyAll();
			// }
			// synchronized (Log.lock) {
			// Log.lock.notifyAll();
			// }

		} catch (Exception exi) {
			// System.err.println(exi.getMessage());
		}
		shouldExport = -1;
	}

	private static ExportedSerializableLog logS = new ExportedSerializableLog();

	public static void exportSerializable() {
		shouldExportSerializable = 0;
		try {

			Log.logLock.lock();
			{
				ExportedSerializableLog.aLog = SerializableLog.aLog;
				ExportedSerializableLog.aLog_fill = SerializableLog.aLog_fill;
				ExportedSerializableLog.bLog = SerializableLog.bLog;
				ExportedSerializableLog.cLog = SerializableLog.cLog;
				ExportedSerializableLog.dLog = SerializableLog.dLog;
				ExportedSerializableLog.iLog = SerializableLog.iLog;
				ExportedSerializableLog.fLog = SerializableLog.fLog;
				ExportedSerializableLog.jLog = SerializableLog.jLog;
				ExportedSerializableLog.zLog = SerializableLog.zLog;
				ExportedSerializableLog.sLog = SerializableLog.sLog;

				ExportedSerializableLog.bLog_fill = SerializableLog.bLog_fill;
				ExportedSerializableLog.cLog_fill = SerializableLog.cLog_fill;
				ExportedSerializableLog.dLog_fill = SerializableLog.dLog_fill;
				ExportedSerializableLog.iLog_fill = SerializableLog.iLog_fill;
				ExportedSerializableLog.fLog_fill = SerializableLog.fLog_fill;
				ExportedSerializableLog.jLog_fill = SerializableLog.jLog_fill;
				ExportedSerializableLog.zLog_fill = SerializableLog.zLog_fill;
				ExportedSerializableLog.sLog_fill = SerializableLog.sLog_fill;

				ExportedSerializableLog.aLog_owners = SerializableLog.aLog_owners;
				ExportedSerializableLog.iLog_owners = SerializableLog.iLog_owners;
				ExportedSerializableLog.jLog_owners = SerializableLog.jLog_owners;
				ExportedSerializableLog.fLog_owners = SerializableLog.fLog_owners;
				ExportedSerializableLog.dLog_owners = SerializableLog.dLog_owners;
				ExportedSerializableLog.bLog_owners = SerializableLog.bLog_owners;
				ExportedSerializableLog.zLog_owners = SerializableLog.zLog_owners;
				ExportedSerializableLog.cLog_owners = SerializableLog.cLog_owners;
				ExportedSerializableLog.sLog_owners = SerializableLog.sLog_owners;

				SerializableLog.aLog = new Object[Constants.DEFAULT_LOG_SIZE];
				SerializableLog.iLog = new int[Constants.DEFAULT_LOG_SIZE];
				SerializableLog.jLog = new long[Constants.DEFAULT_LOG_SIZE];
				SerializableLog.fLog = new float[Constants.DEFAULT_LOG_SIZE];
				SerializableLog.dLog = new double[Constants.DEFAULT_LOG_SIZE];
				SerializableLog.bLog = new byte[Constants.DEFAULT_LOG_SIZE];
				SerializableLog.zLog = new boolean[Constants.DEFAULT_LOG_SIZE];
				SerializableLog.cLog = new char[Constants.DEFAULT_LOG_SIZE];
				SerializableLog.sLog = new short[Constants.DEFAULT_LOG_SIZE];
				SerializableLog.aLog_owners = new String[Constants.DEFAULT_LOG_SIZE];
				SerializableLog.iLog_owners = new String[Constants.DEFAULT_LOG_SIZE];
				SerializableLog.jLog_owners = new String[Constants.DEFAULT_LOG_SIZE];
				SerializableLog.fLog_owners = new String[Constants.DEFAULT_LOG_SIZE];
				SerializableLog.dLog_owners = new String[Constants.DEFAULT_LOG_SIZE];
				SerializableLog.bLog_owners = new String[Constants.DEFAULT_LOG_SIZE];
				SerializableLog.zLog_owners = new String[Constants.DEFAULT_LOG_SIZE];
				SerializableLog.cLog_owners = new String[Constants.DEFAULT_LOG_SIZE];
				SerializableLog.sLog_owners = new String[Constants.DEFAULT_LOG_SIZE];
				SerializableLog.logsize = 0;
				SerializableLog.iLog_fill = 0;
				SerializableLog.jLog_fill = 0;
				SerializableLog.fLog_fill = 0;
				SerializableLog.dLog_fill = 0;
				SerializableLog.bLog_fill = 0;
				SerializableLog.zLog_fill = 0;
				SerializableLog.cLog_fill = 0;
				SerializableLog.sLog_fill = 0;
				SerializableLog.aLog_fill = 0;
			}
			Log.logLock.unlock();
			// System.err.println("Serializing serializable");
			File output = new File("wallace_serializable_" + System.currentTimeMillis() + ".log");

			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(output));
			oos.writeObject(logS);
			oos.flush();
			oos.close();
			// System.err.println("Clearing serializable");
			ExportedLog.clearLog();
			// System.err.println("Cleared serializable");
			// System.out.println("Notifying; " + Log.logsize
			// +";"+SerializableLog.logsize);
			// synchronized (Log.lock) {
			// Log.lock.notifyAll();
			// }
			// synchronized (Log.lock) {
			// Log.lock.notifyAll();
			// }
		} catch (Exception exi) {
			// System.err.println(exi.getMessage());
		}
		shouldExportSerializable = -1;
	}

	private static int shouldExport = -1;
	private static int shouldExportSerializable = -1;

	public static void _exportSerializable() {
		if (shouldExportSerializable == -1) {
			// System.out.println("Flagged shouldexport serializble");
			Thread.yield();
			shouldExportSerializable = 1;
			inst.interrupt();
		}
	}

	public static void _export() {
		if (shouldExport == -1) {
			Thread.yield();
			shouldExport = 1;
			inst.interrupt();
		}
	}

}
