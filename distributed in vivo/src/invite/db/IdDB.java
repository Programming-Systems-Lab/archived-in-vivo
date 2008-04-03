package invite.db;

import java.util.*;

import invite.net.message.*;

public class IdDB
{
    private static int nextFreeUid = 1;
    private static int numClients = 0;

    private static List<Id> activeIds = new ArrayList<Id>();

    public static synchronized Id getNextFreeId()
    {
        Id id = getCurrentFreeId();

        nextFreeUid ++;
        numClients ++;

        activeIds.add(id);
        TestsDB.addNewId(id);

        TestsDB.repartitionUniformily();

        return id;
    }

    public static synchronized Id getCurrentFreeId()
    {
        return new Id(nextFreeUid);
    }

    public static boolean isValidId(Id id)
    {
        return id.getUid() < nextFreeUid;
    }

    public static int getNumClients()
    {
        return numClients;
    }

    public static List<Id> getActiveIds()
    {
        return activeIds;
    }
}
