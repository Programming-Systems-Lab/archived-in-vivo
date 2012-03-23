package invite.net.console;

import java.util.*;

import invite.etc.*;
import invite.net.*;
import invite.net.message.*;

public class Console
{
    private static void println(String s)
    {
        print(s + "\n");
    }
    
    private static void print(String s)
    {
        System.out.print(s);
    }

    private static String command = null;
    private static AAction action = null;

    public static final void main(String[] args)
    {
        Config.init("invite.conf");
        AAction.hostName = Config.get("hostName");
        AAction.portNum = Config.getInt("portNum");

        Scanner scanner = new Scanner(System.in).useDelimiter("\\n");

        println("Welcome.");
        println("");
        while (! (action instanceof Exit))
        {
            print("? ");
            command = scanner.next();

            action = makeAction(command);

            action.act();
        }

        System.out.println("Goodbye!");
    }

    // make this into an ActionFactory class?
    private static AAction makeAction(String command)
    {
        AAction action = new Help(command);

        if (command.equalsIgnoreCase("exit"))
            action = new Exit(command);

        if (command.equalsIgnoreCase("reports"))
            action = new CmdGetReports(command);
        if (command.equalsIgnoreCase("client-summary"))
            action = new CmdClientSummary(command);

        return action;
    }
}
