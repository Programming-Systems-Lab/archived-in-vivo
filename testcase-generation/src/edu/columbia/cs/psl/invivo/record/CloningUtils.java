package edu.columbia.cs.psl.invivo.record;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.nio.channels.Channel;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.rits.cloning.Cloner;

public class CloningUtils {
	public static boolean				CATCH_ALL_ERRORS	= true;
	private static Cloner				cloner				= new Cloner();
	public static ReadWriteLock		exportLock			= new ReentrantReadWriteLock();
	private static HashSet<Class<?>>	moreIgnoredImmutables;
	private static BufferedWriter		log;
	private static WallaceExportRunner exporter = new WallaceExportRunner();
	static {
		moreIgnoredImmutables = new HashSet<Class<?>>();
		moreIgnoredImmutables.add(ClassLoader.class);
		moreIgnoredImmutables.add(Thread.class);
		moreIgnoredImmutables.add(URI.class);
		moreIgnoredImmutables.add(File.class);
		moreIgnoredImmutables.add(ZipFile.class);
		moreIgnoredImmutables.add(ZipEntry.class);
		moreIgnoredImmutables.add(Inflater.class);
		moreIgnoredImmutables.add(InputStream.class);
		moreIgnoredImmutables.add(OutputStream.class);
		moreIgnoredImmutables.add(Deflater.class);
		moreIgnoredImmutables.add(Socket.class);
		moreIgnoredImmutables.add(ServerSocket.class);
		moreIgnoredImmutables.add(Channel.class);
		moreIgnoredImmutables.add(Closeable.class);

		cloner.setExtraNullInsteadOfClone(moreIgnoredImmutables);
		cloner.setExtraImmutables(moreIgnoredImmutables);
		
		exporter.start();
		if (CATCH_ALL_ERRORS) {
			Thread.setDefaultUncaughtExceptionHandler(new WallaceUncaughtExceptionHandler());
		}
		try {
			File f = new File("cloneLog");
			if (f.exists())
				f.delete();
			log = new BufferedWriter(new FileWriter("cloneLog"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static final <T> T clone(T obj, String debug) {
		if (obj != null && !obj.getClass().isArray() && !obj.getClass().equals(Object.class) && !obj.getClass().equals(Thread.class)

		&& !obj.getClass().getName().contains("ClassLoader") && !obj.getClass().getName().contains("InputStream")
				&& !obj.getClass().getName().contains("JarURLConnection")) {
			try {
				log.append(debug + "\n");
				log.flush();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return cloner.deepClone(obj);
		}
		return null;
	}

	public static IdentityHashMap<Object, Object>	cloneCache	= new IdentityHashMap<Object, Object>();	;
	
	public static void exportLog() {
		
	}

}
