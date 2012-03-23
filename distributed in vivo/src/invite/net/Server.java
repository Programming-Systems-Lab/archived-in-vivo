package invite.net;

import java.io.*;
import java.net.*;

public class Server
{
    public static void main(String[] args) //throws IOException
    {
        ServerSocket serverSocket = null;
        boolean listening = true;

        try
        {
            serverSocket = new ServerSocket(4444);
        }
        catch (IOException ex)
        {
            System.err.println("Could not listen on port: 4444.");
            System.exit(-1);
        }

        try
        {
            while (listening)
            {
                new ServerThread(serverSocket.accept()).start();
                System.out.println("Got a new client!");
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
}
