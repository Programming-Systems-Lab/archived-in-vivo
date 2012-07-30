package edu.columbia.cs.psl.invivo.bench;

import java.io.File;
import java.util.IdentityHashMap;

public class SomeOtherObject implements Cloneable {
	private File f;
	private ComplexObject o;
	private SomeOtherObject otherObject;
	private String s;
	private int n;
	public SomeOtherObject(File f, ComplexObject o, SomeOtherObject otherObject, String s, int n) {
		this.f = f;
		this.o = o;
		this.otherObject = otherObject;
		this.s = s;
		this.n = n;
	}
	public File getF() {
		return f;
	}
	public void setF(File f) {
		this.f = f;
	}
	public ComplexObject getO() {
		return o;
	}
	public void setO(ComplexObject o) {
		this.o = o;
	}
	public SomeOtherObject getOtherObject() {
		return otherObject;
	}
	public void setOtherObject(SomeOtherObject otherObject) {
		this.otherObject = otherObject;
	}
	public String getS() {
		return s;
	}
	public void setS(String s) {
		this.s = s;
	}
	public int getN() {
		return n;
	}
	public void setN(int n) {
		this.n = n;
	}
	public SomeOtherObject()
	{
		
	}
	public SomeOtherObject _copy() throws CloneNotSupportedException
	{
		if(BeingCloned.cloneCache.containsKey(this))
			return (SomeOtherObject) BeingCloned.cloneCache.get(this);
		final SomeOtherObject ret = (SomeOtherObject) clone();
		BeingCloned.cloneCache.put(this, ret);
		if(o !=null)
			ret.o = o._copy();
		if(otherObject != null)
			ret.otherObject = otherObject._copy();
		if(s != null)
			ret.s = s;
		ret.n = n;
		return ret;
	}
	public SomeOtherObject copy() {
		BeingCloned.cloneCache = new IdentityHashMap<Object, Object>();
		SomeOtherObject ret = null;
		try {
			ret = _copy();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BeingCloned.cloneCache = new IdentityHashMap<Object, Object>();
		return ret;
	}
	
}
