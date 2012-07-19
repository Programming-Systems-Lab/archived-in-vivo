package edu.columbia.cs.psl.invivo.example;

import java.io.File;

public class OtherClass {
	public SimpleClass c;
	
	public OtherClass()
	{
		
	}
	public OtherClass(SimpleClass c)
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
	}
}
