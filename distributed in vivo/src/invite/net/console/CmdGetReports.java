package invite.net.console;

import java.util.*;

import invite.net.*;
import invite.net.message.*;

public class CmdGetReports extends AAction
{
    public CmdGetReports(String command)
    {
        super(command);
    }

    public void act()
    {
        println("Getting reports from " + hostName + ":" + portNum + "." + "\n");
        
        connect();
        List<Report> reports = client.getReports();
        close();

        println("Got " + reports.size() + " reports:");
        for (Report report : reports)
        {
            println(report);
        }

        println("");
    }
}
