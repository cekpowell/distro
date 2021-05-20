package Server;

import java.net.Socket;

/**
 * Abstract class to represent a Server object. 
 * 
 * Servers listen for new connectors and create new Connection objects to 
 * handle the request(s) the connector.
 */
public abstract class Server {

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
}
