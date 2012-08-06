package edu.columbia.cs.psl.invivo.record;

public class ExportedLog {
	public static Object[] aLog = new Object[Constants.DEFAULT_LOG_SIZE];
	public static int[] iLog = new int[Constants.DEFAULT_LOG_SIZE];
	public static long[] jLog = new long[Constants.DEFAULT_LOG_SIZE];
	public static float[] fLog = new float[Constants.DEFAULT_LOG_SIZE];
	public static double[] dLog = new double[Constants.DEFAULT_LOG_SIZE];
	public static byte[] bLog = new byte[Constants.DEFAULT_LOG_SIZE];
	public static boolean[] zLog = new boolean[Constants.DEFAULT_LOG_SIZE];
	public static char[] cLog = new char[Constants.DEFAULT_LOG_SIZE];
	public static short[] sLog = new short[Constants.DEFAULT_LOG_SIZE];
	public static Object lock = new Object();
	public static int aLog_fill, iLog_fill, jLog_fill, fLog_fill, dLog_fill, bLog_fill, zLog_fill, cLog_fill, sLog_fill;
	public static void clearLog() {
		aLog = new Object[Constants.DEFAULT_LOG_SIZE];
		iLog = new int[Constants.DEFAULT_LOG_SIZE];
		jLog = new long[Constants.DEFAULT_LOG_SIZE];
		fLog = new float[Constants.DEFAULT_LOG_SIZE];
		dLog = new double[Constants.DEFAULT_LOG_SIZE];
		bLog = new byte[Constants.DEFAULT_LOG_SIZE];
		zLog = new boolean[Constants.DEFAULT_LOG_SIZE];
		cLog = new char[Constants.DEFAULT_LOG_SIZE];
		sLog = new short[Constants.DEFAULT_LOG_SIZE];
		
		aLog_fill = 0;
		iLog_fill = 0;
		jLog_fill = 0;
		fLog_fill = 0;
		dLog_fill = 0;
		bLog_fill = 0;
		zLog_fill = 0;
		cLog_fill = 0;
		sLog_fill = 0;
	}
}
