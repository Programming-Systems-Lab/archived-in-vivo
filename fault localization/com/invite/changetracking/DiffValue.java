package com.invite.changetracking;

public class DiffValue {
	public static final int NO_TYPE			= -1;
	public static final int BYTE_TYPE 		= 0;
	public static final int SHORT_TYPE 		= 1;
	public static final int INT_TYPE 		= 2;
	public static final int LONG_TYPE 		= 3;
	public static final int FLOAT_TYPE 		= 4;
	public static final int DOUBLE_TYPE 		= 5;
	public static final int BOOLEAN_TYPE		= 6;
	public static final int CHAR_TYPE 		= 7;
	
	protected int type;
	protected Object oldValue;
	protected Object newValue;
	protected String fieldName;
	
	public DiffValue( Object oldValue, Object newValue, String fieldName, int type ) {
		this.oldValue = oldValue;
		this.newValue = newValue;
		this.fieldName = fieldName;
		this.type = type;
	}
	
	public int getType() {
		return type;
	}
	
	public Object getNewValue() {
		return newValue;
	}
	
	public Object getOldValue() {
		return oldValue;
	}
	
	public String getFieldName() {
		return fieldName;
	}
	
	public String toString() {
		String typeName = "";
		switch ( type ) {
			case BYTE_TYPE: typeName = "Byte"; break;
			case SHORT_TYPE: typeName = "Short"; break;
			case INT_TYPE: typeName = "Integer"; break;
			case LONG_TYPE: typeName = "Long"; break;
			case FLOAT_TYPE: typeName = "Float"; break;
			case DOUBLE_TYPE: typeName = "Double"; break;
			case BOOLEAN_TYPE: typeName = "Boolean"; break;
			case CHAR_TYPE: typeName = "Character"; break;
			default: assert( false );
		}
		return fieldName + "(" + typeName + "): " + oldValue.toString() + "->" + newValue.toString();
	}
}
