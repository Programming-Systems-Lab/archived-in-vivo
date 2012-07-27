package edu.columbia.cs.psl.invivo.example;

import java.io.File;

public class OtherClass {
	public SimpleClass c;
	
	static int integer_1;
	
	int integer_2;
	
	static String string_1;
	
	String string_2;
	
	@Override
	public String toString() {
		return "OtherClass [c=" + (c != null && c.o != this ? c.toString() : "(recurse)") + "]";
	}
	
	public OtherClass(int a, String b)
	{
		this.integer_2 = a;
		this.string_2 = b;
	}
	
	public OtherClass(SimpleClass simpleClass) {
		// TODO Auto-generated constructor stub
	}

	public OtherClass() {
		// TODO Auto-generated constructor stub
	}

	public int foo(int a, float b, double c, short d, String e) {
		a += (int) Math.random();
		a += System.currentTimeMillis();
		return a;
	}
	
	public Object bar(Object j) {
		Object b = foo(1,(float)2,3.14,(short) 1,"test");
		return b;
	}
	
	public static void main(String[] args) {
		OtherClass b = new OtherClass(1, "test");
		Object c = b.bar(1);
	}

	public Object getSimpleClass() {
		// TODO Auto-generated method stub
		return null;
	}

	public void thinkAboutThings() {
		// TODO Auto-generated method stub
		
	}

	public void setFileAttribute() {
		// TODO Auto-generated method stub
		
	}
	
	/*public OtherClass(SimpleClass c)
	{
		this.c = c;
	}
	
	public void setFileAttribute(SimpleClass c)
	{
		c.f = new File("log4j.properties");
	}
	
	public void thinkAboutThings()
	{
		getSimpleClass().evil(this);
	}
	
	public void setFileAttribute()
	{
		c.f = new File("log4j.properties");
	}
	
	public SimpleClass getSimpleClass(SimpleClass c)
	{
		return c;
	}
	
	public SimpleClass getSimpleClass() {
		// TODO Auto-generated method stub
		return c;
	}*/
}
