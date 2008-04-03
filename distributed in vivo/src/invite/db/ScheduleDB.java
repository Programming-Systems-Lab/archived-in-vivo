package invite.db;

import invite.net.message.*;

public class ScheduleDB
{
    // this is 1% (somehow...)
    // chris: percent of tests to execute (0 to 1.0) ... for 1% tests, set to 0.0002.... for 10%, set to 0.002
    //public static final double BASE_RUN_PERCENT = .0002;
    public static final double BASE_RUN_PERCENT = .002;
    //public static final double BASE_RUN_PERCENT = .01;
    
    public static final int DEFAULT_RESCHEDULE_RATE = 0;
    
    public static Schedule getNewSchedule(Id id)
    {
        int numClients = 
            //IdDB.getNumClients();
            -1;
        System.out.println("ScheduleDB: numClients = " + numClients);
        return 
            new Schedule(
                BASE_RUN_PERCENT * (1.0 / numClients),
                // reschedule rate
                DEFAULT_RESCHEDULE_RATE * 1000
            );
    }
}
