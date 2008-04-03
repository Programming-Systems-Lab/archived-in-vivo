import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.CodeSignature;
import java.lang.reflect.*;
import java.util.*;
import java.io.*;

aspect InsertTest {

    // the configuration file
    static String configFile = "/home/idv2101/invite/config";

    // percent of tests to execute (0 to 1.0) ... for 1% tests, set to 0.0002.... for 10%, set to 0.002
    // static final double TEST_PCT = .002;

    // whether or not we're currently executing a test
    static volatile boolean inTest = false;

    // the class to do the fork
    static Forker forker = new Forker();

    // stores the names of all methods that don't have a corresponding unit test
    static TreeSet noTest = new TreeSet();

    // whether or not the test harness has been initialized
    static boolean initialized = false;

    // stores the names of all the test methods
    static ArrayList tests = new ArrayList();

    // stores the percentage values for each test method
    static HashMap testPcts = new HashMap();

    // the number of tests run
    static volatile int testsRun = 0;

    // the amount of time all tests have taken
    static volatile long totalTime = 0;

    // the time at which things got initialized
    static final long inviteStartTime = System.currentTimeMillis();

    // stores the stats for individual methods
    static HashMap testStats = new HashMap();

    // frequency with which to show the stats, in number of tests; 0 means never show the stats
    static int statsFreq = 0;
    
    // maximum allowed overhead; 0 means no limit
    static float maxOverhead = 0;

    // to write to a log file
    static PrintWriter out;

    // helper method
    static final void println(String s){ System.out.println(s); }


    
    //pointcut goCut(): cflow(this(Demo) && execution(void go()));

    pointcut demoExecs(): !within(InsertTest) && !execution(* test*()) && !execution(* main(..)) && execution(!static * *(..)) && execution(* *(..));
    
    Object around(): demoExecs() // {&& !execution(* go())  { //&& goCut() {
    {   
	if (!inTest)
	{
	    //System.out.println("Time for a test");
	    //String className = thisJoinPointStaticPart.getSignature().getDeclaringType().getName();
	    //String methodName = thisJoinPointStaticPart.getSignature().getName();
	    //println("\nMethod: " + className + "." + methodName);

	    launchTest(thisJoinPoint);
	    //launchRandomTest();

	    //println("Running original method:" );
	    Object result = proceed();
	    //println("  result: " + result );
	    return result;
	}
	else 
	    {
		//System.out.println("I'm in a test right now");
		return proceed();
	    }
    }
    
    static private void launchTest(JoinPoint jp) 
    {
	// used for timing how long this takes
	long startTime = System.currentTimeMillis();

	if (!initialized) initialize();

	/*
	println("Arguments: " );
	Object[] args = jp.getArgs();
	String[] names = ((CodeSignature)jp.getSignature()).getParameterNames();
	Class[] types = ((CodeSignature)jp.getSignature()).getParameterTypes();
	
	for (int i = 0; i < args.length; i++) {
	    println("  "  + i + ". " + names[i] +
		    " : " +            types[i].getName() +
		    " = " +            args[i]);
	}
	*/

	// get the object
	Object target = jp.getTarget();

	// figure out the name of the test method
	String name = jp.getSignature().getName();
	String fullName = target.getClass().getName() + "." + name;
	//System.out.println("METHOD?" + fullName);
	
	// update number of times this particular method was called
	Stats s = (Stats)(testStats.get(fullName));
	if (s == null) s = new Stats();
	s.methodCalls++;
	testStats.put(fullName, s);
	

	// if we know there is no unit test, just return
	if (noTest.contains(fullName)) 
	{
	    //System.out.println(fullName + " has no test");
	    return;
	}

	// the probability with which this test will run
	double p = 0.0;

	// figure out the percentage for this method
	if (testPcts.containsKey(fullName))
	{
	    //p = testPcts.get(fullName).doubleValue();
	    p = ((Double)(testPcts.get(fullName))).doubleValue();
	}
	else if (testPcts.containsKey("DEFAULT"))
	{
	    // if no value is specified, try to use the default
	    //p = testPcts.get("DEFAULT").doubleValue();
	    p = ((Double)(testPcts.get("DEFAULT"))).doubleValue();
	}
	// NB: if the default is not specified either, then p = 0
	

	// only execute the test a certain percent of the time
	double go = Math.random();
	if (go >= p)
	{
	    //System.out.println("skipping this test " + go + " " + p);
	    return;
	}
	//else
	//System.out.println("LET'S DO IT! " + go + " " + p);

	// secondary test to ensure we're not exceeding maximum specified overhead
	long runningTime = System.currentTimeMillis() - inviteStartTime;
	float overhead = ((float)totalTime) * 100 / (runningTime - (float)totalTime);
	
	if(maxOverhead != 0 && overhead > maxOverhead - 1)
	{
		//System.out.print("totalTime:" + totalTime + " ");
		//System.out.println("exceeding overhead (" + overhead + "/" + maxOverhead + ") ... scaling back");
		return;
	}

	String firstChar = name.substring(0, 1).toUpperCase();
	String rest = name.substring(1, name.length());
	String testName = "test" + firstChar + rest;
	//System.out.println("TEST?" + testName);
	
	// now invoke the method
	try
	{
	    if (target == null) { System.out.println("target is null in " + name); return; }

	    Method m = target.getClass().getMethod(testName, null);
	    //inTest = true;
		  
	    // do the fork
	    int pid = forker.fork();

	    // try { Thread.sleep(15000); } catch (Exception e) { }

	    if (pid == 0) // this is the child, i.e. the test process
	    {
		inTest = true;
		try
		{
		    // this is the child, so run the test, then end
		    boolean result = ((Boolean)m.invoke(target, null)).booleanValue();
		    if (!result) 
		    {
			System.out.println("oops -- test failed " + fullName);
			out.println("FAIL " + fullName + new Date());
		    }
		    else
		    { 
			System.out.println("test passed");
			out.println("PASS " + fullName + " " + new Date());
		    }
		}
		finally
		{
		    out.flush();
		    System.out.println("flushed");
		    forker.exit();
		    //Runtime.getRuntime().exec("/home/cmurphy/invite/killabandoned");
		    System.exit(0);
		    System.out.println("Still... alive");
		}
	    }
	    else // this is the parent, i.e. the original process
	    {
		// update the global stats
		long endTime = System.currentTimeMillis();
		totalTime += endTime - startTime;
		testsRun++;

		// update the stats for this particular method
		s = (Stats)(testStats.get(fullName));
		if (s == null) s = new Stats();
		s.testsRun++;
		s.totalTime += endTime - startTime;
		testStats.put(fullName, s);
		


		// after every some-odd number of tests, report the stats
		if (statsFreq != 0 && testsRun % statsFreq == 0)
		{
		    //System.out.println("totalTime=" + totalTime + "ms testsRun=" + testsRun);
		    float average = ((float)totalTime) / testsRun;
		    System.out.println("Avg time to execute test: " + average + "ms");
		    float rate = (float)(testsRun * 1000) / runningTime;
		    System.out.println("RunningTime=" + runningTime + "ms Rate=" + rate + "tests/sec");
	    	    runningTime = System.currentTimeMillis() - inviteStartTime;
		    
		    overhead = ((float)totalTime) * 100 / (runningTime - (float)totalTime);
		    System.out.println("Overhead=" + overhead + "%");

		    Set keys = testStats.keySet();
		    for (int i = 0; i < keys.size(); i++)
		    {
			String method = keys.toArray()[i].toString();
			s = (Stats)(testStats.get(method));
			average = ((float)s.totalTime) / s.testsRun;
			rate = (float)(s.testsRun * 1000) / runningTime;
			// TO DO: print out the p value as well
			System.out.println(method + ": Total Calls:" + s.methodCalls + " Tests:" + s.testsRun + " Avg:" + average + "ms Rate:" + rate + "tests/sec");
		    }

		}
	    }

	}
	catch (NoSuchMethodException e)
	{
	    // this exception happens when there is no appropriate test method
	    noTest.add(fullName);
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	    System.exit(0);
	}
	finally
	{
	    inTest = false;
	}
    }


    static private void initialize()
    {
	try
	{
	    Scanner in = new Scanner(new File(configFile));

	    while (in.hasNext())
	    {
		// read the first part of the line
		String key = in.next();

		if (key.equals("LOG")) // if it's the name of the log file
		{
		    try
		    {
			String logFile = in.nextLine().trim();
			out = new PrintWriter(logFile);
			//System.out.println("logFile: " + logFile);
		    }
		    catch (Exception e) 
		    {
			e.printStackTrace();
		    }
		}
		else if (key.equals("OVERHEAD")) // the frequency with which to show stats
		{
		    String s = in.nextLine().trim();
		    maxOverhead = Float.parseFloat(s);
		}
		else if (key.equals("STATS")) // the frequency with which to show stats
		{
		    String s = in.nextLine().trim();
		    statsFreq = Integer.parseInt(s);
		    //System.out.println("STATS: " + statsFreq);
		}
		else if (key.startsWith("#")) // it's a comment
		{
		    in.nextLine();
		}
		else 	// specify the p values for each method
		{
		    String p = in.nextLine().trim();
		    testPcts.put(key, Double.valueOf(p));
		    //System.out.println(key + ": " + p);
		}
	    }

	    initialized = true;
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	    System.exit(0);
	}

    }



    /************************************************************************

     Below here is old stuff that we probably won't use anymore!!!




    static private void launchRandomTest() 
    {

	if (!initialized) initializeRandom();

	if (tests.size() == 0) return;

	double go = Math.random();
	if (go >= TEST_PCT)
	{
	    //System.out.println("skipping this test");
	    return;
	}


	// pick from one of the tests at Random
	int index = (int)(Math.random() * tests.size());

	String methodName = (String)tests.get(index);
	//System.out.println("About to execute " + method);

	// get the method name separate from the class name
	index = methodName.lastIndexOf(".");
	String className = methodName.substring(0, index);
	methodName = methodName.substring(index+1);

	//System.out.println("className=" + className + "; method=" + methodName);


	// now invoke the method
	try
	{
	    Class c = Class.forName(className);
	    Method m = c.getMethod(methodName, null);

	    Constructor cons = null;
	    Object o = null;

	    try
	    {
		Class[] classes = { String.class };
		// this assumes there's a constructor with a String argument
		cons = c.getConstructor(classes);

		Object[] args = { "test"  };
		o = cons.newInstance(args);
	    }
	    catch (NoSuchMethodException e)
	    {
		// in case there's no constructor with a String
		Class[] classes = {  };
		cons = c.getConstructor(classes);

		Object[] args = {  };
		o = cons.newInstance(args);
	    }


	    inTest = true;
		  
	    // do the fork
	    long start = System.currentTimeMillis();
	    int pid = forker.fork();
	    long end = System.currentTimeMillis();

	    // try { Thread.sleep(15000); } catch (Exception e) { }

	    //++testsRun;
	    //totalTime += (end - start);

	    if (pid == 0)
	    {
		//System.out.println("Tests=" + testsRun + "; time=" + totalTime + "; Average: " + totalTime/testsRun);

		// this is the child, so run the test, then end
		try
		{
		    try
		    {
			// first do the setup
			Method setUp = c.getMethod("setUp", null);
			setUp.invoke(o, null);
			//System.out.println("done with setup");
		    }
		    catch (NoSuchMethodException e) {  }

		    // now the test method
		    m.invoke(o, null);
		    //System.out.println(className + "." + methodName + " passed");

		    // then tear down
		    try
		    {
			Method tearDown = c.getMethod("tearDown", null);
			tearDown.invoke(o, null);
		    }
		    catch (NoSuchMethodException e) {  }
		    
		    //System.out.println("test passed " + className + "." + methodName);
		}
		catch (Exception e)
		{
		    // see if it's a JUnit error
		    if (e.getCause() != null && e.getCause() instanceof junit.framework.AssertionFailedError)
		    {
			System.out.println("oops -- test failed " + className + "." + methodName);
			e.printStackTrace();
			//if (e.getCause() != null) e.getCause().printStackTrace();
		    }
		    else
		    {
			System.out.println("An unexpected error occurred " + className + "." + methodName);
			//e.printStackTrace();
			if (e.getCause() != null) e.getCause().printStackTrace();
		    }
		}
		finally
		{
		    try { forker.exit(); } catch (Exception e) { }
		    try { System.exit(0); } catch (Exception e) { }
		    System.out.println("Still... alive");
		}
	    }

	}
	catch (NoSuchMethodException e)
	{
	    // this exception happens when there is no appropriate test method
	    System.out.println("NoSuchMethod???" + className + "." + methodName);
	    e.printStackTrace();
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	    System.exit(0);
	}
	finally
	{
	    inTest = false;
	}
   }

    static private void initializeRandom()
    {
	// the names of the classes should come from a text file or something, obviously
	String[] classes = { "org.mortbay.io.BufferCacheTest" };
	//String[] classes = { "org.mortbay.io.BufferCacheTest", "org.mortbay.util.DateCacheTest", "org.mortbay.util.LazyListTest", "org.mortbay.util.StringMapTest", "org.mortbay.util.StringUtilTest", "org.mortbay.util.URITest", "org.mortbay.util.URLEncodedTest", "org.mortbay.io.BufferTest", "org.mortbay.io.BufferUtilTest", "org.mortbay.thread.TimeoutTest", "org.mortbay.jetty.HttpHeaderTest", "org.mortbay.jetty.HttpParserTest", "org.mortbay.jetty.HttpURITest", "org.mortbay.jetty.ResourceCacheTest", "org.mortbay.jetty.ResponseTest", "org.mortbay.jetty.RequestTest", "org.mortbay.jetty.HttpConnectionTest" };
	//String[] classes = { "org.mortbay.jetty.ResponseTest", "org.mortbay.jetty.RequestTest", "org.mortbay.jetty.HttpConnectionTest" };

	// for each class, find all of the testX methods and put the names in the "tests" TreeSet
	for (int i = 0; i < classes.length; i++)
	{
	    String className = classes[i];
	    try
	    {
		Class c = Class.forName(className);

		Method[] methods = c.getDeclaredMethods();

		for (int j = 0; j < methods.length; j++)
		{
		    String methodName = methods[j].getName();
		    if (methodName.startsWith("test"))
		    {
			String fullname = className + "." + methodName;
			System.out.println("TEST: " + fullname);
			tests.add(fullname);
		    }
		}
	    }
	    catch (Exception e)
	    {
		e.printStackTrace();
	    }
	}

	initialized = true;
    }

    *****************************************/
}
