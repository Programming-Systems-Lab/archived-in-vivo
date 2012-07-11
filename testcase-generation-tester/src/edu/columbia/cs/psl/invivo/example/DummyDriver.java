package edu.columbia.cs.psl.invivo.example;

import java.lang.reflect.Field;

import edu.columbia.cs.psl.invivo.record.Constants;

public class DummyDriver {
	public static void main(String[] args) {
		DummyDriver d = new DummyDriver();
		d.go();
	}
	public String foo = "foo";
	public Bar bar = new Bar();
	public void go()
	{
//		System.out.println(bar.foo.result);
		System.out.println(foo);
		System.out.println(bar);
		foo = "bar";
		System.out.println(foo);

		for(Field f : DummyDriver.class.getDeclaredFields())
		{
			try {
				System.out.println(f.getName()+ "->"+f.get(this));
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
