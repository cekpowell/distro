package Network;

import Token.*;

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
     * 
     */
    public void listenForHeartbeat(){
        try{
            while(this.isActive){
                // getting heartbeat from connnection
                Token heartbeat = RequestTokenizer.getToken(this.connection.getMessage());
            }
        }
        catch(NullPointerException e){
            // Connector disconnected - passing it on to the server to handle
            this.client.handleServerDisconnect(new Exception("Connection terminated server side"));
        }
        catch(Exception e){
            this.client.getClientInterface().handleError("Error listening for heartbeat on port : " + this.connection.getSocket().getLocalPort() + " for Controller on port " + this.connection.getSocket().getPort(), e);
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
