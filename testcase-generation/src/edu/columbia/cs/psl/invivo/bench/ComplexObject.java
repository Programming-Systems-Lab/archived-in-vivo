package edu.columbia.cs.psl.invivo.bench;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map.Entry;

import edu.columbia.cs.psl.invivo.record.CloningUtils;
import edu.columbia.cs.psl.invivo.record.Instrumenter;
import edu.columbia.cs.psl.invivo.sample.SimpleClass;

public class ComplexObject extends SimpleClass implements Cloneable {
	private String[] children;
	private HashMap<String, Integer> children2;
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
	
//	public ComplexObject[] getChildren() {
//		return children;
//	}
//
//	public void setChildren(ComplexObject[] children) {
//		this.children = children;
//	}
//
//	public ArrayList<String> getChildren2() {
//		return children2;
//	}
//
//	public void setChildren2(ArrayList<String> children2) {
//		this.children2 = children2;
//	}

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
		if(CloningUtils.cloneCache.containsKey(this))
			return (ComplexObject) CloningUtils.cloneCache.get(this);
		
		final ComplexObject ret = (ComplexObject) clone();
		
		CloningUtils.cloneCache.put(ret, ret);
		
		ret.annoying0 = CloningUtils.cloner.deepClone(this.annoying0);
		
		if (Instrumenter.instrumentedClasses.containsKey(this.getClass().getSuperclass().getName())) {	
//			ret = super.setFieldsOn(ret);
		}
		
		//BeingCloned.cloneCache.put(this, ret);
		
		/*if (children2 != null) {
			ret.children2 = new HashMap<String, Integer>();
			
			for (Entry<String, Integer> e : children2.entrySet())
				ret.children2.put(e.getKey().toString(), e.getValue());
		}*/
		
		/*if(parent != null)
			ret.parent = parent._copy();
		*/

		if(children != null)
		{
			ret.children = new String[this.children.length];
			System.arraycopy(this.children, 0, ret.children, 0, this.children.length);
		}
		/*
		if(soo != null)
			ret.soo = soo._copy();
		
		if (s != null)
			ret.s = s;
		*/
		
		return ret;
	}

	public ComplexObject(ComplexObject[] children, ArrayList<String> children2, ComplexObject parent, String s, SomeOtherObject soo) {
//		this.children = children;
//		this.children2 = children2;
		this.parent = parent;
		this.s = s;
		this.soo = soo;
	}
	public ComplexObject setFieldsOn(ComplexObject ret) {
		super.setFieldsOn(ret);
		return null;
	}

}
