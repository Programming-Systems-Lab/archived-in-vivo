package com.invite.changetracking;

import java.util.LinkedList;

public class DiffChain<T> {
	private T currentVersion;
	private LinkedList<DiffBundle> diffHistory;
	
	public DiffChain( T o ) {
		currentVersion = o;
		diffHistory = new LinkedList<DiffBundle>();
	}
	
	// d is the array of changes made by the current method call
	//   maxSize is passed in so all generated code is contained to the AspectJ aspect,
	//   perhaps allow it to change based on system resources
	public void update( String methodName, T newVersion, DiffValue[] d, int maxSize ) 
	{
		currentVersion = newVersion;
		diffHistory.addFirst( new DiffBundle( methodName, d ) );
		while ( diffHistory.size() > maxSize )
			diffHistory.removeLast();
	}
	
	public T getCurrentVersion() {
		return currentVersion;
	}

	public LinkedList<DiffBundle> getDiffHistory() {
		return (LinkedList<DiffBundle>)( diffHistory.clone() );
	}

	public void deleteHistory() {
		 diffHistory.clear();
		 currentVersion = null;
	}
	
	public String toString() {
		StringBuffer retBuf = new StringBuffer( currentVersion.toString() + ": < " );
		
		int i = 0;
		for ( DiffBundle bundle : diffHistory ) {
			retBuf.append( "{" );
			for ( int j = 0; j < bundle.diffVals.length; j++ ) {
				if ( j != bundle.diffVals.length - 1 )
					retBuf.append( bundle.diffVals[ j ].toString() + ", " );
				else
					retBuf.append( bundle.diffVals[ j ].toString() + " " );
			}
			
			if ( i != diffHistory.size() - 1 )
				retBuf.append( "}, " );
			else
				retBuf.append( "} >" );
			i++;
		}
		
		return retBuf.toString();
	}
}
