package invite.db;

import java.util.*;

import invite.net.message.*;

public class ReportsDB
{
    private static List<Report> reports = new ArrayList<Report>();

    public synchronized static void addReport(Id id, Report report)
    {
        //System.out.println("Adding report sent by " + id);

        report.setId(id);
        report.setReceivedDate(new Date());

        reports.add(report);
    }

    public synchronized static List<Report> getReports()
    {
        return reports;
    }

    public synchronized static List<Report> getReports(Id id)
    {
        List<Report> results = new ArrayList<Report>();

        for (Report report : reports)
        {
            if (report.getId().equals(id))
            {
                results.add(report);
            }
        }

        return results;
    }

    public synchronized static List<Report> getReports(Id id, boolean passed)
    {
        List<Report> results = new ArrayList<Report>();

        for (Report report : reports)
        {
            if (report.getId().equals(id) && report.isPassed() == passed)
            {
                results.add(report);
            }
        }

        return results;
    }
}
