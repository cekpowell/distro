package Server;

import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import Token.Token;

/**
 * Represents a connection between a Server and a connector, where the Sevrer only recieves
 * messages from the connector.
 */
public abstract class RecieverConnection extends Thread{

    // member variables
    private Server server;
    private Socket connection;
    
    private BufferedReader textIn;
    private InputStream dataIn;

    /**
     * Class constructor.
     * 
     * @param server The Server object involved in the connection.
     * @param connection The conection between the Server and the connector.
     */
    public RecieverConnection(Server server, Socket connection){
        this.server = server;
        this.connection = connection;
        try{
            this.textIn = new BufferedReader (new InputStreamReader(connection.getInputStream()));
            this.dataIn = connection.getInputStream();
        }
        catch(Exception e){
            System.out.println("ERROR : Unable to create streams for a connection.");
        }
    }

        /**
     * Method run when thread started
     */
    public void run(){
        // listening for future requests
        this.waitForRequest();
    }

    /**
     * Starts listening for incoming requests.
     */
    public abstract void waitForRequest();

    /**
     * Handles a given request.
     * 
     * @param request Tokenized request to be handled.
     */
    public abstract void handleRequest(Token request);

    /////////////////////////
    // GETTERS AND SETTERS //
    /////////////////////////
    
    public Socket getConnection(){
        return this.connection;
    }

    public BufferedReader getTextIn(){
        return this.textIn;
    }

    public InputStream getDataIn(){
        return this.dataIn;
    }
}