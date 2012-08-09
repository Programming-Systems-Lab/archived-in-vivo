package edu.columbia.cs.psl.invivo.record;

public class ExportedLog {
	public static Object[] aLog = new Object[Constants.DEFAULT_LOG_SIZE];
	public static String[] aLog_owners = new String[Constants.DEFAULT_LOG_SIZE];

	public static int aLog_fill;
	public static int aLog_replayIndex;

	public static void clearLog() {
		aLog = new Object[Constants.DEFAULT_LOG_SIZE];
		aLog_owners =  new String[Constants.DEFAULT_LOG_SIZE];
		aLog_fill = 0;
	}
}
