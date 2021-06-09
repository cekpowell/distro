package Controller;

import java.util.ArrayList;

import Controller.Index.State.OperationState;
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

    //////////
    // MAIN //
    //////////

    /**
     * Handles a given request.
     * 
     * @param request Tokenized request to be handled.
     */
    public void handleRequest(Connection connection, Token request){

        // JOIN
        if(request instanceof JoinToken){
            JoinToken joinToken = (JoinToken) request;
            this.handleJoinRequest(connection, joinToken.port);
        }

        // STORE
        else if(request instanceof StoreToken){
            StoreToken storeToken = (StoreToken) request;
            this.handleStoreRequest(connection, storeToken.filename, storeToken.filesize);
        }

        // STORE_ACK
        else if(request instanceof StoreAckToken){
            StoreAckToken storeAckToken = (StoreAckToken) request;
            this.handleStoreAckRequest(connection, storeAckToken.filename); 
        }

        // LOAD
        else if(request instanceof LoadToken){
            LoadToken loadToken = (LoadToken) request;
            this.handleLoadRequest(connection, loadToken.filename, false);
        }
        
        // RELOAD
        else if(request instanceof ReloadToken){
            ReloadToken reloadToken = (ReloadToken) request;
            this.handleLoadRequest(connection, reloadToken.filename, true);
        }

        // REMOVE
        else if(request instanceof RemoveToken){
            RemoveToken removeToken = (RemoveToken) request;
            this.handleRemoveRequest(connection, removeToken.filename);
        }

        // REMOVE_ACK
        else if(request instanceof RemoveAckToken){
            RemoveAckToken removeAckToken = (RemoveAckToken) request;
            this.handleRemoveAckRequest(connection, removeAckToken.filename);
        }

        // LIST
        else if(request instanceof ListToken){
            this.handleListRequest(connection);
        }

        // ERROR_FILE_DOES_NOT_EXIST
        else if(request instanceof ErrorFileDoesNotExistFilenameToken){
            // nothing to do...
        }

        // Invalid Request
        else{
            this.handleInvalidRequest(connection, request);
        }
    }

    //////////
    // JOIN //
    //////////

    /**
     * Handles a JOIN request.
     * @param connection The connection associated with the request.
     * @param dstorePort The port number of the Dstore joining the system.
     */
    public void handleJoinRequest(Connection connection, int dstorePort){
        // addding the Dstore to the controller
        this.controller.getIndex().addDstore(dstorePort, connection);
    }

    ///////////
    // STORE //
    ///////////

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
            this.controller.getIndex().waitForOperationComplete(filename, this.controller.getTimeout(), OperationState.STORE_ACK_RECIEVED);

            // store complete, sending STORE_COMPLETE message to Client
            connection.sendMessage(Protocol.STORE_COMPLETE_TOKEN);
        }
        catch(Exception e){
            // logging error
            this.controller.getServerInterface().handleError("Unable to handle STORE request from Client on port : " + connection.getPort(), e);

            // Handling Specific Cases //

            // Not enough Dstores
            if(e.getMessage().equals("Not enough Dstores")){
                try{
                    // sending error message to client
                    connection.sendMessage(Protocol.ERROR_NOT_ENOUGH_DSTORES_TOKEN);
                }
                catch(Exception ex){
                    this.controller.getServerInterface().handleError("Unable to send error message : " + Protocol.ERROR_NOT_ENOUGH_DSTORES_TOKEN + " to Client on port : " + connection.getPort(), ex);
                }
            }
            // File already exists
            else if(e.getMessage().equals("File already exists")){
                try{
                    // sending error message to client
                    connection.sendMessage(Protocol.ERROR_FILE_ALREADY_EXISTS_TOKEN);
                }
                catch(Exception ex){
                    this.controller.getServerInterface().handleError("Unable to send error message : " + Protocol.ERROR_FILE_ALREADY_EXISTS_TOKEN + " to Client on port : " + connection.getPort(), ex);
                }
            }
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

    //////////
    // LOAD //
    //////////

    /**
     * Handles a LOAD request.
     * 
     * @param connection The connection associated with the request.
     * @param filename The name of the file being loaded.
     */
    private void handleLoadRequest(Connection connection, String filename, boolean isReload){
        try{
            // getting the dstore to store on
            int dstoreToLoadFrom = this.controller.getIndex().getDstoreToLoadFrom(connection, filename, isReload);

            // getting the file size
            int filesize = this.controller.getIndex().getFileSize(filename);

            // sending LOAD_FROM to the Client
            connection.sendMessage(Protocol.LOAD_FROM_TOKEN + " " + dstoreToLoadFrom + " " + filesize);
        }
        catch(Exception e){
            // logging error
            this.controller.getServerInterface().handleError("Unable to handle STORE request from Client on port : " + connection.getPort(), e);

            // Handling Specific Cases //

            // Not enough Dstores
            if(e.getMessage().equals("Not enough Dstores")){
                try{
                    // sending error message to client
                    connection.sendMessage(Protocol.ERROR_NOT_ENOUGH_DSTORES_TOKEN);
                }
                catch(Exception ex){
                    this.controller.getServerInterface().handleError("Unable to send error message : " + Protocol.ERROR_NOT_ENOUGH_DSTORES_TOKEN + " to Client on port : " + connection.getPort(), ex);
                }
            }
            // File does not exist
            else if(e.getMessage().equals("File does not exist")){
                try{
                    // sending error message to client
                    connection.sendMessage(Protocol.ERROR_FILE_DOES_NOT_EXIST_TOKEN);
                }
                catch(Exception ex){
                    this.controller.getServerInterface().handleError("Unable to send error message : " + Protocol.ERROR_FILE_DOES_NOT_EXIST_TOKEN + " to Client on port : " + connection.getPort(), ex);
                }
            }
            // No valid Dstores
            else if(e.getMessage().equals("No valid Dstore")){
                try{
                    // sending error messagr to client
                    connection.sendMessage(Protocol.ERROR_LOAD_TOKEN);
                }
                catch(Exception ex){
                    this.controller.getServerInterface().handleError("Unable to send error message : " + Protocol.ERROR_LOAD_TOKEN + " to Client on port : " + connection.getPort(), ex);
                }
            }
        }
    }


    ////////////
    // REMOVE //
    ////////////


    /**
     * Handles a REMOVE request.
     * 
     * @param connection The connection associated witht the request.
     * @param filename The name of the file being removed.
     */
    private void handleRemoveRequest(Connection connection, String filename){
        try{    
            // starting to remove the file
            ArrayList<Connection> dstores = this.controller.getIndex().startRemoving(filename);

            // looping through Dstores
            for(Connection dstore : dstores){
                // sending REMOVE message to the Dstore
                dstore.sendMessage(Protocol.REMOVE_TOKEN + " " + filename);
            }

            // waiting for the REMOVE to be complete
            this.controller.getIndex().waitForOperationComplete(filename, this.controller.getTimeout(), OperationState.REMOVE_ACK_RECIEVED);

            // store complete, sending REMOVE_COMPLETEE message to Client
            connection.sendMessage(Protocol.REMOVE_COMPLETE_TOKEN);
        }
        catch(Exception e){
            // logging error
            this.controller.getServerInterface().handleError("Unable to handle REMOVE request sent by Client on port : " + connection.getPort(), e);

            // Handling Specific Cases //

            // Not enough Dstores
            if(e.getMessage().equals("Not enough Dstores")){
                try{
                    // sending error message to client
                    connection.sendMessage(Protocol.ERROR_NOT_ENOUGH_DSTORES_TOKEN);
                }
                catch(Exception ex){
                    this.controller.getServerInterface().handleError("Unable to send error message : " + Protocol.ERROR_NOT_ENOUGH_DSTORES_TOKEN + " to Client on port : " + connection.getPort(), ex);
                }
            }
            // File does not exist
            else if(e.getMessage().equals("File does not exist")){
                try{
                    // sending error message to client
                    connection.sendMessage(Protocol.ERROR_FILE_DOES_NOT_EXIST_TOKEN);
                }
                catch(Exception ex){
                    this.controller.getServerInterface().handleError("Unable to send error message : " + Protocol.ERROR_FILE_DOES_NOT_EXIST_TOKEN + " to Client on port : " + connection.getPort(), ex);
                }
            }
        }
    }

    ////////////////
    // REMOVE ACK //
    ////////////////

    /**
     * Handles a REMOVE_ACK request.
     * 
     * @param connection The connection associated with the request.
     * @param filename The name of the file associated with the request.
     */
    private void handleRemoveAckRequest(Connection connection, String filename){
        this.controller.getIndex().removeAckRecieved(connection, filename);
    }

    //////////
    // LIST //
    //////////

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
            this.controller.getServerInterface().handleError("Unable to handle LIST request for Client on port : " + connection.getPort(), e);
        }
    }

    /////////////
    // INVALID //
    /////////////

    /**
     * Handles an invalid request.
     */
    public void handleInvalidRequest(Connection connection, Token request){
        this.controller.getServerInterface().handleError("Unable to handle request '" + request.message +  "' received from connector on port : " + connection.getPort(), new Exception("Request is invalid"));
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
            this.controller.getServerInterface().handleError("Unable to handle LIST request for Client on port : " + connection.getPort());
        }
    }
 */