package invite.net;

import java.io.*;
import java.net.*;
import java.util.*;

import invite.net.message.*;

public class ConsoleClient extends Client
{
    public ConsoleClient(String hostName, int portNum)
    {
        super(hostName, portNum);
    }

    /**
     * Gets all reports
     */
    public List<Report> getReports()
    {
        try
        {
            o_out.writeObject(new GetReports());
            return (List<Report>) o_in.readObject();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.exit(1);
            return null;
        }
    }

    /**
     * Summarizes:
     * - number of active clients
     * - number of tests per client
     * - number of tests passed per client
     */
    public ClientSummary getClientSummary()
    {
        try
        {
            o_out.writeObject(new GetClientSummary());
            return (ClientSummary) o_in.readObject();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.exit(1);
            return null;
        }
    }
}
