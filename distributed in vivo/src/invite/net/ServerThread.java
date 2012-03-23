package invite.net;

import java.io.*;
import java.net.*;

import invite.net.message.*;

public class ServerThread
    extends Thread
{
    private Socket socket = null;

    public ServerThread(Socket socket)
    {
        super("ServerThread");
        this.socket = socket;
    }

    public void run()
    {
        try
        {
            System.out.println("Starting run()");

            ObjectOutputStream o_out = new ObjectOutputStream(socket.getOutputStream());
            System.out.println("Finished out, now in.");
            ObjectInputStream o_in = new ObjectInputStream(socket.getInputStream());
            //MessageProtocol protocol = new MessageProtocol();
            
            System.out.println("Waiting for input.");

            Object inputObject = null, outputObject = null;
            //while ((inputObject = o_in.readObject()) != null)
            //o_out.writeObject("Hello");
            inputObject = o_in.readObject();
            {
                /*
                outputLine = kkp.processInput(inputLine);
                out.println(outputLine);
                if (outputLine.equals("Bye"))
                    break;
                */
                //outputObject = protocol.processInput(inputObject);
                //o_out.writeObject(outputObject);
                Report report = (Report) inputObject;
                System.out.println(report);
                //break;
            }

            System.out.println("Closing.");

            o_in.close();
            o_out.close();
            socket.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void run1()
    {
        try
        {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = 
                new BufferedReader(
                    new InputStreamReader(
                        socket.getInputStream()
                        )
                    );

            String inputLine, outputLine;
            Protocol kkp = new Protocol();
            outputLine = kkp.processInput(null);
            out.println(outputLine);

            while ((inputLine = in.readLine()) != null)
            {
                outputLine = kkp.processInput(inputLine);
                out.println(outputLine);
                if (outputLine.equals("Bye"))
                    break;
            }
            out.close();
            in.close();
            socket.close();

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
