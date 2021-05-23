package Controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

import Logger.Protocol;
import Server.*;
import Token.*;
import Token.TokenType.*;


/**
 * Handles requests sent to a Controller by a DSClient.
 */
public class ControllerRequestHandler extends RequestHandler{

    // member variables
    private Controller controller;

    /**
     * Class constructor.
     * 
     * @param controller The Controller associated with the request handler.
     */
    public ControllerRequestHandler(Controller controller){
        // initialising member variables
        super(controller);
        this.controller = controller;
    }

    /**
     * Handles a given request.
     * 
     * @param request Tokenized request to be handled.
     */
    public void handleRequest(ServerConnection connection, Token request){

        boolean clientRequest = true;

        //////////////////////
        // Handling request //
        //////////////////////

        // DStore Requests //

        if(request instanceof JoinToken){
            clientRequest = false;
            // gathering JOIN token
            JoinToken joinRequest = (JoinToken) request;
            int dstorePort = joinRequest.port;

            this.handleJoinRequest(connection, dstorePort);
        }

        // Client Requests //

        else if(request instanceof ListToken){
            this.handleListRequest(connection);
        }

        // Invalid Request //
        else if(request instanceof InvalidRequestToken){
            this.handleInvalidRequest(connection);
        }

        // Request tokenized but not expected by controller -(i.e., the request is not relevant for the controller) //
        else{
            this.handleInvalidRequest(connection);
        }

        // TODO Handle rest of request types

        // Adding the connector to the controller if they are a client
        if(clientRequest){
            // testing if the client is new
            if(!this.controller.getClients().contains(connection)){
                // adding the client to the ccontroller
                this.controller.addClient(connection);
            }
        }
    }

    /**
     * Handles a JOIN request.
     * @param connection The connection associated with the request.
     * @param dstorePort The port number of the Dstore joining the system.
     */
    public void handleJoinRequest(ServerConnection connection, int dstorePort){
        // addding the Dstore to the controller
        this.controller.addDstore(connection, dstorePort);
    }

    /**
     * Handles a LIST request.
     * 
     * ### CURRENT LOGIC DONE FOR TESTING ###
     * 
     * CURRENTLY DOING WHAT IT DOESNT NEED TO DO - SENDS LIST COMMAND TO ALL INDIVIDUAL DSTORES.
     * NEEDS TO JUST USE THE INDEX IN THE CONTROLLER.
     * 
     * ALSO - it sends the LIST request to the Dstore's listen port, and does not use the existing 
     * connection that the Controller has with the Dstore - this may lead to some errors later down the line
     */
    private void handleListRequest(ServerConnection connection){
        try{
            ArrayList<String> messageElements = new ArrayList<String>();
            messageElements.add("LIST");
    
            // looping through list of Dstores
            for(ServerConnection dstore : this.controller.getdstores().keySet()){

                // setting up socket
                int dstoreListenPort = this.controller.getdstores().get(dstore);
                Connection dstoreConnection = new Connection(this.controller.getServerInterface(), InetAddress.getLocalHost(), dstoreListenPort);
    
                // sending request to dstore
                String request = Protocol.LIST_TOKEN;
                dstoreConnection.sendMessage(request);
    
                // gathering response
                Token response = RequestTokenizer.getToken(dstoreConnection.getMessage());
    
                if(response instanceof ListFilesToken){
                    // adding response to message elements
                    ListFilesToken listFilesToken = (ListFilesToken) response;
                    messageElements.addAll(listFilesToken.filenames);
                }
            }
    
            // sending response back to client
            String message = String.join(" ", messageElements);
            if(messageElements.size() == 1) message += " "; // ERROR FIX : for case when there are no files, still need to add the space to make sure it is tokenized correctly on client side
            connection.getConnection().sendMessage(message);
        }
        catch(Exception e){
            //TODO need to test for different types of exception to know where the error occuredd - e.g., SocketTimeoutException, NullPointerException, etc...
            this.controller.getServerInterface().handleError("Unable to handle LIST request for Client on port : " + connection.getConnection().getSocket().getPort());
        }
    }

    /**
     * Handles an invalid request.
     */
    public void handleInvalidRequest(ServerConnection connection){
        this.controller.getServerInterface().handleError("Invalid request recieved from connector on port : " + connection.getConnection().getSocket().getPort());
    }
}
