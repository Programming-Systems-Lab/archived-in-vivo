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
import java.net.URL;
import java.nio.channels.Channel;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.IdentityHashMap;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import com.rits.cloning.Cloner;
import com.thoughtworks.xstream.XStream;

public class CloningUtils {
	public static boolean CATCH_ALL_ERRORS = true;
	private static 		Cloner cloner = new Cloner();

	private static HashSet<Class<?>> moreIgnoredImmutables;
	private static BufferedWriter log;
	static
	{
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
		if(CATCH_ALL_ERRORS)
		{
			Thread.setDefaultUncaughtExceptionHandler(new WallaceUncaughtExceptionHandler());
		}
		try {
			File f = new File("cloneLog");
			if(f.exists())
				f.delete();
			log = new BufferedWriter(new FileWriter("cloneLog"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static final <T> T clone(T obj, String debug)
	{
//		System.out.println(Thread.currentThread().getId());
		if(obj != null && !obj.getClass().isArray() && ! obj.getClass().equals(Object.class) && !obj.getClass().equals(Thread.class)
				
				&& ! obj.getClass().getName().contains("ClassLoader") && ! obj.getClass().getName().contains("InputStream")
				&& !obj.getClass().getName().contains("JarURLConnection"))
		{
//			cloner.setDumpClonedClasses(true);
			try{
			log.append(debug+"\n");
			log.flush();
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
				return cloner.deepClone(obj);			

//			System.out.println(obj.getClass());
//			Objenesis o = new ObjenesisStd();
//			o.newInstance(obj.getClass());
//		System.out.println("!!!Going to clone " + obj.getClass());

		}
//		synchronized (cloner) {
//		}
		return null;
//		return SerializationUtils.clone(obj);
//		Ubiquity u = new Ubiquity();
//		return (T) u.map(obj, obj.getClass());
//		return (T) xs.fromXML(xs.toXML(obj));
	}
	public static IdentityHashMap<Object, Object> cloneCache = new IdentityHashMap<Object, Object>();;
}
