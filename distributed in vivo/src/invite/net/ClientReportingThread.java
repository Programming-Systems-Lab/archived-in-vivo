package invite.net;

import java.util.*;
import java.io.*;

import invite.net.*;
import invite.net.message.*;

public class ClientReportingThread extends Thread
{
    private String workingDirName = null;
    private int sleepDurationMillis = -1;

    private String hostName = null;
    private int portNum = -1;
    private Id id = null;

    private static ClientReportingThread me;
    private static boolean continueMonitoring;
    public static void init(String hostName, int portNum, Id id)
    {
        if (Client.SEND_REPORTS_LOCALLY)
        {
            me = new ClientReportingThread(hostName, portNum, id);
            me.start();
            continueMonitoring = true;
        }
    }
    public static void stopMonitoring()
    {
        if (Client.SEND_REPORTS_LOCALLY)
        {
            getInstance().stopMonitoringThread();
        }
    }
    public static ClientReportingThread getInstance()
    {
        if (Client.SEND_REPORTS_LOCALLY)
            return me;
        else
            throw new RuntimeException("Client.SEND_REPORTS_LOCALLY disabled!");
    }
    public static void sendReports()
    {
        if (Client.SEND_REPORTS_LOCALLY)
            me.notify();
    }
    /*
    public static void stopMonitoring()
    {
        continueMonitoring = false;
    }
    */

    private ClientReportingThread(String hostName, int portNum, Id id)
    {
        workingDirName = "./hello" + ((int) (Math.random() * 100));
        sleepDurationMillis = 500;

        this.hostName = hostName;
        this.portNum = portNum;
        this.id = id;

        // make the dir, including parents if necessary
        File workingDir = new File(workingDirName);
        workingDir.mkdirs();
    }

    private void stopMonitoringThread()
    {
        this.continueMonitoring = false;
    }

    public String getWorkingDirName()
    {
        return workingDirName;
    }

    public void run()
    {
        System.out.println("Started ClientReportingThread.");
        while (continueMonitoring)
        {
            File dir = new File(workingDirName);

            String[] fileNames = dir.list();

            System.out.println(
                "Found " + fileNames.length + " reports in directory \"" + workingDirName + "\".");
            for (int i = 0; i < fileNames.length; i ++)
            {
                String fileName = fileNames[i];

                System.out.println("Remotely sending report: " + fileName);
                sendReport(fileName);

                File file = new File(workingDirName + "/" + fileName);
                boolean deleted = file.delete();
                System.out.println(workingDirName + "/" + fileName + " was " + (deleted ? "" : "NOT ") + "deleted.");
            }

            try
            {
                sleep(sleepDurationMillis);
            }
            catch (InterruptedException ex)
            {
                //ex.printStackTrace();
                System.out.println("Got woken up");
            }
        }

        // TODO clean up dirs

        System.out.println("Closing thread.");
    }

    private void sendReport(String fileName)
    {
        try
        {
            ObjectInputStream ois =
                new ObjectInputStream(
                    new FileInputStream(
                        workingDirName + "/" + fileName
                        )
                    );
            Report report = (Report) ois.readObject();
            ois.close();

            //System.out.println(report);
            Client client = new Client(hostName, portNum, id);
            client.sendReport(report);
            client.close();
        }
        catch (FileNotFoundException ex)
        {
            ex.printStackTrace();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        catch (ClassNotFoundException ex)
        {
            ex.printStackTrace();
        }
    }
}
