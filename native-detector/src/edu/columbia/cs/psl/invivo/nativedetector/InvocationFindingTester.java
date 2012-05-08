package edu.columbia.cs.psl.invivo.nativedetector;

import java.io.IOException;
import java.util.LinkedList;

import org.apache.log4j.Logger;

// TODO delete class
@SuppressWarnings("javadoc")
public class InvocationFindingTester {

	private static Logger logger = Logger.getLogger(InvocationFindingTester.class);
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		NativeDetector nd = new NativeDetector("/System/Library/Java/JavaVirtualMachines/1.6.0.jdk/Contents/Classes/classes.jar");
		
		nd.getAllClasses();
		logger.info("getAllClasses done");
		nd.logStats();
		
		nd.getAllMethods();
		logger.info("getAllMethods done");
		nd.logStats();
		
//		MethodInstance dummy = new MethodInstance("println", "(Ljava/lang/String;)V", "java/io/PrintStream");
//		System.out.println(dummy.hashCode());
//		for(MethodInstance mi : NativeDetector.allMethodsLookup.keySet())
//		{
//			if(mi.getMethod().getName().equals("println") && mi.getClazz().equals("java/io/PrintStream") && mi.getMethod().getDescriptor().equals( "(Ljava/lang/String;)V"))
//			{
//				System.out.println(mi.getMethod().getDescriptor() + "---"+mi.getClazz() + mi.hashCode());
//				if(mi.equals(dummy))
//					System.out.println("eq");
//				else
//					System.out.println("neq");
//			}
//		}
//		MethodInstance truePL = NativeDetector.getMethodInstance( "java/lang/Object","getClass", "()Ljava/lang/Class;");

//		logger.info(truePL.getCallers().size() + " callers");
//		LinkedList<MethodInstance> callers = truePL.getCallers();
//		for (MethodInstance c : callers) {
//			logger.info(c.getClazz()+" "+c.getMethod());
//		}
//		nd.openMethods.add(truePL);
//		logger.info(nd.openMethods.size());
//		assert(nd.openMethods.size()==1);
//		nd.logStats();
//		
//		/*
//		 * TODO FIXME
//		 * bugs I see
//		 * 	1) method is appearing in openMethods but not moving to closedMethods
//		 * 2) the methods that call it are not being added to openMethods
//		 * 3) npe after many methods are processed
//		 */
//		
//		nd.findNativeInvokers();
		logger.info("findNativeInvokers done");
		nd.logStats();
//		
		nd.writeClosedMethods("test.txt");

	}

}
