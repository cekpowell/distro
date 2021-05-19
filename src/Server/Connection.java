package Server;

import java.net.Socket;

import Token.Token;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * Represents a connection between a server and a connecting object.
 * 
 * Created as a new thread to handle the requests coming from the connecting object.
 */
public abstract class Connection extends Thread {
    
    // member variables
    private Server server;
    private Socket connection;
    private Token initialRequest;
    
    private PrintWriter textOut;
    private BufferedReader textIn;
    private OutputStream dataOut;
    private InputStream dataIn;

    /**
     * Class constructor.
     * 
     * @param server The Server object involved in the connection.
     * @param connection The conection between the Server and the connector.
     * @param initialRequest The initial request sent to the Server.
     */
    public Connection(Server server, Socket connection, Token initialRequest){
        this.server = server;
        this.connection = connection;
        this.initialRequest = initialRequest;
        try{
            this.textOut = new PrintWriter (new OutputStreamWriter(connection.getOutputStream())); 
            this.textIn = new BufferedReader (new InputStreamReader(connection.getInputStream()));
            this.dataOut = connection.getOutputStream();
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
        // handling the initial request
        this.handleRequest(initialRequest);

        // listening for future requests
        this.startListening();
    }

    /**
     * Starts listening for incoming requests.
     */
    public abstract void startListening();

    /**
     * Handles a given request.
     * 
     * @param request Tokenized request to be handled.
     */
    public abstract void handleRequest(Token request);

    /**
     * Getters and setters
     */

    
    public Socket getConnection(){
        return this.connection;
    }

    public PrintWriter getTextOut(){
        return this.textOut;
    }

    public BufferedReader getTextIn(){
        return this.textIn;
    }

    public OutputStream getDataOut(){
        return this.dataOut;
    }

    public InputStream getDataIn(){
        return this.dataIn;
    }
}
