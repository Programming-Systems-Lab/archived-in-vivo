package invite.net.message;

import java.util.*;

import invite.db.*;
import invite.net.message.*;

public class ClientSummary extends AMessage
{
    private List<OneClientSummary> summaries = new ArrayList<OneClientSummary>();

    public ClientSummary()
    {
        super();
        
        for (Id id : IdDB.getActiveIds())
        {
            summaries.add(new OneClientSummary(id));
        }
    }

    public String toString()
    {
        String s = "";

        s += "Total Ids = " + summaries.size();
        s += "\n";
        
        for (OneClientSummary summary : summaries)
        {
            s += summary;
            s += "\n";
        }

        return s;
    }

    public class OneClientSummary extends AMessage
    {
        private Id id = null;
        private int totalNumTests = -1;
        private int totalNumTestsPassed = -1;

        public OneClientSummary(Id id)
        {
            this.id = id;

            List<Report> reports = ReportsDB.getReports(id);
            totalNumTests = reports.size();

            List<Report> passedReports = ReportsDB.getReports(id, true);
            totalNumTestsPassed = passedReports.size();
        }

        public String toString()
        {
            String s = "";

            s += "\t";
            s += "id = " + this.id;
            s += "\n";

            s += "\t";
            s += "totalNumTests = " + this.totalNumTests;
            s += "\n";

            s += "\t";
            s += "totalNumTestsPassed = " + this.totalNumTestsPassed;
            s += "\n";

            return s;
        }
    }
}
