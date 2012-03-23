#!/usr/bin/perl

package Stringdata;


our $header = <<ML;
/*
 * Invite.aj
 * Generated on %s
 *
 * Copyright Columbia University 2008
 * Generator written by Del Slane - djs2160
 */

package com.invite.core;


import java.sql.*;
import java.util.*;
import java.lang.reflect.*;

import com.invite.changetracking.*;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.CodeSignature;

%s

public privileged aspect Invite {

	static final String dbURL = "jdbc:mysql://localhost/invite";
        static final String dbUser = "root";
        static final String dbPassword = "rootpass";

	// stores the names of all methods that don't have a corresponding unit test
        static TreeSet noTest = new TreeSet();

	public static final int DEFAULT_OBJ_VEC_SIZE = %s;// allocate this size vector per type
	public static final int MAX_CHAIN_LEN = %s; // max size of diff chains
        
	pointcut notTestMethod() : !call( boolean *.inviteTest () );
        pointcut notInvite() : 
			!within( com.invite.core.* ) 
		&& 	!within( com.invite.changetracking.* );
	pointcut notInit() : 
			!initialization( *.new ( .. ) )
          	&& 	!preinitialization( *.new ( .. ) )
          	&&  	!handler( * );
ML

our $fields_per_class = <<ML;
	private static int %s.nextId = 0; // set on object creation, is index in vector below
	private static Vector<DiffChain<%s>> %s.trackedObjects = 
			new Vector<DiffChain<%s>>( DEFAULT_OBJ_VEC_SIZE );
	private int %s.tracerAspectObjId; // add the ID field to each class instance
ML

our $generic_object_tracking_field = <<ML;
	private int %s.%sValue = 0; // add field to track object values as type GENERIC_OBJECT
ML

# Final pointcut is to avoid tracking while in the testing.  The nature of the final pointcut
#   means the user must manually call copy methods etc. inside the test to not disturb instance
#   variables - make code easy/clear, and can be warned via static analysis later
our $pointcuts_per_class = <<ML;
	pointcut objectCreation( %s x ) :
		initialization( new( .. ) )
	&&	target( x )
	&&	!cflow( execution( * %s.clone () ) );

	pointcut %sCall( %s x ) :
		call( * * ( .. ) )
	&&	!call( * clone () )
	&&	!call( * toString () ) // remove with print statments
	&&	!call( static * * ( .. ) ) // don't need to track static method calls
	&&	target( x );
ML


our $joinpoints_per_class = <<ML;
	after ( %s x ) : objectCreation( x ) && notInvite() && notTestMethod()
	{
		x.tracerAspectObjId = %s.nextId;
		%s.nextId++;
		%s.trackedObjects.add( x.tracerAspectObjId, new DiffChain<%s>( x.clone() ) );
	}

	after( %s newValue ) : %sCall( newValue ) && notInvite() && notTestMethod()
	{
		Vector<DiffValue> diffVals = new Vector<DiffValue>( 10, 5 );
		DiffChain<%s> trackingChain = %s.trackedObjects.get( newValue.tracerAspectObjId );
		%s oldValue = trackingChain.getCurrentVersion();
        	
        	// these cases based on members specified
%s
        	
		DiffValue[] a = new DiffValue[ diffVals.size() ];
		trackingChain.update( thisJoinPointStaticPart.getSignature().toString(), newValue.clone(), diffVals.toArray( a ), MAX_CHAIN_LEN );
        	
		//System.out.println( "after '" + thisJoinPointStaticPart.getSignature() + "': " );
		//System.out.println( "\\t" + trackingChain );
		//System.out.println();
	}
        
        // automatically generate pointcuts in other files that access package/public members
ML


# inserted per field to track for each class being processed, adds changed values to a diff value
our $field_checks_per_class = <<ML;
		if ( newValue.%s != oldValue.%s )
			diffVals.add( new DiffValue( oldValue.%s, newValue.%s, "%s", DiffValue.%s ) );
ML

our $object_checks_per_class = <<ML;
		if ( ! newValue.equals( oldValue ) )
			diffVals.add( new DiffValue( %s.%sValue, ++%s.%sValue, "%s", DiffValue.INT_TYPE ) );
ML

# the code for failure reporting and the aspect's closing brace
our $footer = <<ML;

	// instruments methods returning objects
	Object around() : 
			notInvite()
		&&	notTestMethod()
		&& 	!cflow( within( Invite ) && adviceexecution() )
		&&	!execution( static * * (..) )
		&&	execution( * * (.. ) )
	{
		// fixme: make this parallelizable
		Object ret = proceed();
		Object target = thisJoinPoint.getTarget();
	
		String invokedName = thisJoinPoint.getSignature().getName();                 		
                String fullInvokedName = target.getClass().getName() + "." + invokedName;

		if ( noTest.contains( fullInvokedName ) )
			return ret;

                String firstChar = invokedName.substring(0, 1).toUpperCase();
                String rest = invokedName.substring(1, invokedName.length());
                String testMethodName = "inviteTest" + firstChar + rest;

		try {
			Method testMethod = target.getClass().getMethod( testMethodName, null );
                	boolean testPassed  = ((Boolean)testMethod.invoke( target, null ) ).booleanValue();
                	reportFailedTest( target, testPassed ); 
                }		
                catch ( NoSuchMethodException e ) {
                	noTest.add( fullInvokedName );
                }
                catch ( Exception e ) {
                	e.printStackTrace();
                	System.exit( 0 );
		}
		finally {
			return ret;
		}
	}

	static void reportFailedTest( Object target, boolean failed )
        {
        	try {
                	Field trackedObjectsField = target.getClass().getField( "ajc\$interField\$com_invite_core_Invite\$trackedObjects" );
                	Field tracerObjIdField = target.getClass().getField( "ajc\$interField\$com_invite_core_Invite\$tracerAspectObjId" );
                                                                                               
                	if ( !trackedObjectsField.isAccessible() )
                		trackedObjectsField.setAccessible( true );
                	if ( !tracerObjIdField.isAccessible() )
                		tracerObjIdField.setAccessible( true );
                                                                                               
                	Vector<DiffChain<?>> trackedObjects = (Vector<DiffChain<?>>)
                        	( trackedObjectsField.get( target ) );
                        int objId = ((Integer)tracerObjIdField.get( target )).intValue();
        		
        		if ( objId < trackedObjects.size() ) // compensate for calling joinpoint executing before existence
                        	sendInfoToDb( trackedObjects.get( objId ), failed );
                }
                catch ( NoSuchFieldException e ) {
                	System.out.println( "Test failure report failed: " + e.getMessage() );
                	e.printStackTrace();
                }
                catch ( SecurityException e ) {
                	System.out.println( "Test failure report failed: " + e.getMessage() );
                	e.printStackTrace();
                }
                catch ( IllegalArgumentException e ) {
                	System.out.println( "Test failure report failed: " + e.getMessage() );
                	e.printStackTrace();
                }
                catch ( IllegalAccessException e ) {
                	System.out.println( "Test failure report failed: " + e.getMessage() );
                	e.printStackTrace();
                }
        }

        static private void sendInfoToDb( DiffChain<?> chain, boolean failed ) {
        	String objType = chain.getCurrentVersion().getClass().toString();
                                                                                                                              
        	try {
        		// set up database connection, can make this a pool later
                        Class.forName("com.mysql.jdbc.Driver").newInstance();                              	
                        Connection dbConnection = DriverManager.getConnection( dbURL, dbUser, dbPassword );

			// use transactions
			dbConnection.setAutoCommit( false );
                                                                                                                              
                        PreparedStatement getLastId = dbConnection.prepareStatement( "SELECT LAST_INSERT_ID()" );
                        PreparedStatement infoInsert = dbConnection.prepareStatement( "INSERT INTO diffinfo " +
                        		"(tid, object_type, method_name) VALUES (?, ?, ?)" );
                        PreparedStatement valsInsert = dbConnection.prepareStatement( "INSERT INTO diffvals " +
                        		"(field_name, field_value, field_change, did) VALUES (?, ?, ?, ?)" );
                                                                                                                              
			PreparedStatement makeTrial = dbConnection.prepareStatement( "INSERT INTO trials (failed) VALUES (?)" );
			if ( failed )                    			
                        	makeTrial.setInt( 1, 1 );
			else 
                        	makeTrial.setInt( 1, 0 );
			makeTrial.executeUpdate();

        		ResultSet rs = getLastId.executeQuery();
        		if ( !rs.next() )
        			throw new SQLException( "No element in result set for last ID" );
        		int tid = rs.getInt( 1 );
                                                                                                                              
        		for ( DiffBundle bundle : chain.getDiffHistory() ) {
        			infoInsert.setInt( 1, tid );
        			infoInsert.setString( 2, objType );
        			infoInsert.setString( 3, bundle.getMethodName() );
        			infoInsert.executeUpdate();
                                                                                                                              
        			rs = getLastId.executeQuery();
        			if ( !rs.next() )
        				throw new SQLException( "No element in result set" );
        			int did = rs.getInt( 1 );
                                                                                                                              
        			for ( DiffValue value : bundle.getDiffVals() ) {
        				valsInsert.setString( 1, value.getFieldName() );
        				valsInsert.setInt( 4, did );
        				setFinalValInsertParams( value, valsInsert );	
        				valsInsert.executeUpdate();
        			}
        		}

			dbConnection.commit(); // commit as a transaction
        	}
        	catch ( SQLException e ) {
        		System.out.println( "SQLException in sendInfoToDb: " + e.getMessage() );
        	}
        	catch ( Exception e ) {
        		System.out.println( "Error loading MySQL JDBC driver: " + e.getMessage() );
        	}
        }
	
	static private void setFinalValInsertParams( DiffValue value, PreparedStatement valsInsert )
        		throws SQLException
        {
        	switch ( value.getType() ) {
                	case DiffValue.BYTE_TYPE:
                		Byte oldValue = (Byte)value.getOldValue();
                		Byte newValue = (Byte)value.getNewValue();
                		valsInsert.setDouble( 2, newValue.doubleValue() );
                		valsInsert.setDouble( 3, newValue.doubleValue() - oldValue.doubleValue() );
                		break;
                	case DiffValue.SHORT_TYPE:
                		Short oldValueS = (Short)value.getOldValue();
                		Short newValueS = (Short)value.getNewValue();
                		valsInsert.setDouble( 2, newValueS.doubleValue() );
                		valsInsert.setDouble( 3, newValueS.doubleValue() - oldValueS.doubleValue() );
                		break;
                	case DiffValue.INT_TYPE:
                		Integer oldValueI = (Integer)value.getOldValue();
                		Integer newValueI = (Integer)value.getNewValue();
                		valsInsert.setDouble( 2, newValueI.doubleValue() );
                		valsInsert.setDouble( 3, newValueI.doubleValue() - oldValueI.doubleValue() );
	        		break;                                                                                         	
                	case DiffValue.LONG_TYPE:
                		Long oldValueL = (Long)value.getOldValue();
                		Long newValueL = (Long)value.getNewValue();
                		valsInsert.setDouble( 2, newValueL.doubleValue() );
                		valsInsert.setDouble( 3, newValueL.doubleValue() - oldValueL.doubleValue() );
                		break;
                	case DiffValue.FLOAT_TYPE:
                		Float oldValueF = (Float)value.getOldValue();
                		Float newValueF = (Float)value.getNewValue();
                		valsInsert.setDouble( 2, newValueF.doubleValue() );
                		valsInsert.setDouble( 3, newValueF.doubleValue() - oldValueF.doubleValue() );
                		break;
                	case DiffValue.DOUBLE_TYPE:
                		Double oldValueD = (Double)value.getOldValue();
                		Double newValueD = (Double)value.getNewValue();
                		valsInsert.setDouble( 2, newValueD.doubleValue() );
                		valsInsert.setDouble( 3, newValueD.doubleValue() - oldValueD.doubleValue() );
                		break;
                	case DiffValue.BOOLEAN_TYPE:
                		Boolean oldValueB = (Boolean)value.getOldValue();
                		Boolean newValueB = (Boolean)value.getNewValue();
                		if ( oldValueB.booleanValue() ) {
                			valsInsert.setDouble( 2, 1.0 );
                			if ( newValueB.booleanValue() )
                				valsInsert.setDouble( 3, 0.0 );
                			else
                				valsInsert.setDouble( 3, 1.0 );
                		}
                		else {
                			valsInsert.setDouble( 2, 0.0 );
                			if ( newValueB.booleanValue() )
                				valsInsert.setDouble( 3, 1.0 );
                			else
                				valsInsert.setDouble( 3, 0.0 );
                		}
                		break;
                	case DiffValue.CHAR_TYPE:
                		Integer oldValueC = new Integer( Character.getNumericValue( (Character)value.getOldValue() ) );
                		Integer newValueC = new Integer( Character.getNumericValue( (Character)value.getNewValue() ) );
                		valsInsert.setDouble( 2, newValueC.doubleValue() );
                		valsInsert.setDouble( 3, newValueC.doubleValue() - oldValueC.doubleValue() );
                		break;
                	default:
                		assert ( false );
                }
        }

}
ML

1;

