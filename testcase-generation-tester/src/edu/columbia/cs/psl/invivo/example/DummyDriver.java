package edu.columbia.cs.psl.invivo.example;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Scanner;

public class DummyDriver {
	public static void main(String[] args) {
		DummyDriver d = new DummyDriver();
		d.go();
		DummyDriver.goStatic();
	}
	public DummyDriver() {
		// TODO Auto-generated constructor stub
		this.foo = "zz";
	}
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
	public String evil()
	{
//		String sample = "asdklfjalsdjfladsfjd";
//		Scanner s = new Scanner(sample);
//		while(s.hasNext())
//			if(s.next().equals("k"))
//				throw new Exception();
//		s.close();
//		
		return "bahaha";
	}
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
	}
	public void go()
	{
//		System.out.println(bar.foo.result);
		System.out.println(foo);
		System.out.println(bar);
		foo = "bar";
		double ret = Math.cos(1);
		System.out.println(ret);
		System.out.println(foo);
		for(int i =0; i<6;i++)
			System.out.println(evil());
		Scanner s = new Scanner(System.in);
		System.out.println("enter some non deterministic input!!! then ^D");
		while(s.hasNextLine())
		{
			System.out.println(s.nextLine());
		}
		for(Field f : DummyDriver.class.getDeclaredFields())
		{
			try {
				System.out.print(f.getName()+ "->");
				if(f.getType().isArray())
				{
					if(f.getType().getComponentType().isPrimitive())
						System.out.println(f.get(this));
					else
						System.out.println(Arrays.deepToString((Object[]) f.get(this)));
				}
				else
					System.out.println(f.get(this));
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
