package edu.columbia.cs.psl.invivo.record;

public class Log {
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
	public static int logsize = 0;
	public static int aLog_fill, iLog_fill, jLog_fill, fLog_fill, dLog_fill, bLog_fill, zLog_fill, cLog_fill, sLog_fill;
	public static void growaLog()
	{
		Object[] newA = new Object[(int) (aLog.length * Constants.LOG_GROWTH_RATE)];
		System.arraycopy(aLog, 0, newA, 0, aLog.length);
		aLog = newA;
	}
	public static void growiLog()
	{
		int[] newA = new int[(int) (aLog.length * Constants.LOG_GROWTH_RATE)];
		System.arraycopy(iLog, 0, newA, 0, iLog.length);
		iLog = newA;
	}
	public static void growjLog()
	{
		long[] newA = new long[(int) (aLog.length * Constants.LOG_GROWTH_RATE)];
		System.arraycopy(jLog, 0, newA, 0, jLog.length);
		jLog = newA;
	}
	public static void growfLog()
	{
		float[] newA = new float[(int) (aLog.length * Constants.LOG_GROWTH_RATE)];
		System.arraycopy(fLog, 0, newA, 0, fLog.length);
		fLog = newA;
	}
	public static void growdLog()
	{
		double[] newA = new double[(int) (aLog.length * Constants.LOG_GROWTH_RATE)];
		System.arraycopy(dLog, 0, newA, 0, dLog.length);
		dLog = newA;
	}
	public static void growbLog()
	{
		byte[] newA = new byte[(int) (aLog.length * Constants.LOG_GROWTH_RATE)];
		System.arraycopy(bLog, 0, newA, 0, bLog.length);
		bLog = newA;
	}
	public static void growzLog()
	{
		boolean[] newA = new boolean[(int) (aLog.length * Constants.LOG_GROWTH_RATE)];
		System.arraycopy(zLog, 0, newA, 0, zLog.length);
		zLog = newA;
	}
	public static void growcLog()
	{
		char[] newA = new char[(int) (aLog.length * Constants.LOG_GROWTH_RATE)];
		System.arraycopy(cLog, 0, newA, 0, cLog.length);
		cLog = newA;
	}
	public static void growsLog()
	{
		short[] newA = new short[(int) (aLog.length * Constants.LOG_GROWTH_RATE)];
		System.arraycopy(sLog, 0, newA, 0, sLog.length);
		sLog = newA;
	}
	public static void clearLog() {
//		System.err.println("start cl");
		logsize = 0;
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
//		System.err.println("starting gc");
//		System.gc();
//		System.err.println("Fin gc");
	}
}
