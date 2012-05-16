package edu.columbia.cs.psl.invivo.junit.rewriter;

import java.util.ArrayList;
import java.util.HashSet;

import org.objectweb.asm.Type;

import edu.columbia.cs.psl.invivo.junit.annotation.InvivoTest;

public class JUnitInvivoMethodDescription {
	public String name;
	public String clazz;
	public String desc;
	public boolean isTestCase;
	public ArrayList<VariableReplacement> replacements = new ArrayList<VariableReplacement>();
	public static class VariableReplacement
	{
		public String from;
		public String to;
		public int indx;
		public Type type;
		public int argIndx;
		
		public VariableReplacement(String from)
		{
			this.from = from;
		}

		@Override
		public String toString() {
			return "VariableReplacement [from=" + from + ", to=" + to + ", indx=" + indx + ", type=" + type + ", argIndx=" + argIndx + "]";
		}
	}
	public JUnitInvivoMethodDescription(String name, String desc)
	{
		this.name = name;
		this.desc = desc;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((desc == null) ? 0 : desc.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JUnitInvivoMethodDescription other = (JUnitInvivoMethodDescription) obj;
		if (desc == null) {
			if (other.desc != null)
				return false;
		} else if (!desc.equals(other.desc))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "JUnitInvivoMethodDescription [name=" + name + ", clazz=" + clazz + ", desc=" + desc + ", isTestCase=" + isTestCase + ", replacements="
				+ replacements + "]";
	}
	
}
