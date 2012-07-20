package edu.columbia.cs.psl.invivo.bench;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;

public class ComplexObject implements Cloneable {
	private ComplexObject[] children;
	private ArrayList<ComplexObject> children2;
	private ComplexObject parent;
	private String s;
	private SomeOtherObject soo;

	public ComplexObject annoying0;
	public ComplexObject annoying1;
	public ComplexObject annoying2;
	public ComplexObject annoying3;
	public ComplexObject annoying4;
	public ComplexObject annoying5;
	public ComplexObject annoying6;
	public ComplexObject annoying7;
	public ComplexObject annoying8;
	public ComplexObject annoying9;
	public ComplexObject annoying10;
	public ComplexObject annoying11;
	public ComplexObject annoying12;
	public ComplexObject annoying13;
	public ComplexObject annoying14;
	public ComplexObject annoying15;
	public ComplexObject annoying16;
	public ComplexObject annoying17;
	public ComplexObject annoying18;
	public ComplexObject annoying19;
	public ComplexObject annoying20;
	public ComplexObject annoying21;
	public ComplexObject annoying22;
	public ComplexObject annoying23;
	public ComplexObject annoying24;
	public ComplexObject annoying25;
	public ComplexObject annoying26;
	public ComplexObject annoying27;
	public ComplexObject annoying28;
	public ComplexObject annoying29;
	public ComplexObject annoying30;
	public ComplexObject annoying31;
	public ComplexObject annoying32;
	public ComplexObject annoying33;
	public ComplexObject annoying34;

	
	public ComplexObject()
	{
		
	}
	
	public ComplexObject[] getChildren() {
		return children;
	}

	public void setChildren(ComplexObject[] children) {
		this.children = children;
	}

	public ArrayList<ComplexObject> getChildren2() {
		return children2;
	}

	public void setChildren2(ArrayList<ComplexObject> children2) {
		this.children2 = children2;
	}

	public ComplexObject getParent() {
		return parent;
	}

	public void setParent(ComplexObject parent) {
		this.parent = parent;
	}

	public String getS() {
		return s;
	}

	public void setS(String s) {
		this.s = s;
	}

	public SomeOtherObject getSoo() {
		return soo;
	}

	public void setSoo(SomeOtherObject soo) {
		this.soo = soo;
	}

	public ComplexObject copy()
	{
		BeingCloned.cloneCache = new IdentityHashMap<Object, Object>();
		ComplexObject ret = null;
		try {
			ret = _copy();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
	public ComplexObject _copy() throws CloneNotSupportedException {
		if(BeingCloned.cloneCache.containsKey(this))
			return (ComplexObject) BeingCloned.cloneCache.get(this);
		
		final ComplexObject ret = (ComplexObject) clone();
		BeingCloned.cloneCache.put(this, ret);
		if(children != null){
			ret.children = new ComplexObject[children.length];
		for(int i =0; i<ret.children.length; i++)
			ret.children[i] = children[i]._copy();
		}
		if(parent != null)
			ret.parent = parent._copy();
		if(children2 != null)
		{
			ret.children2 = new ArrayList<ComplexObject>(children2.size());
			for(int i = 0; i<children2.size();i++)
			{
				ret.children2.add(children2.get(i)._copy());
			}
		}
		if(soo != null)
			ret.soo = soo._copy();
		return ret;
	}

	public ComplexObject(ComplexObject[] children, ArrayList<ComplexObject> children2, ComplexObject parent, String s, SomeOtherObject soo) {
		this.children = children;
		this.children2 = children2;
		this.parent = parent;
		this.s = s;
		this.soo = soo;
	}

}
