package Server;

import java.net.Socket;

import Logger.Logger;

/**
 * Abstract class to represent a Server object. 
 * 
 * Servers listen for new connectors and create new Connection objects to 
 * handle the request(s) the connector.
 */
public abstract class Server {

    // member variables
    private RequestHandler requestHandler;
    private Logger logger;

    /**
     * Starts the server listening for connections.
     */
    public abstract void startListening();

    /**
     * Set's up a connection between the server and the connected object.
     * 
     * @param connector The object that has connected to the server.
     */
    public abstract void setUpConnection(Socket connector);


    /////////////////////////
    // GETTERS AND SETTERS //
    /////////////////////////
    
    
    public RequestHandler getRequestHandler(){
        return this.requestHandler;
    }

    public Logger getLogger(){
        return this.logger;
    }

    public void setRequestHandler(RequestHandler requestHandler){
        this.requestHandler = requestHandler;
    }

    public void setLogger(Logger logger){
        this.logger = logger;
    }
}
