package edu.columbia.cs.psl.invivo.record;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Log {
	public static Object[] aLog = new Object[Constants.DEFAULT_LOG_SIZE];
	public static String[] aLog_owners = new String[Constants.DEFAULT_LOG_SIZE];
	public static Lock logLock = new ReentrantLock();
//	public static Object lock = new Object();
	public static int logsize = 0;
	public static int aLog_fill;

	
	public static void clearLog() {
//		System.err.println("start cl");
		logsize = 0;
		aLog = new Object[Constants.DEFAULT_LOG_SIZE];
		aLog_owners = new String[Constants.DEFAULT_LOG_SIZE];
		
		aLog_fill = 0;
		
//		System.err.println("starting gc");
//		System.gc();
//		System.err.println("Fin gc");
	}
}
