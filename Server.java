import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Server extends Thread
{
    // the output stream
    private PrintWriter out;

    // the input stream
    private Scanner in;

    // the server
    private ServerSocket server;


    /* This is the main method for the Server */
    public static void main(String[] args)
    {
	if (args.length < 1)
	{
	    System.out.println("Please specify a port number!");
	    System.exit(0);
	}

	int port = Integer.parseInt(args[0]);

	Server ms = new Server(port);

	ms.run();
    }

    public Server(int port)
    {
	try
	{
	    server = new ServerSocket(port);
	    System.out.println("Server started... waiting for connection");    
	}
	catch (Exception e)
	{
	    System.out.println("Cannot create MathServer!");
	    e.printStackTrace();
	}
    }

    public void run()
    {
	try
	{
	    // wait for a client
	    Socket socket = server.accept();

	    System.out.println("Connection established");

	    // get the input stream
	    in = new Scanner(socket.getInputStream());

	    // get the output stream
	    out = new PrintWriter(socket.getOutputStream());

	    while (true)
	    {
		// read the next line
		int value = in.nextInt();
		
		// echo it out
		System.out.println(":" + value + ":");
		
		// square it
		long sq = value * value;
		
		// write to the output stream
		out.write(sq + "\n");
		out.flush();
		
	    }
        }
	// this will catch ANY exception that occurs
	catch (Exception e)
	{
	    // e.printStackTrace();
	    System.out.println("Server stopped.");
	}
	// make sure you close the ServerSocket when you're done!
	finally
	{
	    shutdown();
	}
    }

    /**
     * Method to stop the server.
     */
    public void shutdown()
    {
	try { server.close(); } catch (Exception e) { }
	try { out.close(); } catch (Exception e) { }
    }
}



