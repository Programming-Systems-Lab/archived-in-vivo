package edu.columbia.cs.psl.invivo.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.thoughtworks.xstream.XStream;

import edu.columbia.cs.psl.wallace.Constants;
import edu.columbia.cs.psl.wallace.xstream.StaticReflectionProvider;


public class DummyDriver extends SimpleClass {
	public String foo="bar";
	private String[] sarray = new String[4];
	private static String vm_Version = System.getProperty("java.runtime.version");
	public static void main(String[] args) throws Exception {

		DummyDriver d = new DummyDriver();
//		try
//		{
		d.go();
		d.evil();
//		}
//		catch(Exception ex)
//		{
//			try{
//				System.out.println("Serializing");
//			Class logger = Class.forName(Constants.LOG_DUMP_CLASS.replace("/", "."));
//				XStream xstream = new XStream(new StaticReflectionProvider());
//				String xml = xstream.toXML(logger.newInstance());
//				File output = new File("output.log");
//				FileWriter fw = new FileWriter(output);
//				fw.write(xml);
//				fw.close();
//				}
//			catch(Exception exi)
//			{
//				exi.printStackTrace();
//			}
//			ex.printStackTrace();
		}
//		try {
//			getText(new File("in-vivo.log"), false, new File("scratch.out"));
//		} catch (IOException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		DummyDriver.goStatic();
//	}
	private int bar = 3;
//	private double evilzzz = Math.random();
	
	public String evil()
	{
//		String sample = "asdklfjalsdjfladsfjd";
//		Scanner s = new Scanner(sample);
//		while(s.hasNext())
//		{
//			byte x = s.nextByte();
//		}
//		s.close();
		double r;
		for(int i = 0; i< 50493; i++)
		{
			r = Math.random();
//			System.out.println(r);
		}
		if(this.foo.equals("bar"))
		{
			return "bahaha";
		}
		else
			return "asldfdsf";
	}
	JFrame f = new JFrame();

	public DummyDriver() {
		// TODO Auto-generated constructor stub
		this.foo = "zz";
		this.sarray = new String[6];
		this.sarray[4] = "zz";
		bar = 6;
		
//		try{
//		File file = new File("fooObject");
//		if(file.exists())
//			file.delete();
//		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
//		oos.writeObject(f);
//		oos.close();
//		
//		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
//		f = (JFrame) ois.readObject();
//		ois.close();
//		}
//		catch(Exception ex){
//			ex.printStackTrace();
//		}
		
//		vm_Version = System.getProperty("java.runtime.version");
//		System.out.println(vm_Version);
//		vm_Version = System.getProperty("java.runtime.version");
//		System.out.println(vm_Version);
//		vm_Version = System.getProperty("java.runtime.version");
//		System.out.println(vm_Version);
//		Scanner s = new Scanner(System.in);
//		if(s.hasNext())
//			System.out.println("Has next");
	}
	/*
	public DummyDriver(String baz)
	{

		this.foo = baz;
	}
	public DummyDriver(String baz, String foo)
	{
		this.foo = baz;
	}
	public String foo = "foo";
	public Bar bar = new Bar();

	public static void goStatic()
	{
		Scanner s = new Scanner(System.in);
		System.out.println("enter some non deterministic (static) input!!! then ^D");
		while(s.hasNextLine())
		{
			System.out.println(s.nextLine());
		}
		for(Field f : DummyDriver.class.getDeclaredFields())
		{
			if(!Modifier.isStatic(f.getModifiers()))
				continue;
			try {
				System.out.print(f.getName()+ "->");
				if(f.getType().isArray())
				{
					if(f.getType().getComponentType().isPrimitive())
						System.out.println(f.get(null));
					else
						System.out.println(Arrays.deepToString((Object[]) f.get(null)));
				}
				else
					System.out.println(f.get(null));
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}*/
	private void doMore()
	{
		x = "zzzz";
	}
	private String x;
	private SimpleClass sc;
	public void go() throws Exception
	{
//		System.out.println(bar.foo.result);
		sc = new SimpleClass("asb");
		sc.f = new File("foo");
//		sc.o = new OtherClass();
		sc.o.c=sc;
		sc = null;
		x = "yz";
		for (int i = 0; i < sarray.length; i++) {
			this.sarray[i] = String.valueOf(i);
		}
		
		String[] newArray = new String[sarray.length];
		for (int i = 0; i < sarray.length; i++) {
			newArray[i] = String.valueOf(i + 1);
		}
		
		this.sarray = newArray;
		
		doMore();
		double ret = Math.cos(1);
		System.out.println(ret);
		if(Math.random() < .9)
			throw new Exception("Crashed");
		System.out.println(System.getProperty("java.runtime.version"));
//		for(Field f : DummyDriver.class.getDeclaredFields())
//		{
//			if(Modifier.isStatic(f.getModifiers()))
//				continue;
//			try {
//				System.out.print(f.getName()+ "->");
//				if(f.getType().isArray())
//				{
//					if(f.getType().getComponentType().isPrimitive())
//						System.out.println(f.get(this));
//					else
//						System.out.println(Arrays.deepToString((Object[]) f.get(this)));
//				}
//				else
//					System.out.println(f.get(this));
//			} catch (IllegalArgumentException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IllegalAccessException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		for(Field f : Class.forName(DummyDriver.class.getName()+"InvivoLog").getDeclaredFields())
//		{
//			try {
//				System.out.print(f.getName()+ "->");
//				if(f.getType().isArray())
//				{
//					if(f.getType().getComponentType().isPrimitive())
//						System.out.println(f.get(this));
//					else
//						System.out.println(Arrays.deepToString((Object[]) f.get(this)));
//				}
//				else
//					System.out.println(f.get(this));
//			} catch (IllegalArgumentException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IllegalAccessException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
	}
}
