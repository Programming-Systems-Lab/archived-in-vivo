package invite.db;

import java.util.*;
import java.lang.reflect.*;

import invite.net.message.*;

public class TestsDB
{
    private static List<String> tests = new ArrayList<String>();
    private static Map<Id, List<String>> testAssignments = new HashMap<Id, List<String>>();

    // TODO pass in a file name
    //public static void init(String classesFileName)
    public static void init()
        throws ClassNotFoundException
    {
        // the names of the classes should come from a text file or something, obviously
        //String[] classes = { "org.mortbay.io.BufferCacheTest" };
        //String[] classes = { "org.mortbay.io.BufferCacheTest", "org.mortbay.util.DateCacheTest", "org.mortbay.util.LazyListTest", "org.mortbay.util.StringMapTest", "org.mortbay.util.StringUtilTest", "org.mortbay.util.URITest", "org.mortbay.util.URLEncodedTest", "org.mortbay.io.BufferTest", "org.mortbay.io.BufferUtilTest", "org.mortbay.thread.TimeoutTest", "org.mortbay.jetty.HttpHeaderTest", "org.mortbay.jetty.HttpParserTest", "org.mortbay.jetty.HttpURITest", "org.mortbay.jetty.ResourceCacheTest", "org.mortbay.jetty.ResponseTest", "org.mortbay.jetty.RequestTest", "org.mortbay.jetty.HttpConnectionTest" };
        //String[] classes = { "org.mortbay.jetty.ResponseTest", "org.mortbay.jetty.RequestTest", "org.mortbay.jetty.HttpConnectionTest" };
        List<String> classes = new ArrayList<String>();
        classes.add("org.mortbay.io.BufferCacheTest");
        //classes.add("org.mortbay.jetty.HttpConnectionTest");

        // for each class, find all of the testX methods and put the names in the "tests" list
        for (String className : classes)
        {
            Class c = Class.forName(className);

            Method[] methods = c.getDeclaredMethods();

            for (int j = 0; j < methods.length; j++)
            {
                String methodName = methods[j].getName();
                if (methodName.startsWith("test"))
                {
                    String fullname = className + "." + methodName;
                    System.out.println("\tADDED: " + fullname);
                    tests.add(fullname);
                }
            }
        }
    }

    // currently, returns a subset based on how many unique clients there are
    public static synchronized List<String> getSubset(Id id)
    {
        /*
        List<String> results = new ArrayList<String>();
        results.add(tests.get(0));
        //results.add("org.mortbay.io.BufferCacheTest.testLookupPartialBuffer");
        System.out.println("Gave to " + id + " 1 tests: " + tests.get(0));
        return results;
        */
        return testAssignments.get(id);
    }

    public static synchronized List<String> getAll()
    {
        return tests;
    }
 
    public static void addNewId(Id id)
    {
        testAssignments.put(id, new ArrayList<String>());
    }

    // re-divide the tests equally among all ids
    // TODO what to do if there aren't enough tests?
    public static void repartitionUniformily()
    {
        List<String> allTests = TestsDB.getAll();

        // first, clear everything
        for (List<String> testList : testAssignments.values())
        {
             testList.clear();
        }

        // now, assign
        int counter = 0;
        int numTestsPerClient = allTests.size() / IdDB.getNumClients();
        //for (Id id : testAssignments.keySet())
        for (List<String> testList : testAssignments.values())
        {
            for (int i = 0; i < numTestsPerClient; i ++)
            {
                testList.add(allTests.get(counter));
                counter ++;
            }
        }

        // TODO redistribute the remaining ones, unfairly
    }
}
