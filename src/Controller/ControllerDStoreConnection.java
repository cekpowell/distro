package Controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

import Connection.*;
import Logger.*;
import Token.*;

/**
 * Represents the connection from Controller to DStore.
 */
public class ControllerDstoreConnection extends Connection{

    // member variables
    private Controller controller;
    private int dPort;
    
    /**
     * Class constructor.
     * @param controller Controller involved in the connection.
     * @param connection The connection between the Controller and the DStore.
     */
    public ControllerDstoreConnection(Controller controller, int dPort, Socket connection){
        super(controller, connection);
        this.controller = controller;
        this.dPort = dPort;
    }

    /**
     * Method run when thread started.
     */
    public void run(){
        this.startListening();
    }

    /**
     * Starts listening for incoming requests from the DStore.
     */
    public void startListening(){
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(this.getConnection().getInputStream()));

            // Wait for message 
            while(true){
                // reading in message
                String request = reader.readLine();

                // handling request
                this.handleRequest(RequestTokenizer.getToken(request));
            }
        }
        catch(NullPointerException e){
            // Client disconnected - nothing to do.
            MyLogger.logEvent("Dstore disconnected on port : " + this.getConnection().getPort()); // MY LOG

            // removing the DStore from the index
            this.controller.dropDstore(this);
        }
        catch(Exception e){
            MyLogger.logError("Unable to gather request from DStore on port : " + this.getConnection().getPort());
        }
    }

    /**
     * Hanndles a given tokenized request.
     * @param Token The request to be handled.
     */
    public void handleRequest(Token request){
        // Logging request
        ControllerLogger.getInstance().messageReceived(this.getConnection(), request.request);

        // handling request
        // TODO
    }

    /**
     * Getters and setters.
     */

    public Controller getController(){
        return this.controller;
    }

    public int getdPort(){
        return this.dPort;
    }
}
