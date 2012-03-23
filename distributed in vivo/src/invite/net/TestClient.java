package invite.net;

import java.io.*;
import java.net.*;

import java.util.*;

import invite.net.message.*;

public class TestClient extends Thread
{
    public static final void main(String[] args)
    {
        TestClient me = new TestClient("localhost", 4444);
        me.start();
    }

    private String hostName = null;
    private int portNum = -1;

    public TestClient(String hostName, int portNum)
    {
        super();
        this.hostName = hostName;
        this.portNum = portNum;
    }

    public void run()
    {
        Random random = new Random();

        int numMillisTillLogIn = random.nextInt(3000);
        int numMessages = random.nextInt(3) + 1;
        int[] waitTimesMillis = new int[numMessages];
        for (int i = 0; i < waitTimesMillis.length; i ++)
        {
            waitTimesMillis[i] = random.nextInt(3000);
        }

        System.out.println("I will wait for " + numMillisTillLogIn + " ms to log in.");
        System.out.println("I am going to randomly send out " + numMessages + " messages");
        System.out.println("The wait times between messages is: ");
        for (int i = 0; i < waitTimesMillis.length; i ++)
        {
            System.out.print(waitTimesMillis[i] + " ");
        }
        System.out.println("");

        // now we can make a client and do the tests
        waitForSure(numMillisTillLogIn);

        // first we log in
        Client client = new Client(this.hostName, this.portNum);
        Id id = client.getId();
        client.close();

        System.out.println("Logged in; got id of " + id);

        // then, start the client reporting thread
        ClientReportingThread.init(this.hostName, this.portNum, id);

        // next, we get the tests
        client = new Client(this.hostName, this.portNum, id);
        List<String> list = client.getTests();
        client.close();

        System.out.println("Got " + list.size() + " tests.");
        for (int i = 0; i < list.size(); i ++)
        {
            System.out.print(list.get(i) + " ");
        }
        System.out.println("");

        for (int i = 0; i < waitTimesMillis.length; i ++)
        {
            waitForSure(waitTimesMillis[i]);

            // randomly:

            // 1) pick a "test"
            int randomTestIndex = random.nextInt(list.size());
            
            // 2) pass or fail
            boolean pass = 
                random.nextInt(2) == 0 
                    ? true 
                    : false;

            // 2a) make exception if necessary
            Exception exception = 
                pass 
                    ? null 
                    : new Exception("We failed \"" + list.get(randomTestIndex) + "\" :(");

            client = new Client(this.hostName, this.portNum, id);
            client.sendReport(
            //client.sendReportLocally(
                new Report(
                    //id, 
                    list.get(randomTestIndex), 
                    pass,
                    exception
                    )
                );
            client.close();

            System.out.println("Report #" + i + " added to directory queue.");
        }

        System.out.println("Sleeping for 3000 ms");
        waitForSure(3000);

        ClientReportingThread.stopMonitoring();

        System.out.println("Done");
    }

    // Avoid spurious wake-ups (see wait() method in Javadoc)
    private void waitForSure(long millis)
    {
        try
        {
            this.sleep(millis);
        }
        catch (InterruptedException ex)
        {
            ex.printStackTrace();
        }
    }
}
