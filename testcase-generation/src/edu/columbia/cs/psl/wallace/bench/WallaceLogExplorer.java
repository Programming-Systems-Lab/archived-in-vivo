package edu.columbia.cs.psl.wallace.bench;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

import edu.columbia.cs.psl.wallace.ExportedSerializableLog;

public class WallaceLogExplorer {
	public static void main(String[] args) throws Exception {
		File f = new File("instrumented/wallace_serializable_1344527362195.log");
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
		ExportedSerializableLog log = (ExportedSerializableLog) ois.readObject();
		Object[] alog = ExportedSerializableLog.aLog;
		
		char[] clog = ExportedSerializableLog.cLog;
		byte[] blog = ExportedSerializableLog.bLog;
		String[] ownersA = ExportedSerializableLog.aLog_owners;
		System.out.println(ExportedSerializableLog.aLog_fill);
		System.out.println(ExportedSerializableLog.cLog_fill);
		System.out.println(ExportedSerializableLog.dLog_fill);
	}
}
