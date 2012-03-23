package com.invite.drivers;

public class IntFields {
	private int field1;
	private int field2;
	private int field3;
	private int field4;

	private int oldField1;
	
	private Integer field5;
	private Integer field6;

	private boolean testBool;
	
	public IntFields( int a, int b, int c, int d ) {
		field1 = a;
		field2 = b;
		field3 = c;
		field4 = d;
		
		field5 = new Integer( a );
		field6 = new Integer( b );

		testBool = false;
	}
	
	public void increment() {
		oldField1 = field1;
		field1++;
		field2++;
		field3++;
		field4++;
	}

	// should fail if decrement was called at least twice in a row
	public boolean inviteTestDecrement() {
		 if ( oldField1 - field1 >= 2 )
			 return true;
		 return false;
	}
	
	public void decrement() {
		field1--;
		field2--;
		field3--;
		field4--;
	}

	public void setTestBool( boolean b ) {
		testBool = b;
	}
	
	public static String myStaticMethod() {
		return "static string";
	}
	
	public IntFields clone() {
		return new IntFields( field1, field2, field3, field4 );
	}
	
	public String toString() {
		return "[ " + Integer.toString(field1) + ", " + Integer.toString(field2) + ", " +
				Integer.toString(field3) + ", " + Integer.toString( field4 ) + "]";
	}
}
