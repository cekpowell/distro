package Network.Client;

import Network.Connection;
import Network.Protocol.Exception.ConnectionTerminatedException;

/**
 * Represents a Heartbeat connection from Client to Server.
 * 
 * A Client can create a new Heartbeat connection to a Server, and the 'handleServerDisconnect' 
 * method on the Client will be called when the connection drops.
 */
public class HeartbeatConnection extends Thread{

    // member variables
    Client client;
    Connection connection;
    boolean isActive;

    /**
     * Class constructor.
     * 
     * @param client The Client associated with the heartbeat.
     * @param connection Connection between two objects.
     */
    public HeartbeatConnection(Client client, Connection connection){
        this.client = client;
        this.connection = connection;
        this.isActive = true;
    }

    /**
     * Method run when thread started.
     */
    public void run(){
        // listening for future requests
        this.listenForHeartbeat();
    }

    /**
     * Maintains heartbeat from Client to Server.
     */
    public void listenForHeartbeat(){
        try{
            // looping while connection open
            while(this.connection.isOpen()){
                // getting heartbeat from connnection
                String heartbeat = this.connection.getMessage();
            }
        }
        catch(Exception e){
            // error getting heartbeat = need to terminate connection
            this.client.handleError(new ConnectionTerminatedException(this.connection, e));
        }
    }

    /**
     * Called to stop the connection for looking for futher requests.
     * 
     * i.e., It ends the connection.
     */
    public void close(){
        this.connection.close();
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
