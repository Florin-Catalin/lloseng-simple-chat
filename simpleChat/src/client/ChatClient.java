// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package client;

import com.lloseng.ocsf.client.*;
import common.*;
import java.io.*;

/**
 * This class overrides some of the methods defined in the abstract superclass
 * in order to give more functionality to the client.
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 * @version July 2000
 */
public class ChatClient extends ObservableClient
{
    // Instance variables **********************************************

    /**
     * The interface type variable. It allows the implementation of the display
     * method in the client.
     */
    ChatIF clientUI;

    /**
     * The client's login id.
     */
    private String loginid;

    /**
     * The client's password
     */
    private String password;

    // Constructors ****************************************************

    /**
     * Constructs an instance of the chat client.
     * @param loginid The client's login id.
     * @param host The server to connect to.
     * @param port The port number to connect on.
     * @param clientUI The interface type variable.
     */

    public ChatClient(String loginid, String password, String host, int port,
            ChatIF clientUI) throws IOException
    {
        super(host, port); // Call the superclass constructor
        this.clientUI = clientUI;
        this.loginid = loginid;
        this.password = password; // added password to constructor

        openConnection();
        sendToServer("#login " + loginid + " " + password); // Send login id and
                                                            // password from
                                                            // client to
        // server
    }

    // Instance methods ************************************************

    /**
     * Get the login id of the client
     * @return String loginid
     */
    public String getLoginId()
    {
        return loginid;
    }

    /**
     * This method terminates the client.
     * @throws IOException if disconnection from server has failed
     */
    public void quit() throws IOException
    {
        closeConnection();
    }

    /**
     * Terminates client's connection to the server.
     * @throws IOException if disconnection from server has failed
     */
    public void logoff() throws IOException
    {
        closeConnection(); // terminates connection to the server
    }

    /**
     * Sets the host.
     * @param host name of host
     * @throws IOException if cannot connect to host
     */
    public void sethost(String host) throws IOException
    {
        if (!isConnected())
        {
            setHost(host);
            openConnection();
            sendToServer("#login " + loginid); // Send login id
            // from client to server
        }
        else
        {
            throw new IOException();
        }
    }

    /**
     * Sets the port.
     * @param port the port number
     * @throws IOExpcetion if cannot connect to port
     */
    public void setport(int port) throws IOException
    {
        if (!isConnected())
        {
            setPort(port);
            openConnection();
            sendToServer("#login " + loginid); // Send login id
            // from client to server
        }
        else
        {
            throw new IOException();
        }
    }

    /**
     * Logs in to the server.
     * @throws IOException if cannot connect to server
     */
    public void login() throws IOException
    {
        if (!isConnected())
        {
            openConnection();
            sendToServer("#login " + loginid); // Send login id
            // from client to server
        }
        else
        {
            throw new IOException();
        }
    }

    /**
     * Private chat with another user.
     * @param username the username of the other user
     * @throws IOException if cannot send username to server
     */
    public void privateChat(String username, String msg) throws IOException
    {
        String message = "#private " + username + " " + msg;
        sendToServer(message);
    }

    /**
     * Create the specified channel.
     * @param channelName the name of the channel
     * @throws IOException if cannot send channel name to server
     */
    public void create(String channelName) throws IOException
    {
        String message = "#create " + channelName;
        sendToServer(message);
    }

    /**
     * Join the specified channel.
     * @param channelName the name of the channel
     * @throws IOException if cannot send channel name to server
     */
    public void join(String channelName) throws IOException
    {
        String message = "#join " + channelName;
        sendToServer(message);
    }

    /**
     * Leave the specified channel.
     * @param channelName the name of the channel
     * @throws IOException if cannot send channel name to server
     */
    public void leave() throws IOException
    {
        String message = "#leave";
        sendToServer(message);
    }

    /**
     * Select the specified monitee to monitor messages.
     * @param monitee the specified monitee's username
     * @throws IOException if cannot send monitee's username to server
     */
    public void select(String monitee) throws IOException
    {
        String message = "#select " + monitee;
        sendToServer(message);
    }

    /**
     * Display the current channels on the server.
     * @throws IOException if cannot send displayChannels command to server
     */
    public void displayChannels() throws IOException
    {
        String message = "#displayChannels";
        sendToServer(message);
    }

    /**
     * Back from being away.
     * @throws IOException if cannot send back command to server
     */
    public void back() throws IOException
    {
        String message = "#back";
        sendToServer(message);
    }

    /**
     * Retrieve messages monitored while away.
     * @throws IOException if cannot send retrieve command to server
     */
    public void retrieve() throws IOException
    {
        String message = "#retrieve";
        sendToServer(message);
    }
}
// End of ChatClient class
