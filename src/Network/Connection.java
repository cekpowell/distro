package Network;

import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import Network.Protocol.Exception.*;

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
     * @param networkInterace The interface associated with the connection.
     * @param socket The socket involved in the connection
     * @throws ConnectionSetupException If the Connection could not be setup.
     */
    public Connection(NetworkInterface networkInterface, Socket socket) throws ConnectionSetupException{
        try{
            this.networkInterface = networkInterface;
            this.socket = socket;
            this.textOut = new PrintWriter (new OutputStreamWriter(this.socket.getOutputStream())); 
            this.textIn = new BufferedReader (new InputStreamReader(this.socket.getInputStream()));
            this.dataOut = this.socket.getOutputStream();
            this.dataIn = this.socket.getInputStream();

            // logging creation of connection
            //this.networkInterface.logEvent("New connection made to port : " + socket.getPort());
        }
        catch(Exception e){
            throw new ConnectionSetupException(socket.getPort(),e);
        }
    }

    /**
     * Class constructor.
     * 
     * @param networkInterace The interface associated with the connection.
     * @param port The port the connection will be made to.
     * @throws ConnectionSetupException If the Connection could not be setup.
     */
    public Connection(NetworkInterface networkInterface, int port) throws ConnectionSetupException{
        try{
            // creating the connection
            this.networkInterface = networkInterface;
            this.socket = new Socket(InetAddress.getLocalHost(), port);
            this.textOut = new PrintWriter (new OutputStreamWriter(this.socket.getOutputStream())); 
            this.textIn = new BufferedReader (new InputStreamReader(this.socket.getInputStream()));
            this.dataOut = this.socket.getOutputStream();
            this.dataIn = this.socket.getInputStream();

            // logging creation of connection
            //this.networkInterface.logEvent("New connection made to port : " + port);
        }
        catch(Exception e){
            throw new ConnectionSetupException(port,e);
        }
    }

    /**
     * Determines if the Connection is open.
     * 
     * @return True if the connection is open, false if not.
     */
    public boolean isOpen(){
        return !this.isClosed();
    }

    /**
     * Closes the underlying socket.
     */
    public void close(){
        try{
            // closing connection
            this.socket.close();

            // passing the error to the network process
            this.networkInterface.getNetworkProcess().handleError(new ConnectionTerminatedException(this.getPort(), new ConnectionClosedException(this.getPort())));
        }
        catch(Exception e){
            this.networkInterface.getNetworkProcess().handleError(new SocketCloseException(this.getPort()));
        }
    }
    
    /**
     * Send's a message to the connection endpoint.
     * 
     * @param message The message to be sent.
     * @throws MessageSendException If the message could not be sent.
     */
    public void sendMessage(String message) throws MessageSendException{
        try{
            // Sending request
            this.textOut.println(message);
            this.textOut.flush(); 

            // logging message
            this.networkInterface.logMessageSent(this.socket, message);
        }
        catch(Exception e){
            throw new MessageSendException(message, this.getPort(), e);
        }
    }

    /**
     * Waits for an incoming message for an unbound length of time.
     * 
     * @return The message receieved as a String.
     * @throws MessageReceivedException If the message could not be received.
     */
    public String getMessage() throws MessageReceivedException{
        try{
            // getting request from connnection
            String message = this.textIn.readLine();

            // Message is non-null
            if(message != null){
                // logging message
                this.networkInterface.logMessageReceived(this.socket, message);

                return message;
            }
            // message is null - connection down
            else{
                throw new ConnectorDisconnectedException(this.getPort());
            }
        }
        catch(Exception e){
            throw new MessageReceivedException(this.getPort(), e);
        }
    }

    /**
     * Waits for a message to arrive within the given timeout.
     * 
     * @param timeout The timeout to wait for the message to arrive.
     * @return The message receieved.
     * @throws ConnectorDisconnectedException If the connector disconnected while waiting for 
     * the message to arrive.
     * @throws MessageReceivedException If the message could not be receieved, or could not 
     * be received within the timeout period.
     */
    public String getMessageWithinTimeout(int timeout) throws Exception{
        try{
            // setting socket timeout
            this.socket.setSoTimeout(timeout);

            // getting request from connnection
            String message = this.textIn.readLine();

            // Message is non-null
            if(message != null){
                this.socket.setSoTimeout(0);

                // logging message
                this.networkInterface.logMessageReceived(this.socket, message);

                return message;
            }
            // message is null - connection down
            else{
                throw new ConnectorDisconnectedException(this.getPort());
            }
        }
        catch(Exception e){
            this.socket.setSoTimeout(0);

            // Handling Specific Cases

            // Socket timeout exception - throw a message timeout exception
            if(e instanceof SocketTimeoutException){
                throw new MessageReceivedException(this.getPort(), new MessageTimeoutException());
            }
            // other form of exception
            else{
                throw new MessageReceivedException(this.getPort(), e);
            }
        }
    }

    /**
     * Sends byte data to the connection endpoint.
     * 
     * @param bytes The array of bytes to be sent.
     * @throws MessageSendException If the bytes could not be sent.
     */
    public void sendBytes(byte[] bytes) throws MessageSendException{
        try{
            // Sending request
            this.dataOut.write(bytes);
            this.textOut.flush(); 

            // logging
            this.networkInterface.logMessageSent(this.socket, "[FILE CONTENT]");
        }
        catch(Exception e){
            throw new MessageSendException(this.getPort(), e);
        }

    }

    /**
     * Waits for a N bytes to arrive within the given timeout.
     * 
     * @param timeout The timeout to wait for the message to arrive.
     * @return The array of bytes gathered from the connection.
     * @throws ConnectorDisconnectedException If the connector disconnected while waiting for 
     * the bytes to arrive.
     * @throws MessageReceivedException If the bytes could not be receieved, or could not 
     * be received within the timeout period.
     */
    public byte[] getNBytesWithinTimeout(int n, int timeout) throws Exception{
        try{
            // setting socket timeout
            this.socket.setSoTimeout(timeout);

            // getting request from connnection - returns empty as soon as connection drops
            byte[] bytes = this.dataIn.readNBytes(n);

            // returninig the gathered bytes
            if(bytes.length == n){
                this.socket.setSoTimeout(0);

                // logging message
                this.networkInterface.logMessageReceived(this.socket, "[FILE CONTENT]");

                // returning
                return bytes;
            }
            else{
                throw new ConnectorDisconnectedException(this.getPort());
            }
        }
        catch(Exception e){
            this.socket.setSoTimeout(0);

            // Handling Specific Cases

            // Socket timeout exception - throw a message timeout exception
            if(e instanceof SocketTimeoutException){
                throw new MessageReceivedException(this.getPort(), new MessageTimeoutException());
            }
            // other form of exception
            else{
                throw new MessageReceivedException(this.getPort(), e);
            }
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