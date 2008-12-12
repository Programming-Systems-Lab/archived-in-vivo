package com.invite.drivers;

import java.util.Random;

import com.invite.core.*;

public class Runner {
	
	public Runner() {
		
	}


	public static void main( String[] args )
	{
		IntFields f = new IntFields( 1, 2, 3, 4 );
		
		Random r = new Random( System.currentTimeMillis() );

		int i;
		long start = System.currentTimeMillis();
		for ( i = 0; i < 30000000; i++ ) {
			if ( r.nextDouble() < 0.5 )
				f.increment();
			else
				f.decrement();
			IntFields.myStaticMethod();
		}
		long finish = System.currentTimeMillis();
		
		System.out.println( "Finished (instrumented) in " + (finish - start) / 1000.0 + " seconds" );
		System.out.println( "i: " + i );
	}
}
