package ui;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

import java.io.*;
import java.util.Observable;
import java.util.Observer;
import java.util.StringTokenizer;

import com.lloseng.ocsf.client.ObservableClient;

import client.*;
import common.*;

/**
 * This class constructs the UI for a chat client. It implements the chat
 * interface in order to activate the display() method. Warning: Some of the
 * code here is cloned in ServerConsole
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @version July 2000
 */
public class ClientConsole implements ChatIF, Observer
{
    // Class variables *************************************************

    /**
     * The default port to connect on.
     */
    final public static int DEFAULT_PORT = 5555;

    // Instance variables **********************************************

    /**
     * The instance of the client that created this ConsoleChat.
     */
    ChatClient client;

    // Constructors ****************************************************

    /**
     * Constructs an instance of the ClientConsole UI.
     * @param loginid The client's login id.
     * @param host The host to connect to.
     * @param port The port to connect on.
     */
    public ClientConsole(String loginid, String password, String host, int port)
    {
        try
        {
            client = new ChatClient(loginid, password, host, port, this);
            client.addObserver(this);
        }
        catch (IOException exception)
        {
            System.out.println("Error: Can't setup connection!"
                    + " Terminating client.");
            System.exit(1);
        }
    }

    // Instance methods ************************************************

    /**
     * This method waits for input from the console. Once it is received, it
     * sends it to the client's message handler.
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

    /**
     * This method overrides the method in the ChatIF interface. It displays a
     * message onto the screen.
     * @param message The string to be displayed.
     */
    public void display(String message)
    {
        System.out.println("> " + message);
    }

    // Class methods ***************************************************

    /**
     * This method is responsible for the creation of the Client UI.
     * @param args[0] The login id of the client.
     * @param args[1] The host to connect to.
     * @param args[2] The port to connect to.
     */
    public static void main(String[] args)
    {
        String host = "";


        String loginid = "";
        String password = "";
        int port = 0; // The port number

        try
        {
            loginid = args[0]; // login id
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            System.out
                    .println("ERROR - No login ID specified. Connection aborted.");
            System.exit(0); // login id not provided, quit
        }
        
        //check for password
        try
        {
            password = args[1]; // password
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            System.out
                    .println("ERROR - No password specified. Connection aborted.");
            System.exit(0); // password not provided, quit
        }

        try
        {
            host = args[2];
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            host = "localhost";
        }

        try
        {
            port = Integer.parseInt(args[3]);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            port = DEFAULT_PORT;
        }

        ClientConsole chat = new ClientConsole(loginid, password, host, port); //added password to constructor
        chat.accept(); // Wait for console data
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
                    client.quit();
                }
                catch (IOException e)
                {
                    this.display("Disconnection from server failed.");
                }
                if (!client.isConnected())
                {
                    this.display("Connection closed.");
                    System.exit(0); // exits the program
                }
            }
            else if (command.equals("#logoff"))
            {
                try
                {
                    client.logoff(); // terminates connection to the server
                    this.display("Connection closed.");
                }
                catch (IOException e)
                {
                    this.display("Disconnection from server failed.");
                }
            }
            else if (command.equals("#sethost"))
            {
                String host = "";
                if (sb.hasMoreTokens())
                {
                    // gets the host
                    host = sb.nextToken();
                }
                
                try
                {
                    client.sethost(host);
                }
                catch (IOException ex)
                {
                    this.display("Error! Cannot connect to host!");
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
                
                try
                {
                    client.setport(port);
                }
                catch (IOException ex)
                {
                    this.display("Error! Cannot connect to port!");
                }
            }
            else if (command.equals("#login"))
            {
                try
                {
                    client.login();
                }
                catch (Exception e)
                {
                    this.display("Error. Cannot make a connection.");
                }
            }
            else if (command.equals("#gethost"))
            {
                this.display(client.getHost());
            }
            else if (command.equals("#getport"))
            {
                this.display(Integer.toString(client.getPort()));
            }





        }

        // message is not a command
        else
        {
            try
            {
                client.sendToServer(message);
            }
            catch (IOException e)
            {
                this.display("Could not send message to server.  Terminating client.");
                try
                {
                    client.quit();
                }
                catch (IOException e1)
                {
                    e1.printStackTrace();
                }
            }
        }
    }
    
    public void update(Observable obs, Object message)
    {
        if(message instanceof String)
        {
            System.out.println(message);
        }
        else if(message instanceof Exception)
        {
            try
            {
                throw (Exception) message;
            }
            catch(Exception e)
            {
                // message displayed to client when server shuts down
                this.display("Server has shut down. Terminating client.");
                
                e.printStackTrace();
                
                //terminates the client
                try
                {
                    client.quit();
                }
                catch (IOException e1)
                {
                    e1.printStackTrace();
                }
            }
        }
    }
}
// End of ConsoleChat class
