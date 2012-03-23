package invite.net.console;

import java.util.*;

import invite.net.*;
import invite.net.message.*;

public class CmdClientSummary extends AAction
{
    public CmdClientSummary(String command)
    {
        super(command);
    }

    public void act()
    {
        connect();
        ClientSummary summary = client.getClientSummary();
        close();

        println(summary);
    }
}
