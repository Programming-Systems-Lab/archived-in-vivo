package com.invite.drivers;

import java.lang.reflect.*;

public class Fields {
	public static void main( String[] args ) {
		 try {
		 	Class intFieldsClass = Class.forName( "com.invite.drivers.IntFields" );	
                 	Field[] fields = intFieldsClass.getDeclaredFields();
                 	for ( int i = 0; i < fields.length; i++ )
                 		System.out.println( fields[ i ] );
		 }
		 catch ( ClassNotFoundException e ) {
		 	System.out.println( e.getMessage() );
			e.printStackTrace();
		 }
		
	}
}

