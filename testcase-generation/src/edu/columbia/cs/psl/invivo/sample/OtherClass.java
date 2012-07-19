package edu.columbia.cs.psl.invivo.sample;

import java.io.File;

public class OtherClass {
	public SimpleClass c;
	
	public OtherClass()
	{
		
	}
	public OtherClass(SimpleClass c)
	{
		
	}
	public void setFileAttribute(SimpleClass c)
	{
		c.f = new File("log4j.properties");
	}
	public SimpleClass getSimpleClass(SimpleClass c)
	{
		return c;
	}
}
