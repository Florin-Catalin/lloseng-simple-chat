package ui;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Observable;
import java.util.Observer;
import java.util.StringTokenizer;

import common.ChatIF;

/**
 * This class constructs the UI for a chat server. It implements the chat
 * interface in order to activate the display() method.
 */
public class ServerConsole implements ChatIF, Observer
{
    // Class variables *************************************************

    /**
     * The default port to listen on.
     */
    final public static int DEFAULT_PORT = 5555;

    // Instance variables **********************************************

    /**
     * The instance of the server that created this ConsoleChat.
     */
    EchoServer server;

    // Constructors ****************************************************

    /**
     * Constructs an instance of the ServerConsole UI.
     * @param port The port to connect on.
     */
    public ServerConsole(int port)
    {
        server = new EchoServer(port, this);
        server.addObserver(this);
    }

    // Instance methods ************************************************

    /**
     * This method waits for input from the console. Once it is received, it
     * sends it to the server's message handler.
     */
    public void accept()
    {
        try
        {
            BufferedReader fromConsole = new BufferedReader(
                    new InputStreamReader(System.in));
            String message;

            while (true)
            {
                message = fromConsole.readLine();
                handleMessage(message);
            }

        }
        catch (Exception ex)
        {
            System.out.println("Unexpected error while reading from console!");
        }
    }
    
    public void handleMessage(String message)
    {
     // checks to see if message is a command
        if (message.substring(0, 1).equals("#"))
        {
            StringTokenizer sb = new StringTokenizer(message, " ");

            // gets the command
            String command = sb.nextToken();

            if (command.equals("#quit"))
            {
                try
                {
                    server.quit();
                }
                catch (IOException e)
                {
                    this.display("Error! Server cannot quit.");
                }
            }
            else if (command.equals("#stop"))
            {
                server.stopListening();
            }
            else if (command.equals("#close"))
            {
                try
                {
                    server.close();
                }
                catch (IOException e)
                {
                    this.display("Error! Server cannot quit.");
                }
            }
            else if (command.equals("#setport"))
            {
                int port = 0;
                if (sb.hasMoreTokens())
                {
                    // gets the port
                    port = Integer.parseInt(sb.nextToken());
                }
                server.setPort(port);
            }
            else if (command.equals("#start"))
            {
                try
                {
                    server.listen();
                }
                catch (IOException ex)
                {
                    this.display("Error! Cannot start listening for clients.");
                }
            }
            else if (command.equals("#getport"))
            {
                String port = Integer.toString(server.getPort());
                this.display(port);
            }
        }

        // message is not a command
        else
        {
            server.sendToAllClients("SERVER MSG> " + message);
            this.display(message);
        }
    }

    /**
     * This method overrides the method in the ChatIF interface. It displays a
     * message onto the screen.
     * @param message The string to be displayed.
     */
    public void display(String message)
    {
        System.out.println("SERVER MSG> " + message);
    }

    /**
     * Begins the thread that waits for new clients.
     * @exception IOException if an I/O error occurs when creating the server
     * socket.
     */
    public void listen() throws IOException
    {
        server.listen();
    }

    // Class methods ***************************************************

    /**
     * This method is responsible for the creation of the Server UI.
     */
    public static void main(String[] args)
    {
        int port = 0; // Port to listen on

        try
        {
            port = Integer.parseInt(args[0]); // Get port from command line
        }
        catch (Throwable t)
        {
            port = DEFAULT_PORT; // Set port to 5555
        }
        ServerConsole chat = new ServerConsole(port);
        try
        {
            chat.listen(); // Start listening for connections
        }
        catch (Exception ex)
        {
            System.out.println("ERROR - Could not listen for clients!");
        }
        chat.accept(); // Wait for console data
    }

    public void update(Observable obs, Object message)
    {
        if(message instanceof Exception)
        {
            try
            {
                throw (Exception) message;
            }
            catch(Exception e)
            {
                
            }
        }
    }
}
// End of ServerConsole class
