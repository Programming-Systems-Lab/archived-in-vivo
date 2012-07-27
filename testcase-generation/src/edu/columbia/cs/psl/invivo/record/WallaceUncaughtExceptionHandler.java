package edu.columbia.cs.psl.invivo.record;

import java.io.File;
import java.io.FileWriter;

import com.thoughtworks.xstream.XStream;

import edu.columbia.cs.psl.invivo.record.xstream.StaticReflectionProvider;

public class WallaceUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler{

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		try{
			System.err.println("Wallace caught an exception");
			e.printStackTrace();
		Class logger = Class.forName(Constants.LOG_DUMP_CLASS.replace("/", "."));
			XStream xstream = new XStream(new StaticReflectionProvider());
			String xml = xstream.toXML(logger.newInstance());
			File output = new File("wallace.log");
			FileWriter fw = new FileWriter(output);
			fw.write(xml);
			fw.close();
			}
		catch(Exception exi)
		{
			exi.printStackTrace();
		}
	}

}
