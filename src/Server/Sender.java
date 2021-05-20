package Server;

import java.net.InetAddress;
import java.net.Socket;

import Logger.MyLogger;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * Represents a connection between a Server and a connector, where the Sevrer only sends
 * messages to the connector.
 */
public class Sender extends Socket{

    // member variables
    private Server server;
    
    private PrintWriter textOut;
    private OutputStream dataOut;

    /**
     * Class constructor.
     * 
     * @param server The Server object involved in the connection.
     * @param port The port of the object the server is connecting to.
     */
    public Sender(Server server, int port) throws Exception{
        super(InetAddress.getLocalHost(), port);
        this.server = server;
        try{
            this.textOut = new PrintWriter (new OutputStreamWriter(this.getOutputStream())); 
            this.dataOut = this.getOutputStream();
        }
        catch(Exception e){
            System.out.println("ERROR : Unable to create streams for a connection.");
        }
    }

    /**
     * Sends the given message to the through the connection.
     * 
     * @param message To be sent through the connection.
     */
    public void sendMessage(String message){
        try{
            // sending
            this.getTextOut().println(message);
            this.getTextOut().flush();

            // Logging
            this.server.getLogger().messageSent(this, message);
        }
        catch(Exception e){
            MyLogger.logError("Object on port : " + this.getLocalPort() + " unable to send : " + message +  " to object on port : " + this.getPort());
        }
    }

    /////////////////////////
    // GETTERS AND SETTERS //
    /////////////////////////

    protected PrintWriter getTextOut(){
        return this.textOut;
    }

    protected OutputStream getDataOut(){
        return this.dataOut;
    }
}
