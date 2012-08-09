package edu.columbia.cs.psl.invivo.record;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class ExportedSerializableLog implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1166783255069514273L;
	public static Object[]	aLog	= new Object[Constants.DEFAULT_LOG_SIZE];
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
	public static int aLog_replayIndex , iLog_replayIndex, jLog_replayIndex, fLog_replayIndex, dLog_replayIndex, bLog_replayIndex, zLog_replayIndex, cLog_replayIndex, sLog_replayIndex;


	public static void clearLog() {
		aLog = new Serializable[Constants.DEFAULT_LOG_SIZE];
		aLog_fill = 0;
	}

	private void writeObject(ObjectOutputStream oos) throws IOException {
		oos.defaultWriteObject();
		oos.writeInt(aLog_fill);
		oos.writeObject(aLog);
		oos.writeInt(iLog_fill);
		oos.writeObject(iLog);
		oos.writeInt(jLog_fill);
		oos.writeObject(jLog);
		oos.writeInt(fLog_fill);
		oos.writeObject(fLog);
		oos.writeInt(dLog_fill);
		oos.writeObject(dLog);
		oos.writeInt(bLog_fill);
		oos.writeObject(bLog);
		oos.writeInt(zLog_fill);
		oos.writeObject(zLog);
		oos.writeInt(cLog_fill);
		oos.writeObject(cLog);
		oos.writeInt(sLog_fill);
		oos.writeObject(sLog);
	}

	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
		ois.defaultReadObject();
		aLog_fill = ois.readInt();
		aLog = (Object[]) ois.readObject();
		iLog_fill = ois.readInt();
		iLog = (int[]) ois.readObject();
		jLog_fill = ois.readInt();
		jLog = (long[]) ois.readObject();
		fLog_fill = ois.readInt();
		fLog = (float[]) ois.readObject();
		dLog_fill = ois.readInt();
		dLog = (double[]) ois.readObject();
		bLog_fill = ois.readInt();
		bLog = (byte[]) ois.readObject();
		zLog_fill = ois.readInt();
		zLog = (boolean[]) ois.readObject();
		cLog_fill = ois.readInt();
		cLog = (char[]) ois.readObject();
		sLog_fill = ois.readInt();
		sLog = (short[]) ois.readObject();
	}
}