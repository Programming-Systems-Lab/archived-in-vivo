package edu.columbia.cs.psl.invivo.record;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class ExportedSerializableLog implements Serializable {
	public static Serializable[]	aLog	= new Serializable[Constants.DEFAULT_LOG_SIZE];

	public static int				aLog_fill;
	public static int				aLog_replayIndex;

	public static void clearLog() {
		aLog = new Serializable[Constants.DEFAULT_LOG_SIZE];
		aLog_fill = 0;
	}

	private void writeObject(ObjectOutputStream oos) throws IOException {
		oos.defaultWriteObject();
		oos.writeInt(aLog_fill);
		oos.writeObject(aLog);
	}

	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
		ois.defaultReadObject();
		aLog_fill = ois.readInt();
		aLog = (Serializable[]) ois.readObject();
	}
}