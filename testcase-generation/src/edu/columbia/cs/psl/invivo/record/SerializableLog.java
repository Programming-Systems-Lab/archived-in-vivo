package edu.columbia.cs.psl.invivo.record;

import java.io.Serializable;

public class SerializableLog implements Serializable{

	private static final long	serialVersionUID	= 4627796984904522647L;
	public static Serializable[] aLog = new Serializable[Constants.DEFAULT_LOG_SIZE];
	public static int logsize = 0;
	public static int aLog_fill = 0;
	public static void growaLog()
	{
		Serializable[] newA = new Serializable[(int) (aLog.length * Constants.LOG_GROWTH_RATE)];
		System.arraycopy(aLog, 0, newA, 0, aLog.length);
		aLog = newA;
	}
	
	public static void clearLog() {
//		System.err.println("start cl");
		aLog = new Serializable[Constants.DEFAULT_LOG_SIZE];
		logsize = 0;
//		System.err.println("starting gc");
//		System.gc();
//		System.err.println("Fin gc");
	}
}
