package invite.net.console;

import java.util.*;

import invite.net.*;
import invite.net.message.*;
import invite.net.console.*;

public abstract class AAction
{
    public static String hostName = null;
    public static int portNum = -1;

    protected List<String> args = null;
    protected ConsoleClient client = null;

    public AAction(String command)
    {
        String[] ss = command.split("\\s+");
        this.args = new ArrayList<String>();
        for (int i = 0; i < ss.length; i ++)
        {
            this.args.add(ss[i]);
        }
    }

    protected void println(Object o)
    {
        print(o+ "\n");
    }
    
    protected void print(Object o)
    {
        System.out.print(o);
    }

    protected void connect()
    {
        client = new ConsoleClient(hostName, portNum);
    }

    protected void close()
    {
        client.close();
    }

    public abstract void act();
}
