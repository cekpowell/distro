package Controller;

import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import Index.State.OperationState;
import Logger.Protocol;
import Network.*;
import Token.*;
import Token.TokenType.*;


/**
 * Handles requests sent to a Controller by a DSClient.
 */
public class ControllerRequestHandler implements RequestHandler{

    // member variables
    private Controller controller;

    /**
     * Class constructor.
     * 
     * @param controller The Controller associated with the request handler.
     */
    public ControllerRequestHandler(Controller controller){
        // initialising member variables
        this.controller = controller;
    }

    /**
     * Handles a given request.
     * 
     * @param request Tokenized request to be handled.
     */
    public void handleRequest(Connection connection, Token request){

        // DStore Requests //

        if(request instanceof JoinToken){
            // gathering JOIN token
            JoinToken joinRequest = (JoinToken) request;
            int dstorePort = joinRequest.port;

            this.handleJoinRequest(connection, dstorePort);
        }

        else if(request instanceof StoreAckToken){
            StoreAckToken storeAckToken = (StoreAckToken) request;
            this.handleStoreAckRequest(connection, storeAckToken.filename); 
        }

        // Client Requests //

        else if(request instanceof ListToken){
            this.handleListRequest(connection);
        }

        else if(request instanceof StoreToken){
            StoreToken storeToken = (StoreToken) request;
            this.handleStoreRequest(connection, storeToken.filename, storeToken.filesize);
        }

        // Invalid Request //

        else{
            this.handleInvalidRequest(connection);
        }
    }

    /**
     * Handles a JOIN request.
     * @param connection The connection associated with the request.
     * @param dstorePort The port number of the Dstore joining the system.
     */
    public void handleJoinRequest(Connection connection, int dstorePort){
        // addding the Dstore to the controller
        this.controller.getIndex().addDstore(dstorePort, connection);
    }

    /**
     * Handles a request to store a file in the system.
     * 
     * @param connection The connection associated with the request.
     * @param filename The name of the file being stored.
     * @param filesize The size of the file being stored.
     */
    public void handleStoreRequest(Connection connection, String filename, int filesize){
        try{    
            // starting to store the file
            ArrayList<Integer> dstores = this.controller.getIndex().startStoring(filename, filesize);

            // sending the store message to the Client
            ArrayList<String> stringDstores = new ArrayList<String>();
            for(int dstore : dstores){
                stringDstores.add(Integer.toString(dstore));
            }
            connection.sendMessage(Protocol.STORE_TO_TOKEN + " " + String.join(" ", stringDstores));

            // waiting for the store to be complete
            this.controller.getIndex().waitForOperationComplete(filename, this.controller.getTimeout(), OperationState.STORE_ACK_RECIEVED, OperationState.IDLE);

            // store complete, sending STORE_COMPLETE message to Client
            connection.sendMessage(Protocol.STORE_COMPLETE_TOKEN);
        }
        catch(TimeoutException e){
            this.controller.getServerInterface().handleError("Timeout occured on STORE request sent by Client on port : " + connection.getSocket().getPort());
        }
        catch(Exception e){
            this.controller.getServerInterface().handleError("Unable to handle STORE request sent by Client on port : " + connection.getSocket().getPort());
        }
    }

    /**
     * Handles a STORE_ACK token.
     * 
     * @param connection The connection the STORE_ACK was receieved from.
     * @param filename The filename associiated with the STORE_ACK.
     */
    private void handleStoreAckRequest(Connection connection, String filename){
        this.controller.getIndex().storeAckRecieved(connection, filename);
    }

    /**
     * Handles a LIST request.
     */
    private void handleListRequest(Connection connection){
        try{
            // gathering list of files
            ArrayList<String> messageElements = new ArrayList<String>();
            messageElements.add("LIST");
            messageElements.addAll(this.controller.getIndex().getFiles());

            // sending response back to client
            String message = String.join(" ", messageElements);
            if(messageElements.size() == 1) message += " "; // ERROR FIX : for case when there are no files, still need to add the space to make sure it is tokenized correctly on client side
            connection.sendMessage(message);
        }
        catch(Exception e){
            //TODO need to test for different types of exception to know where the error occuredd - e.g., SocketTimeoutException, NullPointerException, etc...
            this.controller.getServerInterface().handleError("Unable to handle LIST request for Client on port : " + connection.getSocket().getPort());
        }
    }

    /**
     * Handles an invalid request.
     */
    public void handleInvalidRequest(Connection connection){
        this.controller.getServerInterface().handleError("Invalid request recieved from connector on port : " + connection.getSocket().getPort());
    }
}

/**
 * /**
     * Handles a LIST request.
     * 
     * ### CURRENT LOGIC DONE FOR TESTING ###
     * 
     * CURRENTLY DOING WHAT IT DOESNT NEED TO DO - SENDS LIST COMMAND TO ALL INDIVIDUAL DSTORES.
     * NEEDS TO JUST USE THE INDEX IN THE CONTROLLER.
     * 
     * ALSO - it sends the LIST request to the Dstore's listen port, and does not use the existing 
     * connection that the Controller has with the Dstore - this may lead to some errors later down the line
     *
    private void handleListRequest(Connection connection){
        try{
            ArrayList<String> messageElements = new ArrayList<String>();
            messageElements.add("LIST");
    
            // looping through list of Dstores
            for(DstoreIndex dstore : this.controller.getIndex().getDstores()){

                // setting up socket
                Connection dstoreConnection = new Connection(this.controller.getServerInterface(), InetAddress.getLocalHost(), dstore.getPort());
    
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
            connection.sendMessage(message);
        }
        catch(Exception e){
            //TODO need to test for different types of exception to know where the error occuredd - e.g., SocketTimeoutException, NullPointerException, etc...
            this.controller.getServerInterface().handleError("Unable to handle LIST request for Client on port : " + connection.getSocket().getPort());
        }
    }
 */