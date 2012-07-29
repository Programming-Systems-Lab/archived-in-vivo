package edu.columbia.cs.psl.invivo.record;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;

import com.thoughtworks.xstream.XStream;

import edu.columbia.cs.psl.invivo.record.xstream.StaticReflectionProvider;

public class WallaceExportRunner extends Thread {
	@Override
	public void run() {
		while(1 == 1)
		{
			export();
			try {
				Thread.sleep(300000); //5 minutes
//				Thread.sleep(10000); //10 seconds
			} catch (InterruptedException e) {

			}
		}
	}
	public WallaceExportRunner() {
		setDaemon(true);
	}
	public void export() {
		try {
			Class<?> logger = Class.forName(Constants.LOG_DUMP_CLASS.replace("/", "."));
//			logger.getMethod("initLogs").invoke(null);
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
