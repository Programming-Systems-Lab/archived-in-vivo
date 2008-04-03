package invite.net.message;

import java.io.*;
import java.util.*;
import java.text.*;

public class Report extends AMessage
{
    // TODO clean this up, remove duplication with Client.sendReport
    public Id id = null;
    private String testName = null;
    private boolean passed = false;
    private Exception exception = null;
    private Date creationDate = null;
    private Date receivedDate = null;

    public Report(String testName, boolean passed)
    {
        this(testName, passed, null);
    }

    public Report(String testName, boolean passed, Exception exception)
    {
        super();

        this.testName = testName;
        this.passed = passed;
        this.exception = exception;

        this.creationDate = new Date();
    }

    public Report(String testName, boolean passed, Throwable throwable)
    {
        this(testName, passed, (Exception) throwable);
    }

    /*
    public Report(Id id, String testName, boolean passed, Exception exception)
    {
        this.id = id;
        this.testName = testName;
        this.passed = passed;
        this.exception = exception;
    }
    */

    public Id getId()
    {
        return this.id;
    }

    public void setId(Id id)
    {
        this.id = id;
    }

    public String getTestName()
    {
        return this.testName;
    }

    public void setTestName(String testName)
    {
        this.testName = testName;
    }

    public boolean isPassed()
    {
        return this.passed;
    }

    public Exception getException()
    {
        return this.exception;
    }

    public Date getCreationDate()
    {
        return this.creationDate;
    }

    public Date getReceivedDate()
    {
        return this.receivedDate;
    }

    public void setReceivedDate(Date date)
    {
        this.receivedDate = date;
    }

    public String toString()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM dd HH:mm:ss.SSSS");

        return
            "Report: " + "\n" +
            "\t" + "id = " + this.getId() + "\n" +
            "\t" + "testName = " + this.getTestName() + "\n" +
            "\t" + "pass = " + this.isPassed() + "\n" +
            "\t" + "exception = " + this.getException() + "\n" + 
            //"\t" + this.getException().getStackTrace() + "\n"
            "\t" + "creation = " + sdf.format(this.creationDate) + "\n" +
            "\t" + "received = " + sdf.format(this.receivedDate) + "\n" +
            ""
            ;
    }
}
