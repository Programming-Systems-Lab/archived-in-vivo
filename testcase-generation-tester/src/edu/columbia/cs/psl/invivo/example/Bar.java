package edu.columbia.cs.psl.invivo.example;

import java.io.File;

public class Bar implements C{
	private static String bad;
	private double r;
	public Foo foo = new Foo();
	public String result;
	public void evil()
	{
		result = foo.result;
//		C evil = EvilFactory.getC();
		EvilFactory.getC().doSomething(this);
	}
	@Override
	public void doSomething(Bar b) {
		//Does nothing.
//		bad = System.getProperty("java.version").toString() + "foo";
		File f = new File("foo");
		if(f.exists())
		{
			System.out.println("foo");
		}
//		r = Math.random();
	}
	public static void main(String[] args) {
		Bar b = new Bar();
		b.doSomething(b);
		System.out.println(bad);
	}
}
