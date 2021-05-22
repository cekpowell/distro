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
 * Handles requests sent to a Controller by a Client.
 */
public class ControllerRequestHandler extends RequestHandler{

    // member variables
    private Controller controller;

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

        /////////////////////
        // Logging request //
        /////////////////////

        this.controller.getServerInterface().logMessageReceived(connection.getConnection(), request.message);

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
                Socket dstoreConnection = new Socket(InetAddress.getLocalHost(), dstoreListenPort);

                // setting up streams
                PrintWriter out = new PrintWriter (new OutputStreamWriter(dstoreConnection.getOutputStream()));
                BufferedReader in = new BufferedReader (new InputStreamReader(dstoreConnection.getInputStream()));
    
                // sending request to dstore
                String request = Protocol.LIST_TOKEN;
                out.println(request);
                out.flush(); // closing the stream
    
                // Logging
                this.controller.getServerInterface().logMessageSent(dstoreConnection, request);
    
                // gathering response
                Token response = RequestTokenizer.getToken(in.readLine());
    
                // Logging 
                this.controller.getServerInterface().logMessageReceived(dstore.getConnection(), response.message);
    
                if(response instanceof ListFilesToken){
                    // adding response to message elements
                    ListFilesToken listFilesToken = (ListFilesToken) response;
                    messageElements.addAll(listFilesToken.filenames);
                }
            }
    
            // sending response back to client
            String message = String.join(" ", messageElements);
            if(messageElements.size() == 1) message += " "; // ERROR FIX : for case when there are no files, still need to add the space to make sure it is tokenized correctly on client side
            connection.getTextOut().println(message);
            connection.getTextOut().flush();
    
            // Logging 
            this.controller.getServerInterface().logMessageSent(connection.getConnection(), message);
        }
        catch(Exception e){
            this.controller.getServerInterface().logError("Unable to perform LIST command for Client on port : " + connection.getConnection().getPort());
        }
    }

    /**
     * Handles an invalid request.
     */
    public void handleInvalidRequest(ServerConnection connection){
        this.controller.getServerInterface().logError("Invalid request recieved from connector on port : " + connection.getConnection().getPort());
    }
}
