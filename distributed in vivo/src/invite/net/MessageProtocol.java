package invite.net;

import java.net.*;
import java.io.*;
import java.util.*;

import invite.net.message.*;
import invite.db.*;

public class MessageProtocol
{
    private boolean finished = false;
    private Id id = null;

    private static void debug(String s)
    {
        if (false) System.out.println(s);
    }

    public Object process(Object inputObject)
    {
        if (inputObject instanceof GetId)
        {
            debug("Got GetId Message");
            finished = true;
            return IdDB.getNextFreeId();
        }

        if (inputObject instanceof Id)
        {
            debug("Got Id Message");
            //finished = true;
            this.id = (Id) inputObject;
            // TODO check that this is a valid id
            return this.id;
        }

        if (inputObject instanceof GetSchedule)
        {
            debug("Got GetSchedule Message");
            finished = true;
            return ScheduleDB.getNewSchedule(this.id);
        }

        if (inputObject instanceof GetTests)
        {
            debug("Got GetTests Message");
            finished = true;
            //return TestsDB.getSubset(id);
            return TestsDB.getAll();
        }

        if (inputObject instanceof Report)
        {
            debug("Got Report Message");
            finished = true;
            ReportsDB.addReport(id, (Report) inputObject);
            return null;
        }

        // console functionality

        if (inputObject instanceof GetReports)
        {
            debug("Got GetReports Message");
            finished = true;
            return ReportsDB.getReports();
        }

        if (inputObject instanceof GetClientSummary)
        {
            debug("Got GetClientSummary Message");
            finished = true;
            return new ClientSummary();
        }

        // this should never happen
        return null;
    }

    public boolean isFinished()
    {
        return finished;
    }
}
