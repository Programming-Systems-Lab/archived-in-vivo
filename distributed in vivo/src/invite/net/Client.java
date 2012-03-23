package invite.net;

import java.io.*;
import java.net.*;
import java.util.*;

import invite.net.message.*;

public class Client
{
    public static boolean SEND_REPORTS_LOCALLY = false;

    protected Socket socket = null;
    protected ObjectInputStream o_in = null;
    protected ObjectOutputStream o_out = null;

    protected Id id = null;

    protected static int fileId = 1;

    public Client()
    {
    }

    public Client(String hostName, int portNum)
    {
        this.connect(hostName, portNum);
    }

    public Client(String hostName, int portNum, Id id)
    {
        this(hostName, portNum);
        logIn(id);
    }

    public int getLocalPort()
    {
        return this.socket.getLocalPort();
    }

    public int getRemotePort()
    {
        return this.socket.getPort();
    }

    private void connect(String hostName, int portNum)
    {
        try
        {
            socket = new Socket(hostName, portNum);
            //System.out.println("Got a socket");
            o_out = new ObjectOutputStream(socket.getOutputStream());
            //System.out.println("Finished out, now in.");
            o_in = new ObjectInputStream(socket.getInputStream());
        }
        catch (UnknownHostException ex)
        {
            System.err.println("Don't know about host: " + hostName + ".");
            ex.printStackTrace();
            System.exit(1);
        }
        catch (IOException ex)
        {
            //System.err.println("invite.net.CliCouldn't get I/O for the connection to: " + hostName + ".");
            System.out.println("Error: " + hostName + ":" + portNum);
            ex.printStackTrace();
            System.exit(1);
        }
    }

    public void close()
    {
        try
        {
            o_in.close();
            o_out.close();
            socket.close();

            //System.out.println("Finished!");
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    public Id getId()
    {
        try
        {
            o_out.writeObject(new GetId());
            Id id = (Id) o_in.readObject();
            return id;
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        catch (ClassNotFoundException ex)
        {
            ex.printStackTrace();
        }
        System.exit(1);
        return null;
    }

    //public void logIn(Id id)
    private void logIn(Id id)
    {
        try
        {
            o_out.writeObject(id);
            Id receivedId = (Id) o_in.readObject();
            //return id;
            
            // TODO does it matter which version to use, the local or the received?
            this.id = id;
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        catch (ClassNotFoundException ex)
        {
            ex.printStackTrace();
        }
        //System.exit(1);
        //return null;
    }

    public Schedule getSchedule()
    {
        try
        {
            o_out.writeObject(new GetSchedule());
            Schedule schedule = (Schedule) o_in.readObject();
            return schedule;
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        catch (ClassNotFoundException ex)
        {
            ex.printStackTrace();
        }
        System.exit(1);
        return null;
    }

    public List<String> getTests()
    {
        //logIn(id);

        try
        {
            o_out.writeObject(new GetTests());
            return (List<String>) o_in.readObject();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        catch (ClassNotFoundException ex)
        {
            ex.printStackTrace();
        }
        System.exit(1);
        return null;
    }

    public void sendReport(Report report)
    {
        if (SEND_REPORTS_LOCALLY)
        {
            sendReportLocally(report, ClientReportingThread.getInstance().getWorkingDirName());
        }
        else
        {
            sendReportRemotely(report);
        }
    }

    private void sendReportRemotely(Report report)
    {
        try
        {
            //System.out.println("CLIENT: Sending report!");
            //System.out.println(report);
            o_out.writeObject(report);
            //System.out.println("CLIENT: Report sent");
            Object whatever = o_in.readObject();
            //System.out.println("CLIENT: Got whatever");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    private void sendReportLocally(Report report, String workingDirName)
    {
        //System.out.println("Starting to get OOS on workingDir = " + workingDirName);
        try
        {
            ObjectOutputStream oos = 
                new ObjectOutputStream(
                    new FileOutputStream(
                        workingDirName + "/" + fileId
                        )
                    );
            //System.out.println("Got ObjectOutputStream: " + oos);
            oos.writeObject(report);
            //System.out.println("Locally sent report object: " + report);
            oos.close();
            //System.out.println("Closed OOS");

            fileId ++;
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    public void sendReportLocally(Report report)
    {
        ClientReportingThread crt = ClientReportingThread.getInstance();
        //System.out.println("Got instance: " + crt);
        String workingDirName = crt.getWorkingDirName();
        //System.out.println("Got got working dir name: " + workingDirName);

        this.sendReportLocally(report, workingDirName);
    }
}
