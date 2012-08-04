package edu.columbia.cs.psl.invivo.record;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;

import com.thoughtworks.xstream.XStream;

import edu.columbia.cs.psl.invivo.record.xstream.StaticReflectionProvider;

public class WallaceExportRunner extends Thread {
	@Override
	public void run() {
		System.err.println("Export runner started");
		while(1 == 1)
		{
//			System.out.println("Exporting");
//			System.err.println("Exporting");
			export();
//			System.err.println("Exported");
			try {
//				Thread.sleep(60000); //1 minute
//				Thread.sleep(300000); //5 minutes
//				Thread.sleep(10000); //10 seconds
				Thread.sleep(5000); //5 seconds
//				Thread.sleep(1000); //1 seconds
			} catch (InterruptedException e) {

			}
		}
	}
	public WallaceExportRunner() {
		setDaemon(true);
	}
	public void export() {
		try {
//			System.err.println("Getting log class");
			Class<?> logger = Class.forName(Constants.LOG_DUMP_CLASS.replace("/", "."));
//			System.err.println("Have log class");
			XStream xstream = new XStream(new StaticReflectionProvider());
			CloningUtils.exportLock.writeLock().lock();
			String xml = xstream.toXML(logger.newInstance());
			logger.getMethod("clearLog").invoke(null);
			CloningUtils.exportLock.writeLock().unlock();
			File output = new File("wallace_" + System.currentTimeMillis() + ".log");
			FileWriter fw = new FileWriter(output);
			fw.write(xml);
			fw.close();

		} catch (Exception exi) {
			exi.printStackTrace();
		}
	}
}
