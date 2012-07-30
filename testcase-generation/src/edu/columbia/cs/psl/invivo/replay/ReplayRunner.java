package edu.columbia.cs.psl.invivo.replay;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import com.thoughtworks.xstream.XStream;

import edu.columbia.cs.psl.invivo.record.CloningUtils;
import edu.columbia.cs.psl.invivo.record.Constants;
import edu.columbia.cs.psl.invivo.record.xstream.StaticReflectionProvider;

public class ReplayRunner {
	public static void main(String[] args) {
		if (args.length < 2) {
			System.err.println("Usage: ReplayRunner <mainClass> log [log2...logN]");
			System.exit(-1);
		}
		String mainClass = args[0];
		for (int i = 1; i < args.length; i++)// Presently not going to work with
												// more than 1 log
		{
			String log = args[i];
			try {
				Class<?> logger = Class.forName(Constants.LOG_DUMP_CLASS.replace("/", "."));
				XStream xstream = new XStream(new StaticReflectionProvider());
				CloningUtils.exportLock.writeLock().lock();
				Object o = xstream.fromXML(new File(log));
				logger.getMethod("clearLogFill").invoke(null);
				CloningUtils.exportLock.writeLock().unlock();

				Class<?> toRun = Class.forName(mainClass);
				toRun.getMethod("main", new Class<?>[]{args.getClass()}).invoke(null, new Object[]{new String[0]});
			} catch (Exception exi) {
				exi.printStackTrace();
			}
		}
		
//		try {
//			for (Field f : Class.forName("edu.columbia.cs.psl.invivo.example.ReaderUserInvivoLog").getDeclaredFields()) {
//				if (!Modifier.isStatic(f.getModifiers()))
//					continue;
//				try {
//					System.out.print(f.getName() + "->");
//					if (f.getType().isArray()) {
//						if (f.getType().getComponentType() == Integer.TYPE) {
//							for (int i = 0; i < Array.getLength(f.get(null)); i++) {
//								System.out.println(Array.get(f.get(null), i));
//							}
//						}
//						if (f.getType().getComponentType().isPrimitive())
//							System.out.println(f.get(null));
//						else
//							System.out.println(Arrays.deepToString((Object[]) f.get(null)));
//					} else
//						System.out.println(f.get(null));
//				} catch (IllegalArgumentException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (IllegalAccessException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
	}
}
