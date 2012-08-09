package edu.columbia.cs.psl.invivo.record;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;

import com.thoughtworks.xstream.XStream;

import edu.columbia.cs.psl.invivo.record.xstream.StaticReflectionProvider;

public class WallaceExportRunner extends Thread {
	//	static Class<?> logger;
	static {
		//		try {
		////			System.err.println("Loading log class");
		//			logger = Class.forName(Constants.LOG_DUMP_CLASS.replace("/", "."));
		////			System.err.println("Loaded");
		//		} catch (ClassNotFoundException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}
	}

	@Override
	public void run() {
		//		System.err.println("Export runner started");
				while(1 == 1)
				{
		//			System.out.println("Exporting");
		//			System.err.println("Exporting");
		//			export(); //TODO uncomment
		//			System.err.println("Exported");
		try {
			//				Thread.sleep(60000); //1 minute
//			Thread.sleep(300000); //5 minutes
							Thread.sleep(10000); //10 seconds
			//				Thread.sleep(5000); //5 seconds
//							Thread.sleep(1000); //1 seconds
//							System.out.println("Waking up checking flag");
							if(shouldExport == 1)
								export();

		} catch (InterruptedException e) {
			if(shouldExport == 1)
				export();
		}
				}
	}
	static WallaceExportRunner inst = new WallaceExportRunner();
	public WallaceExportRunner() {
		setDaemon(true);
	}

	private static ExportedLog	log	= new ExportedLog();
	public static void export() {
		shouldExport = 0;
		try {
			XStream xstream = new XStream(new StaticReflectionProvider());
			String xml = "";
//			System.out.println("Waiting for the lock");
			synchronized (Log.lock) {
				ExportedLog.aLog = Log.aLog;
				ExportedLog.bLog = Log.bLog;
				ExportedLog.cLog = Log.cLog;
				ExportedLog.dLog = Log.dLog;
				ExportedLog.iLog = Log.iLog;
				ExportedLog.fLog = Log.fLog;
				ExportedLog.jLog = Log.jLog;
				ExportedLog.zLog = Log.zLog;
				ExportedLog.sLog = Log.sLog;
				
				ExportedLog.aLog_fill = Log.aLog_fill;
				ExportedLog.bLog_fill = Log.bLog_fill;
				ExportedLog.cLog_fill = Log.cLog_fill;
				ExportedLog.dLog_fill = Log.dLog_fill;
				ExportedLog.iLog_fill = Log.iLog_fill;
				ExportedLog.fLog_fill = Log.fLog_fill;
				ExportedLog.jLog_fill = Log.jLog_fill;
				ExportedLog.zLog_fill = Log.zLog_fill;
				ExportedLog.sLog_fill = Log.sLog_fill;
				Log.clearLog();
			}
				System.err.println("Serializing");
				try{
				xml = xstream.toXML(log);
				}
				catch(Exception ex)
				{
					System.err.println("NPE" + ex.getMessage());
				}
				System.err.println("Clearing");
				ExportedLog.clearLog();
				System.err.println("Cleared");

			//CloningUtils.exportLock.writeLock().unlock();
			File output = new File("wallace_" + System.currentTimeMillis() + ".log");
			FileWriter fw = new FileWriter(output);
			fw.write(xml);
			fw.close();

		} catch (Exception exi) {
//			System.err.println(exi.getMessage());
		}
		shouldExport = -1;
	}

	private static int	shouldExport	= -1;

	public static void _export() {
//		System.err.println("flag export");
		if(shouldExport == -1)
		{
			shouldExport = 1;
			inst.interrupt();
		}
//		if(inst.isAlive())
//			System.out.println("Alive still!");
//		else
//			System.out.println("Its dead");
	}

}
