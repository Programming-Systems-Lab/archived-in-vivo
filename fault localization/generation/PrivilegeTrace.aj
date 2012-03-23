package snapshotter;

import java.util.Vector;
import java.util.LinkedList;

import changetracking.*;
import drivers.*; // must import all classes instrumented

public privileged aspect PrivilegeTrace {
/*
	public static final int DEFAULT_OBJ_VEC_SIZE = 50; // allocate this size vector per type
	
	// from here on, do once per class as appropriate for indicated instrumentation
	private static int IntFields.nextId = 0;
	private static Vector<DiffChain<IntFields>> IntFields.trackedObjects = new Vector<DiffChain<IntFields>>( DEFAULT_OBJ_VEC_SIZE );	
	private int IntFields.tracerAspectObjId;
	
	// need one of these for all classes having some part traced
	pointcut objectCreation( IntFields x ) :
		initialization( new( .. ) )
	&&	target( x )
	&&	!cflow( execution( * IntFields.copy() ) );
	
	after ( IntFields x ) : objectCreation( x )
	{
		x.tracerAspectObjId = IntFields.nextId;
		IntFields.nextId++;
		IntFields.trackedObjects.add( x.tracerAspectObjId, new DiffChain<IntFields>( x.copy() ) );
		//LinkedList<IntFields> objList = new LinkedList<IntFields>();
		//objList.add( x.copy() );
		//IntFields.trackedCopies.add( x.tracerAspectObjId, objList );
		//System.out.println( "Creation: " + IntFields.trackedObjects.toString() );
	}
	
	// once per class to prevent type lookup, matches a method call not related to the implementation
	pointcut IntFieldsCall( IntFields x ) :
		call( * * ( .. ) )
	&& 	!call( * copy () )
	&&	!call( * toString () ) // comment out after taking out print statements
	&&	!call( static * * () )
	&&	target( x );
	
	// track changes to the object due to method calls, DIFF VERSION
	after( IntFields newValue ) : IntFieldsCall( newValue )
	{
		Vector<DiffValue> diffVals = new Vector<DiffValue>( 10, 5 );
		DiffChain<IntFields> trackingChain = IntFields.trackedObjects.get( newValue.tracerAspectObjId );
		IntFields oldValue = trackingChain.getCurrentVersion();
		
		// generate these cases based on members
		if ( newValue.field1 != oldValue.field1 )
			diffVals.add( new DiffValue( oldValue.field1, newValue.field1, "field1", DiffValue.INT_TYPE ) );
		if ( newValue.field3 != oldValue.field3 )
			diffVals.add( new DiffValue( oldValue.field3, newValue.field3, "field3", DiffValue.INT_TYPE ) );
		
		DiffValue[] a = new DiffValue[ diffVals.size() ];
		trackingChain.update( thisJoinPointStaticPart.getSignature().toString(), newValue.copy(), diffVals.toArray( a ) );
		
//		System.out.println( "after '" + thisJoinPointStaticPart.getSignature() + "': " );
//		System.out.println( "\t" + trackingChain );
//		System.out.println();
	}
	
	// automatically generate pointcuts in other files that access package/public members
	*/
}
