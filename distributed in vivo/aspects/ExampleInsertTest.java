import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.CodeSignature;
import java.lang.reflect.*;
import java.util.*;
import java.io.*;

import org.mortbay.io.*;

import invite.net.*;
import invite.net.message.*;

aspect ExampleInsertTest
{
    static boolean RAN_ONCE = false;
/*
    static boolean FORK = true;
    static boolean NEVER = false;
    static boolean ONLY_ONCE = true;
    static boolean PARENT_SLEEP = false;

    static volatile int testsRun = 0;
    static volatile long totalTime = 0;

    // percent of tests to execute (0 to 1.0) ... for 1% tests, set to 0.0002.... for 10%, set to 0.002
    static final double TEST_PCT = .002;

    // whether or not we're currently executing a test
    static volatile boolean inTest = false;

    static Forker forker = new Forker();

    // stores the names of all methods that don't have a corresponding unit test
    static TreeSet noTest = new TreeSet();

    // whether or not the test harness has been initialized
    static boolean initialized = false;

    static final void println(String s){ System.out.println(s); }

    // stores the names of all the test methods
    static List<String> tests = new ArrayList<String>();
    
    static String hostName;
    static int portNum;
    static Id id;

    //pointcut goCut(): cflow(this(Demo) && execution(void go()));
*/
    /*
    pointcut constructors():
        //call(new (*))
        ! initialization(*.new(*))
        ;

    Object around(): constructors()
    {
        System.out.println("Constructor!");
        return proceed();
    }
    */

    //TODO filter out constructors
    pointcut demoExecs(): 
        !within(ExampleInsertTest) && 
        !execution(* test*()) && 
        !execution(* main(..)) && 
        execution(!static * *(..)) && 
        execution(* *(..))
        //! initialization(*.new(*))
        ;
    
    Object around(): demoExecs() // {&& !execution(* go())  { //&& goCut() {
    {
        if (! RAN_ONCE)
        {
            System.out.println("Hello! This is only going to run once.");
            RAN_ONCE = true;
        }
        return proceed();

/*
        //System.out.println("We're going in!!!");
        if (!inTest)
        {
            //String className = thisJoinPointStaticPart.getSignature().getDeclaringType().getName();
            //String methodName = thisJoinPointStaticPart.getSignature().getName();
            //println("\nMethod: " + className + "." + methodName);

            //launchTest(thisJoinPoint);
            launchRandomTest();

            //println("Running original method:" );
            Object result = proceed();
            //println("  result: " + result );
            return result;
        }
        else 
        {
            return proceed();
        }
*/
    }
    
/*
    static private void launchTest(JoinPoint jp) 
    {
	println("Arguments: " );
	Object[] args = jp.getArgs();
	String[] names = ((CodeSignature)jp.getSignature()).getParameterNames();
	Class[] types = ((CodeSignature)jp.getSignature()).getParameterTypes();
	
	for (int i = 0; i < args.length; i++) {
	    println("  "  + i + ". " + names[i] +
		    " : " +            types[i].getName() +
		    " = " +            args[i]);
	}

	// get the object
	Object target = jp.getTarget();

	// figure out the name of the test method
	String name = jp.getSignature().getName();
	String fullName = target.getClass().getName() + "." + name;
	//System.out.println("METHOD?" + fullName);

	// if we know there is no unit test, just return
	if (noTest.contains(fullName)) 
	{
	    //System.out.println(fullName + " has no test");
	    return;
	}

	// only execute the test a certain percent of the time
	double go = Math.random();
	if (go >= TEST_PCT)
	{
	    //System.out.println("skipping this test");
	    return;
	}
	else
	    System.out.println(go);


	String firstChar = name.substring(0, 1).toUpperCase();
	String rest = name.substring(1, name.length());
	String testName = "test" + firstChar + rest;
	//System.out.println("TEST?" + testName);
	
	// now invoke the method
	try
	{
	    if (target == null) { System.out.println("target is null in " + name); return; }

	    Method m = target.getClass().getMethod(testName, null);
	    inTest = true;
		  
	    // do the fork
	    int pid = forker.fork();

	    // try { Thread.sleep(15000); } catch (Exception e) { }

	    if (pid == 0)
	    {
		try
		{
		    // this is the child, so run the test, then end
		    boolean result = ((Boolean)m.invoke(target, null)).booleanValue();
		    if (!result) System.out.println("oops -- test failed");
		    //else System.out.println("test passed");
		}
		finally
		{
		    forker.exit();
		    System.exit(0);
		    System.out.println("Still... alive");
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
*/

/*
    static private void launchRandomTest() 
    {
        if (NEVER) return;

        if (! initialized)
        {
            initialize();
        }
        else if (ONLY_ONCE)
        {
            return;
        }

        if (tests.size() == 0) return;

        double go = Math.random();
        //if (go >= TEST_PCT)
        if (false)
        {
            System.out.println("skipping this test");
            return;
        }


        // pick from one of the tests at Random
        int index = (int)(Math.random() * tests.size());

        String methodName = (String)tests.get(index);
        System.out.println("About to execute " + methodName);

        // get the method name separate from the class name
        index = methodName.lastIndexOf(".");
        String className = methodName.substring(0, index);
        methodName = methodName.substring(index+1);

        System.out.println("className=" + className + "; method=" + methodName);

        int pid = -1;

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

                Object[] args = { "test" };
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
            if (FORK)
            {
                pid = forker.fork();
                System.out.println("Forked! pid = " + pid);
            }
            else
                pid = 0;
            long end = System.currentTimeMillis();

            //++testsRun;
            //totalTime += (end - start);

            if (pid == 0)
            //if (pid != 0)
            {
                //System.exit(-1);

                //System.out.println("Tests=" + testsRun + "; time=" + totalTime + "; Average: " + totalTime/testsRun);

                Report report = null;

                // this is the child, so run the test, then end
                    try
                    {
                        // first do the setup
                        Method setUp = c.getMethod("setUp", null);
                        setUp.invoke(o, null);
                        System.out.println("done with setup");
                    }
                    catch (NoSuchMethodException e) 
                    {
                        / * we don't care if there's no setUp method * / 
                        System.out.println("No setup");
                    }

                try
                {
                    // now the test method
                    m.invoke(o, null);
                    System.out.println(className + "." + methodName + " passed");

                    report = new Report(
                        className + "." + methodName,
                        true
                        );
                }
                catch (NullPointerException ex)
                {
                    System.out.println("NPE");
                }
                // WHY THE _HELL_ IS THIS NOT CAUGHT BY EXCEPTION???
                catch (InvocationTargetException ex)
                {
                    System.out.println("Invocation Target Exception, arg");
                    report = new Report(
                        className + "."+ methodName,
                        false,
                        ex
                        );
                }
                catch (Error err)
                {
                    System.out.println("Whoops! Error caught!");
                }
                catch (RuntimeException ex)
                {
                    System.out.println("Whoops! RuntimeException caught!");
                }
                catch (Exception e)
                {
                    // see if it's a JUnit error
                    if (e.getCause() != null && e.getCause() instanceof junit.framework.AssertionFailedError)
                    {
                        System.out.println("oops -- test failed " + className + "." + methodName);
                        //e.printStackTrace();
                        //if (e.getCause() != null) e.getCause().printStackTrace();
                    }
                    else
                    {
                        System.out.println("An unexpected error occurred " + className + "." + methodName);
                        //e.printStackTrace();
                        //if (e.getCause() != null) e.getCause().printStackTrace();
                    }
                }
                    / *
                finally
                {
                    //System.out.println("Finally block, forker exiting.");
                    //try { forker.exit(); } catch (Exception e) { System.out.println("Problem!"); }
                    System.out.println("Finally block, System exiting.");
                    try { System.exit(0); } catch (Exception e) { System.out.println("Problem!"); }
                    System.out.println("Still... alive");
                }
                    * /

                    // then tear down
                    try
                    {
                        Method tearDown = c.getMethod("tearDown", null);
                        tearDown.invoke(o, null);
                    }
                    catch (NoSuchMethodException e)
                    {
                        / * it's okay if there's no teardown method * /
                        System.out.println("No teardown");
                    }

                    //System.out.println("test passed " + className + "." + methodName);
                    System.out.println("test completed " + className + "." + methodName);

                    System.out.println("Sending report");
                    Client client = new Client("localhost", 4444);
                    System.out.println("local port = " + client.getLocalPort());
                    System.out.println("remote port = " + client.getRemotePort());
                    client.sendReport(report);
                    client.close();

                    forker.exit();
                / *
                if (FORK)
                {
                    forker.exit();
                    System.exit(-1);
                }
                * /
            }
            / *
            else
            {
                System.exit(-1);
            }
            * /
            / *
            else if (FORK)
            {
                if (PARENT_SLEEP)
                {
                    System.out.println("Parent is Sleeping...");
                    try { Thread.sleep(10000); } catch (Exception e) { }
                    System.out.println("Parent is Awake!");
                }
            }
            * /
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
            System.out.println("Nested finally. pid = " + pid);
        }
    }
*/

/*
    static private void initialize()
    {
        initialized = true;
        Client client = null;

        client = new Client(hostName, portNum);
        id = client.getId();
        client.close();

        client = new Client("localhost", 4444, id);
        tests = client.getTests();
        System.out.println("Got " + tests.size() + " tests: " + tests.get(0) + ".");
        client.close();

        //new ClientReportingThread().start();
        ClientReportingThread.init(hostName, portNum, id);
    }
*/

    /*
    static private void initialize()
    {
        //System.out.println("initializing for the first time.");

        initialized = true;

        // the names of the classes should come from a text file or something, obviously
        String[] classes = { "org.mortbay.io.BufferCacheTest" };
        //String[] classes = { "org.mortbay.io.BufferCacheTest", "org.mortbay.util.DateCacheTest", "org.mortbay.util.LazyListTest", "org.mortbay.util.StringMapTest", "org.mortbay.util.StringUtilTest", "org.mortbay.util.URITest", "org.mortbay.util.URLEncodedTest", "org.mortbay.io.BufferTest", "org.mortbay.io.BufferUtilTest", "org.mortbay.thread.TimeoutTest", "org.mortbay.jetty.HttpHeaderTest", "org.mortbay.jetty.HttpParserTest", "org.mortbay.jetty.HttpURITest", "org.mortbay.jetty.ResourceCacheTest", "org.mortbay.jetty.ResponseTest", "org.mortbay.jetty.RequestTest", "org.mortbay.jetty.HttpConnectionTest" };
        //String[] classes = { "org.mortbay.jetty.ResponseTest", "org.mortbay.jetty.RequestTest", "org.mortbay.jetty.HttpConnectionTest" };

        // for each class, find all of the testX methods and put the names in the "tests" TreeSet
        for (int i = 0; i < classes.length; i++)
        {
            String className = classes[i];
            //System.out.println("We want: " + className);

            try
            {
                //System.out.println("Start try");
                Class c = Class.forName(className);
                //System.out.println("Got: " + c);

                Method[] methods = c.getDeclaredMethods();
                //System.out.println("Got this many methods: " + methods.length);

                for (int j = 0; j < methods.length; j++)
                {
                    String methodName = methods[j].getName();
                    if (methodName.startsWith("test"))
                    {
                        String fullname = className + "." + methodName;
                        //System.out.println("TEST: " + fullname);
                        tests.add(fullname);
                    }
                }
            }
            catch (Exception e)
            {
                //System.out.println("BAAAAD initializing");
                //e.printStackTrace();
            }
        }

        //System.out.println("Initialization all good.");
    }
    */
}
