package edu.columbia.cs.psl.invivo.example;

import java.io.File;
import java.util.ArrayList;

public class SimpleClass extends ArrayList {
	public File f;
	public SimpleClass()
	{
		
	}
	public SimpleClass(String s)
	{
		
	}
	@Override
	public String toString() {
		return "SimpleClass [f=" + f + ", o=" + o + ", o2=" + o2 + "]";
	}
	public OtherClass o;
	public OtherClass o2;
	public void evil(OtherClass o1)
	{
		OtherClass z = getOtherClass();
		z.c.f = new File("b"); //copy z.c.f onto this object, and a ref to z.c
		
		o1 = o2; //made copy of o1 NB onto this object
		o1.c.f = new File("s"); //make copy of o1.c.f onto this object
		o1.c.f = new File("z"); //make copy
	}
	private OtherClass getOtherClass()
	{
		if(Math.random() > .5)
			return o2;
		else if(Math.random() < .4)
			return null;
		return o;
	}
	public void makeCrash()
	{
		//record counters for each array of backups that we think we might touch, with the references
		evil(o);
		getOtherClass().thinkAboutThings();
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
		//getOtherClass().getSimpleClass().f = new File("stuff");
//		OtherClass o = new OtherClass();
		
//		new OtherClass().c.f = new File("sdf");
	}
}
