package Server;

import Token.*;

/**
 * Represents a Heartbeat connection from Client to Server.
 * 
 * A Client can create a new Heartbeat connection to a Server,
 * and the 'handleServerDisconnect' method on the Client will be called
 * when the connection drops.
 * 
 * The Heartbeat is detected by trying to tokenize the input stream on the connection.
 * 
 * If the connection drops, the input stream returns the null string, and the tokenizer 
 * throws a NullPointerException. Catching this exception allows for the detection of the 
 * Server disconnecting from the client.
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
                Token heartbeat = RequestTokenizer.getToken(this.connection.getTextIn().readLine());
            }
        }
        catch(NullPointerException e){
            // Connector disconnected - passing it on to the server to handle
            this.client.handleServerDisconnect();
        }
        catch(Exception e){
            this.client.getClientInterface().handleError("Error listening for heartbeat on port : " + this.connection.getLocalPort() + " for Controller on port " + this.connection.getPort());
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
