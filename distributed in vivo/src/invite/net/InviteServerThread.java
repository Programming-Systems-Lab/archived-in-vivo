package invite.net;

import java.io.*;
import java.net.*;
import java.util.*;

import invite.net.message.*;

public class InviteServerThread
    extends Thread
{
    private Socket socket = null;

    public InviteServerThread(Socket socket)
    {
        super("ServerThread");
        this.socket = socket;
    }

    public void run()
    {
        //System.out.println("\tlocal port = " + this.socket.getLocalPort());
        //System.out.println("\tremote port = " + this.socket.getPort());

        try
        {
            //System.out.println("Starting run()");

            ObjectOutputStream o_out = new ObjectOutputStream(socket.getOutputStream());
            //System.out.println("Finished out, now in.");
            ObjectInputStream o_in = new ObjectInputStream(socket.getInputStream());
            MessageProtocol protocol = new MessageProtocol();
            
            //System.out.println("Waiting for input.");

            Object inputObject = null, outputObject = null;

            do
            {
                //System.out.println("SERVER: about to get object");

                inputObject = o_in.readObject();

                //System.out.println("SERVER: got object");

                outputObject = protocol.process(inputObject);

                o_out.writeObject(outputObject);
            }
            while (! protocol.isFinished());

            //while ((inputObject = o_in.readObject()) != null)
            //while (! protocol.finished)
            //o_out.writeObject("Hello");
            {
                /*
                outputLine = kkp.processInput(inputLine);
                out.println(outputLine);
                if (outputLine.equals("Bye"))
                    break;
                */
                //outputObject = protocol.processInput(inputObject);
                //o_out.writeObject(outputObject);

                //Report report = (Report) inputObject;
                //System.out.println(report);

                //break;
            }

            //System.out.println("Closing.");

            o_in.close();
            o_out.close();
            socket.close();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
