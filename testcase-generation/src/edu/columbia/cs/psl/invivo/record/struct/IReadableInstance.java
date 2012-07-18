package edu.columbia.cs.psl.invivo.record.struct;

import java.util.Stack;

public interface IReadableInstance {
	public int getType();
	public static int FIELD_TYPE = 1;
	public static int METHOD_TYPE = 2;
	public static int CONSTANT_TYPE = 3;
	public IReadableInstance getParent();
	public void setParent(IReadableInstance ir);
	public int getStackElementsToSkip();
	public int getOpcode();
}
