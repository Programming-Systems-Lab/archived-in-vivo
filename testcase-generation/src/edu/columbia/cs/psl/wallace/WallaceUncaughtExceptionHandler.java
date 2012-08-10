package edu.columbia.cs.psl.wallace;

import java.io.File;
import java.io.FileWriter;

import com.thoughtworks.xstream.XStream;

import edu.columbia.cs.psl.wallace.xstream.StaticReflectionProvider;

public class WallaceUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler{

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		try{
			System.err.println("Wallace caught an exception");
			e.printStackTrace();
			System.err.println("Writing log");
			WallaceExportRunner.export();
			WallaceExportRunner.exportSerializable();
			}
		catch(Exception exi)
		{
			exi.printStackTrace();
		}
	}

}
