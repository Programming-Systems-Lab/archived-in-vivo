package edu.columbia.cs.psl.invivo.sample;

import java.io.File;

public class SimpleClass {
	public File f;
	public SimpleClass()
	{
		
	}
	public SimpleClass(String s)
	{
		
	}
	public OtherClass o;
	public OtherClass o2;
	public void evil(OtherClass o1)
	{
		OtherClass z = getOtherClass();
		z.c.f = new File("b"); //copy z.c
		
		o1 = o2; //made copy of o1 NB
		o1.c.f = new File("s"); //make copy of o1.c.f
	}
	private OtherClass getOtherClass()
	{
		if(Math.random() > .5)
			return o2;
		else if(Math.random() < .4)
			return o.getSimpleClass().o;
		return o;
	}
	public void makeCrash()
	{
		evil(o);
		if(o.c.f.equals("something bad"))
			System.out.println("Crash");
	}
	public void doSomething()
	{
		OtherClass o = new OtherClass(this);
		o.setFileAttribute();
//		new OtherClass().setFileAttribute(this);
//		new OtherClass().getSimpleClass(this).f = new File("sdf");
//		new OtherClass(new SimpleClass("a")).c = this;
//		getOtherClass().getSimpleClass(getOtherClass().c).f = new File("sdf");
		getOtherClass().getSimpleClass(new OtherClass().getSimpleClass(new SimpleClass())).f = new File("stuff");
//		OtherClass o = new OtherClass();
		
//		new OtherClass().c.f = new File("sdf");
	}
}
