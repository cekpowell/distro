package Server;

import java.net.Socket;

import Logger.MyLogger;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * Represents a connection between a Server and a connector, where the Sevrer only sends
 * messages to the connector.
 */
public class Sender {

    // member variables
    private Server server;
    private Socket connection;
    
    private PrintWriter textOut;
    private OutputStream dataOut;

    /**
     * Class constructor.
     * 
     * @param server The Server object involved in the connection.
     * @param connection The conection between the Server and the connector.
     */
    public Sender(Server server, Socket connection){
        this.server = server;
        this.connection = connection;
        try{
            this.textOut = new PrintWriter (new OutputStreamWriter(connection.getOutputStream())); 
            this.dataOut = connection.getOutputStream();
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
            this.server.getLogger().messageSent(this.getConnection(), message);
        }
        catch(Exception e){
            MyLogger.logError("Object on port : " + this.connection.getLocalPort() + " unable to send : " + message +  " to object on port : " + this.getConnection().getPort());
        }
    }

    /////////////////////////
    // GETTERS AND SETTERS //
    /////////////////////////
    
    public Socket getConnection(){
        return this.connection;
    }

    protected PrintWriter getTextOut(){
        return this.textOut;
    }

    protected OutputStream getDataOut(){
        return this.dataOut;
    }
}
