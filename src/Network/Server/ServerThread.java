package Network.Server;

import DS.Protocol.Token.RequestTokenizer;
import DS.Protocol.Token.Token;
import Network.Connection;
import Network.Protocol.Exception.ConnectionTerminatedException;

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
            while(this.connection.isOpen()){
                // getting request
                String request = this.connection.getMessage();

                // tokenizing request
                Token requestToken = RequestTokenizer.getToken(request);

                // handling request
                this.server.getRequestHandler().handleRequest(this.connection, requestToken);
            }
        }
        catch(Exception e){
            // error getting request = need to terminate connection
            this.server.handleError(new ConnectionTerminatedException(this.connection, e));
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