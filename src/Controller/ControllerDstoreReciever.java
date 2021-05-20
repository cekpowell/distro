package Controller;

import java.net.Socket;

import Logger.*;
import Server.Reciever;
import Token.*;

/**
 * Represents a conection from a Controller to a DStore.
 * 
 * Used to recieve control messages from the Dstore.
 * 
 * These control messages are ACK messages for STORE, LOAD and REMOVE.
 */
public class ControllerDstoreReciever extends Reciever {
    
    // member variables
    private Controller controller;
    private int dstoreListenPort;

    /**
     * Class constructor.
     * 
     * @param controller The Controller involved in the connection.
     * @param connection The socket connecting the Controller to the Client.
     * @param dstoreListenPort The port that the Dstore involved in this communication will listeen to client requests on.
     */
    public ControllerDstoreReciever(Controller controller, Socket connection, int dstoreListenPort){
        // initialising member variables
        super(controller, connection);
        this.controller = controller;
        this.dstoreListenPort = dstoreListenPort;

        // adding the dstore connection to the Controller
        this.controller.addDstore(this);
    }

    /**
     * Starts listening for incoming messages from the Dstore.
     */
    public void waitForMessage(){
        try{
            while(true){
                // getting request from connection
                Token request = RequestTokenizer.getToken(this.getTextIn().readLine());

                // handling request in a new thread
                new Thread(() -> {this.handleMessage(request);}).start();
            }
        }
        catch(NullPointerException e){
            // Connnector disconnected //

            // Logging error
            MyLogger.logEvent("Dstore listening on port : " + this.dstoreListenPort + " disconnected."); // MY LOG

            // Removing Dstore from Controller
            this.controller.removeDstore(this);
        }
        catch(Exception e){
            MyLogger.logError("Controller unable to recieve message from Dstore listening on port : " + this.dstoreListenPort);
        }
    }

    /**
     * Handles a given message.
     * 
     * @param request The request to be handled.
     */
    public void handleMessage(Token request){
        // TODO Handle incoming messages from Dstore
    }

    /////////////////////////
    // GETTERS AND SETTERS //
    /////////////////////////

    public int getDstoreListenPort(){
        return this.dstoreListenPort;
    }
}