package Network;

import java.net.SocketException;

import Token.RequestTokenizer;
import Token.Token;

/**
 * Represents a connection between a Server and a connecting object.
 * 
 * Is a Thread so that the requests coming to the Server can be handled on a new thread,
 * which allows for one Server to server multiple connecting objects.
 * 
 * When the Thread is run, the connection waits for a request and then passes this
 * request onto the underlying Server's request handler.
 */
public class ServerThread extends Thread {
    
    // member variables
    private Server server;
    private Connection connection;
    private boolean isActive;

    /**
     * Class constructor.
     * 
     * @param server The Server object involved in the connection.
     * @param connection The conection between the Server and the connector.
     */
    public ServerThread(Server server, Connection connection){
        this.server = server;
        this.connection = connection;
        this.isActive = true;
    }

    /**
     * Method run when thread started.
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
                // getting request
                Token requestToken = RequestTokenizer.getToken(this.connection.getMessage());

                // handling request
                this.server.getRequestHandler().handleRequest(this.connection, requestToken);
            }
        }
        catch(NullPointerException e){
            // Connector disconnected - passing it on to the server to handle
            this.server.handleDisconnect(this.getConnection().getSocket().getPort(), new Exception("Connection terminated client side"));
        }
        catch(Exception e){
            this.server.getServerInterface().handleError(this.server.getType().toString() + " on port : " + this.connection.getSocket().getLocalPort() + " unable to handle request from port : " + this.connection.getSocket().getPort(), e);
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
    
    public Connection getConnection(){
        return this.connection;
    }

    public boolean isActive(){
        return this.isActive;
    }
}