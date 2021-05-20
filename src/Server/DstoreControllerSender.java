package Server;

import java.net.Socket;

import Dstore.*;
import Logger.*;

/**
 * Represents a connection from a Dstore to a Controller.
 * 
 * Used to JOIN the Dstore to the Controller and send control messages 
 * that are not part of requests made by the Controller.
 * 
 * These control messages are ACK messages for STORE, LOAD and REMOVE.
 */
public class DstoreControllerSender extends SenderConnection{
    
    // member variables
    private Dstore dstore;

    /**
     * Class constructor.
     * 
     * @param dstore The Dstore involved in the connection.
     * @param connection The connection between the Dstore and the client
     */
    public DstoreControllerSender(Dstore dstore, Socket connection){
        // initialising member variables
        super(dstore, connection);
        this.dstore = dstore;

        // sending JOIN request to controller
        this.sendJoinRequest();
    }

    /**
     * Sends a JOIN request to the controller.
     */
    public void sendJoinRequest(){
        // Sending JOIN request to controller
        String message = Protocol.JOIN_TOKEN + " " + this.dstore.getPort();
        this.sendMessage(message);
    }

    /**
     * Sends the given message to the through the connection.
     * 
     * @param message To be sent through the connection.
     */
    public void sendMessage(String message){
        try{
            // sending
            this.getTextOut().println(message);
            this.getTextOut().flush();

            // Logging
            DstoreLogger.getInstance().messageSent(this.getConnection(), message);
        }
        catch(Exception e){
            MyLogger.logError("DStore on port : " + this.dstore.getPort() + " unable to send : " + message +  " to Controller on port : " + this.getConnection().getPort());
        }
    }
}