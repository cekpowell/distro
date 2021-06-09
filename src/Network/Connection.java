package Network;

import java.net.InetAddress;
import java.net.Socket;
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
 * Creates new socket and the input and output streams for the socket at instantiation.
 * 
 * Has methods that allow for messages to be sent an recieved and also handles the logging
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
     * Closes the underlying socket.
     */
    public void close(){
        try{
            this.socket.close();
        }
        catch(Exception e){
            this.networkInterface.handleError("Unable to close connection to connector on port : " + this.socket.getPort(), e);
        }
    }
    
    /**
     * Send's a message to the connection endpoint.
     * 
     * Logs the message being sent if it was sent, throws an exception otherwise.
     * 
     * @param The message to be sent
     */
    public void sendMessage(String request) throws Exception{
        // Sending request
        this.textOut.println(request);
        this.textOut.flush(); 

        // logging message
        this.networkInterface.logMessageSent(this.socket, request);
    }

    /**
     * Waits for an incoming message for an unbound length of time.
     * 
     * Logs and returns the message if one was recieved, throws an exception if the connection closed.
     */
    public String getMessage() throws Exception{
        // getting request from connnection
        Token message = RequestTokenizer.getToken(this.textIn.readLine());

        // logging message
        this.networkInterface.logMessageReceived(this.socket, message.message);

        return message.message;
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
            // getting request from connnection - returns NullPointerException if connection drops
            Token message = RequestTokenizer.getToken(this.textIn.readLine());

            // logging message
            this.networkInterface.logMessageReceived(this.socket, message.message);

            this.socket.setSoTimeout(0);
            return message.message;
        }
        catch(Exception e){
            this.socket.setSoTimeout(0);
            throw e;
        }
    }

    /**
     * Send's a message to the connection endpoint.
     * 
     * No logging is requredd, throws an exception if the message could not be send
     * 
     * @param The message to be sent
     */
    public void sendBytes(byte[] bytes) throws Exception{
        // Sending request
        this.dataOut.write(bytes);
        this.textOut.flush(); 
    }

    /**
     * Waits for an incoming message for an unbound length of time.
     * 
     * The receieved data is not logged, and an exception is thrown if the data is not gathered within
     * the specified time.
     */
    public byte[] getNBytes(int n) throws Exception{
        // getting request from connnection
        byte[] bytes = this.dataIn.readNBytes(n);

        // returninig the gathered bytes
        return bytes;
    }

    /**
     * Waits for a message to arrive within the given timeout.
     * 
     * The receieved data is not logged, and an exception is thrown if the data is not gathered within
     * the specified time.
     * 
     * @param timeout The timeout to wait for the message to arrive.
     */
    public byte[] getNBytesWithinTimeout(int n, int timeout) throws Exception{
        // setting socket timeout
        this.socket.setSoTimeout(timeout);

        // trying to gather message from socket
        try{
            // getting request from connnection - returns empty as soon as connection drops
            byte[] bytes = this.dataIn.readNBytes(n);

            // returninig the gathered bytes
            if(bytes.length == n){
                this.socket.setSoTimeout(0);
                return bytes;
            }
            else{
                throw new Exception();
            }
        }
        catch(Exception e){
            this.socket.setSoTimeout(0);
            throw e;
        }
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

    public boolean isClosed(){
        return this.socket.isClosed();
    }

    public int getPort(){
        return this.socket.getPort();
    }

    public int getLocalPort(){
        return this.socket.getLocalPort();
    }
}