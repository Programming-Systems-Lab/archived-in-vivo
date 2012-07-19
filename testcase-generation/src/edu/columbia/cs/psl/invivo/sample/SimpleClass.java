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
	private OtherClass getOtherClass()
	{
		if(Math.random() > .5)
			return new OtherClass();
		return null;
	}
	public void doSomething()
	{
//		new OtherClass().setFileAttribute(this);
//		new OtherClass().getSimpleClass(this).f = new File("sdf");
//		new OtherClass(new SimpleClass("a")).c = this;
//		getOtherClass().getSimpleClass(getOtherClass().c).f = new File("sdf");
		getOtherClass().getSimpleClass(new OtherClass().getSimpleClass(new SimpleClass())).f = new File("stuff");
//		OtherClass o = new OtherClass();
		
//		new OtherClass().c.f = new File("sdf");
	}
}
