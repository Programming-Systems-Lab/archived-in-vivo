package invite.forker;

import java.util.*;

import invite.net.*;
import invite.net.message.*;

public class TestForker
{
    public static final void main(String[] args)
    {
/*
        Client client = new Client("localhost", 4444);
        Id id = client.getId();
        client.close();

        ClientReportingThread.init("localhost", 4444, new Id(10));

        client = new Client("localhost", 4444, id);
        List<String> list = client.getTests();
        System.out.println("Got " + list.size() + " tests.");
        client.close();

        // we need this info before the fork
        //String workingDirName = ClientReportingThread.getInstance().getWorkingDirName();
        //String workingDirName = ".";
        //System.out.println("Got workingDirName: " + workingDirName);

        Forker forker = new Forker();
        int pid = forker.fork();

        System.out.println("pid = " + pid);

        if (pid == 0)
        {
            System.out.println("Forked");
            //client = new Client("localhost", 4444);
            System.out.println("Making client");
            //client = new Client();
            //System.out.println("Started new client");
            //System.out.println("Opened socket.");
            Integer ii = new Integer(3);
            System.out.println(ii);
            Report report = null;
            System.out.println("Report is " + report);
            report = new Report("I am " + pid, true);
            System.out.println("Made report = " + report);
            System.out.println("Sending report locally");

            client = new Client("localhost", 4444);
            client.sendReport(report);
            //client.sendReportLocally(report, workingDirName);
            System.out.println("Sent.");
            //System.out.println("Finished sending, going to close");
            client.close();
            //System.out.println("Closed");

            // This breaks it
            //try { Thread.sleep(3000); } catch (Exception ex) { } 

            //System.exit(0);
            System.out.println("Exiting");
            forker.exit();
        }
        else
        {
            Report report = new Report("Hello", true);
            System.out.println("Parent's report = " + report);
            //try { Thread.sleep(30000); } catch (Exception ex) { } 
            //busySleep(10000);
        }

        try { Thread.sleep(3000); } catch (Exception ex) { } 

        ClientReportingThread.stopMonitoring();
        System.out.println(pid + " is done!");
*/
    }

    private static final void busySleep(int ms)
    {
        long start = System.currentTimeMillis();

        while (System.currentTimeMillis() < start + ms)
        {
            int[] i = new int[1000000];
            for (int j = 0; j < i.length; j ++)
            {
                i[j] = (int) (Math.random() * i.length);
            }
            for (int j = 0; j < i.length; j ++)
            {
                for (int k = 0; k < i.length; k ++)
                {
                    int temp = i[j];
                    i[j] = i[k];
                    i[k] = temp;
                }
            }
        }
    }
}
