package invite.net.message;

public class Schedule extends AMessage
{
    // a number between 0.0 and 1.0; this is not a "real" percent
    public double executionPercent;

    // num millis to get a new schedule
    private long millisTillNewSchedule;

    public Schedule(double executionPercent, long millisTillNewSchedule)
    {
        this.executionPercent = executionPercent;
        this.millisTillNewSchedule = millisTillNewSchedule;
    }
    
    public long getMillisTillNewSchedule()
    {
        return this.millisTillNewSchedule;
    }

    public String toString()
    {
        return
            "(Schedule: " + 
                "executionPercent = " + this.executionPercent + 
                ", " +
                "millisTillNewSchedule = " + this.millisTillNewSchedule +
            ")";
    }
}
