package invite.net;

import java.io.*;
import java.net.*;

import invite.db.*;

public class InviteServer
{
    public static boolean listening = true;

    public static final void main(String[] args)
    {
        ServerSocket serverSocket = null;
        int portNum = 4444;

        // initialization
        try
        {
            println("Opening socket " + portNum);
            serverSocket = new ServerSocket(portNum);
            println("Success opening socket.");

            println("Starting TestsDB.init");
            // load classes and methods dynamically, for now
            TestsDB.init();
            println("Finished TestsDB.init");

            println("IdDB initialized; next free Id is: " + IdDB.getCurrentFreeId());

            println("");
        }
        catch (Exception ex)
        {
            println("ERR IN INITIALIZATION");
            ex.printStackTrace();
            System.exit(-1);
        }

        try
        {
            while (listening)
            {
                Socket ss = serverSocket.accept();
                //System.out.println("Got a new socket.");
                Thread t = new InviteServerThread(ss);
                //System.out.println("Created new thread");
                t.start();
                //System.out.println("Got a new client!");
            }
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }

        try
        {
            serverSocket.close();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    private static final boolean DEBUG = true;

    private static void println(String s)
    {
        if (DEBUG) System.out.println(s);
    }

    private static void print(String s)
    {
        if (DEBUG) System.out.print(s);
    }
}
