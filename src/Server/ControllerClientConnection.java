package Server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

import Controller.Controller;
import Logger.*;
import Token.*;

/**
 * Represents a connection from Controller to Client.
 */
public class ControllerClientConnection extends Connection{

    // member variables
    private Controller controller;

    /**
     * Class constructor.
     * 
     * @param controller The Controller involved in the connection.
     * @param connection The socket connecting the Controller to the Client.
     * @param initialRequest The initial request recieved by the Controller when the Client conected.
     */
    public ControllerClientConnection(Controller controller, Socket connection, Token initialRequest){
        // initialising member variables
        super(controller, connection, initialRequest);
        this.controller = controller;
    }

    /**
     * Starts listening for incoming requests.
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
            MyLogger.logEvent("Client disconnected on port : " + this.getConnection().getPort()); // MY LOG

            // removing the client from the index
            this.controller.dropClient(this);
        }
        catch(Exception e){
            MyLogger.logError("Unable to gather user input for Client." + e.toString());
        }
    }

    /**
     * Handles a given request.
     * 
     * @param request Tokenized request to be handled.
     */
    public void handleRequest(Token request){
        // Logging request
        ControllerLogger.getInstance().messageReceived(this.getConnection(), request.request);

        // handling request
        // TODO
    }
}
