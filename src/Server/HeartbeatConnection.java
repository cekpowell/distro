package Server;

import Token.*;

/**
 * 
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
     * Waits for an incoming request.
     * 
     * If the socket being listened on disconnects, the readLine method will return null,
     * and the getToken method on the null object will throw a null pointer exception.
     * 
     * It is behaviour that comes not by design.
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
