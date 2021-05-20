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
public abstract class ServerConnection extends Thread {
    
    // member variables
    private Server server;
    private Socket connection;
    private boolean furtherRequests;
    
    private PrintWriter textOut;
    private BufferedReader textIn;
    private OutputStream dataOut;
    private InputStream dataIn;

    /**
     * Class constructor.
     * 
     * @param server The Server object involved in the connection.
     * @param connection The conection between the Server and the connector.
     */
    public ServerConnection(Server server, Socket connection){
        this.server = server;
        this.connection = connection;
        this.furtherRequests = true;
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

    public boolean hasFurtherRequests(){
        return this.furtherRequests;
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

    public void noFurtherRequests(){
        this.furtherRequests = false;
    }
}
