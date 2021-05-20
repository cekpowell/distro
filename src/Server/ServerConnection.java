package Server;

import java.net.Socket;

import Logger.MyLogger;
import Token.RequestTokenizer;
import Token.Token;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * Represents a connection between a Server and a connecting object.
 * 
 * Is a Thread so that the requests coming to the Server can be handled on a new thread,
 * which allows for one Server to server multiple connecting objects.
 * 
 * When the Thread is run, the connection waits for a request and then passes this
 * request onto the underlying Server's request handler.
 */
public class ServerConnection extends Thread {
    
    // member variables
    private Server server;
    private Socket connection;
    private boolean isActive;
    
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
        this.isActive = true;
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
     * Waits for an incoming request.
     */
    public void waitForRequest(){
        try{
            while(this.isActive()){
                // getting request from connnection
                Token request = RequestTokenizer.getToken(this.getTextIn().readLine());

                // handling request
                this.server.getRequestHandler().handleRequest(this, request);
            }
        }
        catch(NullPointerException e){
            // Connector disconnected - passing it on to the server to handle
            this.server.handleDisconnect(this.getConnection().getPort());
        }
        catch(Exception e){
            MyLogger.logError("Server on port : " + this.connection.getLocalPort() + " unable to connect to new connector.");
        }
    }

    /**
     * Called to stop the connection for looking for futher requests.
     * 
     * i.e., It ends the connection.
     */
    public void close(){
        this.isActive = false;
    }

    /////////////////////////
    // GETTERS AND SETTERS //
    /////////////////////////
    
    public Socket getConnection(){
        return this.connection;
    }

    public boolean isActive(){
        return this.isActive;
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
