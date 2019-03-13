package ui;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

import java.io.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;
import java.util.StringTokenizer;

import com.lloseng.ocsf.server.*;

import common.ChatIF;

/**
 * This class overrides some of the methods in the abstract 

superclass in order
 * to give more functionality to the server.
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 * @version July 2000
 */



public class EchoServer extends ObservableServer
{
    // Instance variables **********************************************

    /**
     * The interface type variable. It allows the implementation of the display
     * method in the client.
     */
    ChatIF serverUI;

    /**
     * Hashtable to store client's username and password pairs
     */
    private Hashtable<String, String> accounts;



    // Class variables *************************************************

    /**
     * The default port to listen on.
     */
    final public static int DEFAULT_PORT = 5555;

    /**
     * The default account file
     */
    final private static String ACCOUNT_FILE = "accounts.txt";


    // Constructors ****************************************************
       private Hashtable<String,Boolean> serverStatus ;


      private final int CAPACITY = 5 ;
    int countConnectedClients = 0 ;
   boolean clientHasAtemptedToConnect ;
    /**
     * Constructs an instance of the echo server.
     * @param port The port number to connect on.
     * @param serverUI The interface type variable.
     */
    public EchoServer(int port, ChatIF serverUI)
    {
        super(port);
        this.serverUI = serverUI;
        //channels = new Hashtable<String, ArrayList<ConnectionToClient>>();
        accounts = new Hashtable<String, String>();

        // import accounts from text file
        File accountFile = new File(ACCOUNT_FILE);
        try
        {
            Scanner input = new Scanner(new FileReader(accountFile));
            while (input.hasNextLine())
            {
                String[] nextAccount = input.nextLine().split(":");
                accounts.put(nextAccount[0], nextAccount[1]);
            }
            input.close();
        }
        catch (FileNotFoundException e)
        {

        }
    }

    // Instance methods ************************************************

    /**
     * This method handles any messages received from the client.
     * @param msg The message received from the client.
     * @param client The connection from which the message originated.
     */
    public void handleMessageFromClient(Object msg, ConnectionToClient client)
    {


        String message = (String) msg;

  // if( countConnectedClients != 0 )
      //  showStatus(serverStatus);

        String[] line = message.split(" ");
        if (line[0].equals("#login")) {
            if (countConnectedClients == CAPACITY) {
                try {
                    //client.sendToClient("Too many clients");
                    client.close();
                    System.out.print("The server is full yet another client want to login");
                    clientHasAtemptedToConnect = true ;
                  return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                // if username does not exist, create new account
                if (!accounts.containsKey(line[1])) {
                    accounts.put(line[1], line[2]);
                }

                // if the username/password does not match
                if (!accounts.get(line[1]).equalsIgnoreCase(line[2])) {
                    try {
                        // display error, terminate client
                        client
                                .sendToClient("Invalid username/password...disconnecting");
                        client.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {

                    System.out.println("Message received: " + msg + " from "
                            + client.getInfo("loginid"));
                    client.setInfo("loginid", line[1]); // Save client's login id
                    String loginMessage = client.getInfo("loginid")
                            + " has logged on.";
                    System.out.println(loginMessage);

                  String id = client.getInfo("loginid") + "";
                  //  serverStatus.put(id,true);

                    countConnectedClients++;
                    System.out.println("Number of connected Clients is " + countConnectedClients);

                    this.sendToAllClients(loginMessage); // Notify clients that
                    // this
                    // client has logged on
                }
            }
        }


    }

    /**
     * Sends a message to all the clients in a channel
     * @param channelName name of the channel
     * @param message message to send
     * @param loginid login id of the sender
     */
    private void sendMessageToChannel(String channelName, Object msg,
            String loginid, ConnectionToClient client)
    {
        String message = (String) msg;
        String[] line = message.split(" ");
        

    }

    /**
     * This method overrides the one in the superclass. Called when the server
     * starts listening for connections.
     */
    protected void serverStarted()
    {
        System.out.println("Server listening for connections on port "
                + getPort());
    }

    /**
     * This method overrides the one in the superclass. Called when the server
     * stops listening for connections.
     */
    protected void serverStopped()
    {
        // save the accounts to file
        File accountFile = new File(ACCOUNT_FILE);
        try
        {
            PrintWriter output = new PrintWriter(accountFile);

            Set<String> accountList = accounts.keySet();

            for (String key : accountList)
            {
                output.println(key + ":" + accounts.get(key));
            }

            output.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        System.out.println("Server has stopped listening for connections.");
    }

    /**
     * Overridden method called each time a new client connection is accepted.
     * @param client the connection connected to the client.
     */
    protected void clientConnected(ConnectionToClient client)
    {
        /*
         * // saves the client's description client.setInfo("client",
         * client.toString()); // displays message on server console when the
         * client connects System.out.println(client + " has logged on.");
         *
         */
        if (countConnectedClients == CAPACITY) {
            try {
                client.sendToClient("There are too many users");
            }catch ( Exception e )
            {
                e.printStackTrace();
            }
            return;
        }
        try
        {
            client.sendToClient("");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        System.out
                .println("A new client is attempting to connect to the server.");
    }

    /**
     * Overridden method called each time a client disconnects. The client is
     * guarantee to be disconnected but the thread is still active until it is
     * asynchronously removed from the thread group.
     * @param client the connection with the client.
     */
    synchronized protected void clientDisconnected(ConnectionToClient client) {
        
        if ( clientHasAtemptedToConnect == false ) {

            serverStatus.put(client.getInfo("loginid") + "",false);

            System.out.println(client.getInfo("loginid") + " has logged off.");
            countConnectedClients--;
            System.out.println("The number of connected clients has changed to " + countConnectedClients);


        }

    }



    public void showStatus (Hashtable <String,Boolean> ht)
    {
   Set <String> keys = ht.keySet();
   for (String k : keys )
   {
       System.out.println("The client" + k + "is currently" + ht.get(k));
   }
    }
    /**
     * quit()
     */
    public void quit() throws IOException
    {
        close();

    }

    /**
     * closeConnection();
     */
    public void closeConnection() throws IOException
    {
        stopListening();
        close();

    }

    /**
     * startListening()
     */
    public void startListening() throws IOException
    {
        listen();
    }     
}
// End of EchoServer class
