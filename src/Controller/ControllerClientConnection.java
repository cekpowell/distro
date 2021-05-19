package Controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

import Connection.Connection;
import Logger.*;
import Token.*;

/**
 * Represents the connection from Controller to Client.
 */
public class ControllerClientConnection extends Connection{

    // member variables
    private Controller controller;
    private Token initialRequest;

    /**
     * Class constructor.
     * @param controller The Controller involved in the connection.
     * @param connection The socket connecting the Controller to the Client.
     * @param initialRequest The initial request recieved by the Controller when the Client conected.
     */
    public ControllerClientConnection(Controller controller, Socket connection, Token initialRequest){
        super(controller, connection);
        this.controller = controller;
        this.initialRequest = initialRequest;
    }

    /**
     * Method run when thread started.
     */
    public void run(){
        this.handleRequest(initialRequest);

        this.startListening();
    }

    /**
     * Listens for incoming requests from the Client.
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
     * Deals with the provided request.
     * @param request The request to be handeled.
     */
    public void handleRequest(Token request){
        // Logging request
        ControllerLogger.getInstance().messageReceived(this.getConnection(), request.request);

        // handling request
        // TODO
    }
}
