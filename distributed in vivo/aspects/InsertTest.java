//package invite.instrumenter; 
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.CodeSignature;
import java.lang.reflect.*;
import java.util.*;
import java.io.*;

import org.mortbay.io.*;

import invite.net.*;
import invite.net.message.*;
import invite.forker.*;
import invite.etc.*;

aspect InsertTest
{
    private static String hostName = null;
    private static int portNum = -1;

    // variables related to forking, running the tests, etc.
    static Forker forker = new Forker();
    // whether or not we're currently executing a test
    static volatile boolean inTest = false;
    // whether or not the test harness has been initialized
    static boolean initialized = false;
    // stores the names of all the test methods
    static List<String> tests = new ArrayList<String>();

    // Settings to control how often the tests run
    //static boolean FORK = true;
    //static boolean NEVER = false;
    //static boolean ONLY_ONCE = false;
    //static boolean PARENT_SLEEP = false;
    // percent of tests to execute (0 to 1.0) ... for 1% tests, set to 0.0002.... for 10%, set to 0.002
    //static double TEST_PCT = .002;
    private static final boolean ENABLE_INVITE = true;

    // useful "local" "globals"
    private static Id id = null;
    private static Schedule schedule = null;

    static final void println(String s){ System.out.println(s); }

    // Counters for statistics
    private static int timesCalled = 0;
    private static int timesRun = 0;

    //pointcut goCut(): cflow(this(Demo) && execution(void go()));

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
        !within(InsertTest) && 
        !execution(* test*()) && 
        !execution(* main(..)) && 
        execution(!static * *(..)) && 
        execution(* *(..))
        //! initialization(*.new(*))
        ;
    
    Object around(): demoExecs() // {&& !execution(* go())  { //&& goCut() {
    {
        if (! ENABLE_INVITE) 
        {
            return proceed();
        }

        if (! inTest)
        {
            launchRandomTest();
        }

        return proceed();
    }

    private static boolean toRunOrNotToRun()
    {
        //if (NEVER) return false;
        timesCalled ++;

        if (! initialized)
        {
            initialize();
        }
        /*
        else if (ONLY_ONCE)
        {
            return false;
        }
        */

        if (tests.size() == 0) return false;
        //if (true) return;

        double go = Math.random();
        //if (go >= TEST_PCT)
        if (go >= schedule.executionPercent)
        //if (false)
        {
            //System.out.println("skipping this test");
            return false;
        }

        //System.out.println("NOT skipping this test");

        timesRun ++;

        return true;
    }

    private static void execMethodIgnore(Class c, Object o, String methodName)
        throws InvocationTargetException
    {
        try
        {
            execMethod(c, o, methodName);
        }
        catch (NoSuchMethodException ex)
        {
            // ignore this
        }
    }

    private static void execMethod(Class c, Object o, String methodName)
        throws NoSuchMethodException, InvocationTargetException
    {
        try
        {
            // first do the setup
            //Method setUp = c.getMethod("setUp", null);
            Method m = c.getMethod(methodName, null);
            m.invoke(o, null);
            //System.out.println("done with setup");
        }
        catch (NoSuchMethodException ex)
        {
            throw ex;
        }
        catch (InvocationTargetException ex)
        {
            throw ex;
        }
        catch (Exception ex)
        {
            // ignore?
        }
    }

    private static Report runTest(Class c, Object o, String className, String testName)
    {
        Report report;

        try
        {
            execMethodIgnore(c, o, "setUp");

            /*
            Method m = c.getMethod(testName, null);
            m.invoke(o, null);
            //System.out.println(className + "." + methodName + " passed");
            */
            execMethodIgnore(c, o, testName);

            execMethodIgnore(c, o, "tearDown");

            report = new Report(
                className + "." + testName,
                true
                );
        }
        catch (InvocationTargetException ex)
        {
            report = new Report(
                className + "."+ testName,
                false,
                ex.getTargetException()
                );
        }

        return report;
    }

    private static void launchRandomTest() 
    {
        if (! toRunOrNotToRun()) return;

        // pick from one of the tests at Random
        int index = (int)(Math.random() * tests.size());

        String methodName = (String)tests.get(index);
        //System.out.println("About to execute " + methodName);

        // get the method name separate from the class name
        index = methodName.lastIndexOf(".");
        String className = methodName.substring(0, index);
        methodName = methodName.substring(index+1);

        //System.out.println("className=" + className + "; method=" + methodName);

        int pid = -1;

        // now prepare to run the tests
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
            /*
            if (FORK)
            {
                pid = forker.fork();
                //System.out.println("Forked! pid = " + pid);
            }
            else
            {
                pid = 0;
            }
            */
            pid = forker.fork();
            long end = System.currentTimeMillis();

            if (pid == 0)
            {
                Report report = runTest(c, o, className, methodName);

                //System.out.println("test completed " + className + "." + methodName);

                //System.out.println("Sending report");
                Client client = new Client(hostName, portNum, id);
                //System.out.println("local port = " + client.getLocalPort());
                //System.out.println("remote port = " + client.getRemotePort());
                
                report.setTestName("timesCalled = " + timesCalled + ", " + "timesRun = " + timesRun);

                client.sendReport(report);
                client.close();

                forker.exit();
            }
        }
        catch (NoSuchMethodException e)
        {
            // this exception happens when there is no appropriate test method
            System.out.println("NoSuchMethod??? " + className + "." + methodName);
            //e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(0);
        }
        finally
        {
            inTest = false;
            //System.out.println("Nested finally. pid = " + pid);
        }
    }

    private static void initialize()
    {
        initialized = true;
        Client client = null;

        Config.init("invite.conf");

        hostName = Config.get("hostName");
        portNum = Config.getInt("portNum");

        client = new Client(hostName, portNum);
        id = client.getId();
        client.close();

        client = new Client(hostName, portNum, id);
        schedule = client.getSchedule();
        client.close();

        client = new Client(hostName, portNum, id);
        tests = client.getTests();
        client.close();

        System.out.println("\n");

        System.out.println("I am: " + id);
        System.out.println("Got " + tests.size() + " tests: ");
        for (String test : tests)
        {
            System.out.println("\t" + test);
        }
        System.out.println(schedule);
        System.out.println("\n");

        ClientReportingThread.init(hostName, portNum, id);
    }
}
