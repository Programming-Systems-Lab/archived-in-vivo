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
		if(annoying0 != null) ret.annoying0 = annoying0._copy();
		if(annoying1 != null) ret.annoying1 = annoying1._copy();
		if(annoying2 != null) ret.annoying2 = annoying2._copy();
		if(annoying3 != null) ret.annoying3 = annoying3._copy();
		if(annoying4 != null) ret.annoying4 = annoying4._copy();
		if(annoying5 != null) ret.annoying5 = annoying5._copy();
		if(annoying6 != null) ret.annoying6 = annoying6._copy();
		if(annoying7 != null) ret.annoying7 = annoying7._copy();
		if(annoying8 != null) ret.annoying8 = annoying8._copy();
		if(annoying9 != null) ret.annoying9 = annoying9._copy();
		if(annoying10 != null) ret.annoying10 = annoying10._copy();
		if(annoying11 != null) ret.annoying11 = annoying11._copy();
		if(annoying12 != null) ret.annoying12 = annoying12._copy();
		if(annoying13 != null) ret.annoying13 = annoying13._copy();
		if(annoying14 != null) ret.annoying14 = annoying14._copy();
		if(annoying15 != null) ret.annoying15 = annoying15._copy();
		if(annoying16 != null) ret.annoying16 = annoying16._copy();
		if(annoying17 != null) ret.annoying17 = annoying17._copy();
		if(annoying18 != null) ret.annoying18 = annoying18._copy();
		if(annoying19 != null) ret.annoying19 = annoying19._copy();
		if(annoying20 != null) ret.annoying20 = annoying20._copy();
		if(annoying21 != null) ret.annoying21 = annoying21._copy();
		if(annoying22 != null) ret.annoying22 = annoying22._copy();
		if(annoying23 != null) ret.annoying23 = annoying23._copy();
		if(annoying24 != null) ret.annoying24 = annoying24._copy();
		if(annoying25 != null) ret.annoying25 = annoying25._copy();
		if(annoying26 != null) ret.annoying26 = annoying26._copy();
		if(annoying27 != null) ret.annoying27 = annoying27._copy();
		if(annoying28 != null) ret.annoying28 = annoying28._copy();
		if(annoying29 != null) ret.annoying29 = annoying29._copy();
		if(annoying30 != null) ret.annoying30 = annoying30._copy();
		if(annoying31 != null) ret.annoying31 = annoying31._copy();
		if(annoying32 != null) ret.annoying32 = annoying32._copy();
		if(annoying33 != null) ret.annoying33 = annoying33._copy();
		if(annoying34 != null) ret.annoying34 = annoying34._copy();

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
