package edu.columbia.cs.psl.invivo.sample;

import java.io.File;

public class SimpleClass {
	public File f;
	public void doSomething()
	{
//		new OtherClass().setFileAttribute(this);
//		new OtherClass().getSimpleClass(this).f = new File("stuff");
		OtherClass o = new OtherClass();
		
		o.c.f = new File("sdf");
	}
}
