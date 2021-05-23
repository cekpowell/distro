package Server;

import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import Interface.NetworkInterface;
import Token.*;

/**
 * Convenience class for Socket and Logging.
 * 
 * Creates new socket but also creates the input and output streams at instantiation.
 * 
 * Has method that allow for messages to be sent an recieved and also handles the logging
 * of these messages to the given NetworkInterface.
 */
public class Connection{
    
    // member variables
    private NetworkInterface networkInterface;
    private Socket socket;
    private PrintWriter textOut;
    private BufferedReader textIn;
    private OutputStream dataOut;
    private InputStream dataIn;

    /**
     * Class constructor.
     * 
     * @param socket The socket involved in the connection
     */
    public Connection(NetworkInterface networkInterface, Socket socket) throws Exception{
        this.networkInterface = networkInterface;
        this.socket = socket;
        this.textOut = new PrintWriter (new OutputStreamWriter(this.socket.getOutputStream())); 
        this.textIn = new BufferedReader (new InputStreamReader(this.socket.getInputStream()));
        this.dataOut = this.socket.getOutputStream();
        this.dataIn = this.socket.getInputStream();
    }

    /**
     * Class constructor. 
     * 
     * Doesnt take in socket, but just address and port instead.
     * 
     * @param socket The socket involved in the connection
     */
    public Connection(NetworkInterface networkInterface, InetAddress address, int port) throws Exception{
        this.networkInterface = networkInterface;
        this.socket = new Socket(address, port);
        this.textOut = new PrintWriter (new OutputStreamWriter(this.socket.getOutputStream())); 
        this.textIn = new BufferedReader (new InputStreamReader(this.socket.getInputStream()));
        this.dataOut = this.socket.getOutputStream();
        this.dataIn = this.socket.getInputStream();
    }
    
    /**
     * Send's a message to the connection endpoint.
     * 
     * Logs the message being sent if it was sent, throws an exception otherwise.
     * 
     * @param The message to be sent
     */
    public void sendMessage(String request) throws Exception{
        try{
            // Sending request
            this.textOut.println(request);
            this.textOut.flush(); 

            // logging message
            this.networkInterface.logMessageSent(this.socket, request);
        }
        catch(Exception e){
            throw new Exception();
        }
    }

    /**
     * Waits for an incoming message for an unbound length of time.
     * 
     * Logs and returns the message if one was recieved, throws an exception if the connection closed.
     */
    public String getMessage() throws Exception{
        try{
            // getting request from connnection
            Token message = RequestTokenizer.getToken(this.textIn.readLine());

            // logging message
            this.networkInterface.logMessageReceived(this.socket, message.message);

            return message.message;
        }
        catch(NullPointerException e){
            // connection disconnected - throwing exception
            throw new NullPointerException(e.toString());
        }
        catch(Exception e){
            throw new Exception(e.toString());
        }
    }

    /**
     * Waits for a message to arrive within the given timeout.
     * 
     * Logs and returns the message if one was recieved within the timeout, throws exception otherwise.
     * 
     * @param timeout The timeout to wait for the message to arrive.
     */
    public String getMessageWithinTimeout(int timeout) throws Exception{
        // setting socket timeout
        this.socket.setSoTimeout(timeout);

        // trying to gather message from socket
        try{
            // getting request from connnection
            Token message = RequestTokenizer.getToken(this.textIn.readLine());

            // logging message
            this.networkInterface.logMessageReceived(this.socket, message.message);

            return message.message;
        }
        catch(NullPointerException e){
            // connection disconnected - throwing exception
            this.socket.setSoTimeout(0);
            throw new NullPointerException();
        }
        catch(SocketTimeoutException e){
            this.socket.setSoTimeout(0);
            throw new SocketTimeoutException();
        }
        catch(Exception e){
            this.socket.setSoTimeout(0);
            throw new Exception();
        }
    }

    /**
     * Send's a message to the connection endpoint.
     * 
     * Logs the message being sent if it was sent, throws an exception otherwise.
     * 
     * @param The message to be sent
     */
    public void sendNBytes(Byte[] message){
        // TODO
    }

    /**
     * Waits for an incoming message for an unbound length of time.
     * 
     * Logs and returns the message if one was recieved, throws an exception if the connection closed.
     */
    public Byte[] getNBytes(){
        // TODO
        return null;
    }

    /**
     * Waits for a message to arrive within the given timeout.
     * 
     * Logs and returns the message if one was recieved within the timeout, throws exception otherwise.
     * 
     * @param timeout The timeout to wait for the message to arrive.
     */
    public Byte[] getNBytesWithinTimeout(int timeout){
        // TODO
        return null;
    }

    /////////////////////////
    // GETTERS AND SETTERS //
    /////////////////////////

    public void setNetworkInterface(NetworkInterface networkInterface){
        this.networkInterface = networkInterface;
    }

    public Socket getSocket(){
        return this.socket;
    }
}