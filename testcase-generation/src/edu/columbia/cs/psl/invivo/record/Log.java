package edu.columbia.cs.psl.invivo.record;

public class Log {
	public static Object[] aLog = new Object[Constants.DEFAULT_LOG_SIZE];
	
	public static Object lock = new Object();
	public static int logsize = 0;
	public static int aLog_fill;
	public static void growaLog()
	{
		Object[] newA = new Object[(int) (aLog.length * Constants.LOG_GROWTH_RATE)];
		System.arraycopy(aLog, 0, newA, 0, aLog.length);
		aLog = newA;
	}
	
	public static void clearLog() {
//		System.err.println("start cl");
		logsize = 0;
		aLog = new Object[Constants.DEFAULT_LOG_SIZE];
	
		
		aLog_fill = 0;
		
//		System.err.println("starting gc");
//		System.gc();
//		System.err.println("Fin gc");
	}
}
