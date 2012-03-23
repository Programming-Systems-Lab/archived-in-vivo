package com.invite.drivers;

import com.invite.core.*;

public class TestTracking {
	public static void main( String[] args )
	{
		IntFields f = new IntFields( 1, 2, 3, 4 );
		for ( int i = 0; i < 4; i++ ) {
			f.increment();
			f.decrement();
		}
	}
}

