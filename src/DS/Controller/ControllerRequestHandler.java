package DS.Controller;

import java.util.ArrayList;
import java.util.HashMap;

import DS.Controller.Index.State.OperationState;
import DS.Protocol.Protocol;
import DS.Protocol.Event.Operation.ListCompleteEvent;
import DS.Protocol.Event.Operation.LoadCompleteEvent;
import DS.Protocol.Event.Operation.RemoveCompleteEvent;
import DS.Protocol.Event.Operation.StoreCompleteEvent;
import DS.Protocol.Exception.*;
import DS.Protocol.Token.*;
import DS.Protocol.Token.TokenType.*;
import Network.*;
import Network.Client.Client.ClientType;
import Network.Protocol.Event.ServerConnectionEvent;
import Network.Protocol.Exception.*;
import Network.Server.RequestHandler;

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
        // initializing
        super(controller);
        this.controller = controller;
    }

    //////////
    // MAIN //
    //////////

    /**
     * Handles a given request.
     * 
     * @param connection The connection associated with the request.
     * @param request Tokenized request to be handled.
     */
    public void handleRequestAux(Connection connection, Token request){
        // handling request
        try{
            // JOIN_DSTORE
            if(request instanceof JoinDstoreToken){
                JoinDstoreToken joinToken = (JoinDstoreToken) request;
                this.handleJoinDstoreRequest(connection, joinToken.port);
            }

            // JOIN_CLIENT
            else if(request instanceof JoinClientToken){
                this.handleJoinClientRequest(connection, request);
            }

            // JOIN_CLIENT_HEARTBEAT
            else if(request instanceof JoinClientHeartbeatToken){
                JoinClientHeartbeatToken joinToken = (JoinClientHeartbeatToken) request;
                this.handleJoinClientHeartbeatRequest(connection, joinToken);
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

            // ERROR_FILE_DOES_NOT_EXIST
            else if(request instanceof ErrorFileDoesNotExistFilenameToken){
                // nothing to do ...
            }

            // LIST
            else if(request instanceof ListToken){
                this.handleListRequest(connection);
            }

            // LIST OF FILES (rebalancing)
            else if(request instanceof ListFilesToken){
                ListFilesToken listFilesToken = (ListFilesToken) request;
                this.handleListFilesRequest(connection, listFilesToken.files);
            }

            // REBALANCE COMPLETE
            else if(request instanceof RebalanceCompleteToken){
                this.handleRebalanceCompleteRequest(connection);
            }

            // Invalid Request
            else{
                this.handleInvalidRequest(connection, request);
            }
        }
        catch(Exception e){
            // loggiing error
            this.controller.handleError(new RequestHandlingException(request.message, e));

            // Handling Specific Cases //

            try{
                // Dstore port already in use
                if(e instanceof DstorePortInUseException){
                    // sending error message to Dstore
                    connection.sendMessage(Protocol.getErrorDstorePortInUseMessage());
                }
                // Not enough Dstores
                if(e instanceof NotEnoughDstoresException){
                    // sending error message to client
                    connection.sendMessage(Protocol.getErrorNotEnoughDstoresMessage());
                }
                // File already exists
                else if(e instanceof FileAlreadyExistsException){
                    // sending error message to client
                    connection.sendMessage(Protocol.getErrorFileAlreadyExistsMessage());
                }
                // File does not exist
                else if(e instanceof FileDoesNotExistException){
                    // sending error message to client
                    connection.sendMessage(Protocol.getErrorFileDoesNotExistMessage());
                }
                // No valid Dstores
                else if(e instanceof NoValidDstoresException){
                    // sending error message to client
                    connection.sendMessage(Protocol.getErrorLoadMessage());
                }
            }
            catch(MessageSendException ex){
                this.controller.handleError(ex);
            }
        }
    }

    /////////////////
    // JOIN_DSTORE //
    /////////////////

    /**
     * Handles a JOIN_DSTORE request.
     * 
     * @param connection The connection associated with the request.
     * @param dstorePort The port number of the Dstore joining the system.
     * @throws DstorePortInUseException If the port the Dstore is trying to join on is already in use.
     */
    public void handleJoinDstoreRequest(Connection connection, int dstorePort) throws Exception{
        // addding the Dstore to the index
        this.controller.getIndex().addDstore(dstorePort, connection);

        // adding the dstore to the server
        this.controller.getServerConnections().add(connection);

        // sending JOIN_ACK to Dstore
        connection.sendMessage(Protocol.getJoinAckMessage());

        // rebalancing system
        this.controller.getRebalancer().rebalance();
    }

    /////////////////
    // JOIN_CLIENT //
    /////////////////

    /**
     * Handles a JOIN_CLIENT request.
     * 
     * @param connection The connection the request came from.
     * @param request The request token.
     */
    public void handleJoinClientRequest(Connection connection, Token request) throws Exception{
        // adding the client to the server
        this.controller.getClientConnections().add(connection);

        // logging
        this.controller.handleEvent(new ServerConnectionEvent(ClientType.CLIENT, connection.getPort()));

        // sending JOIN_ACK to Client
        connection.sendMessage(Protocol.getJoinAckMessage());
    }

    ///////////////////////////
    // JOIN_CLIENT_HEARTBEAT //
    ///////////////////////////

    /**
     * Handles a JOIN_CLIENT_HEARTBEAT request.
     * 
     * @param connection The connection the request came from.
     * @param joinToken The request token.
     */
    public void handleJoinClientHeartbeatRequest(Connection connection, JoinClientHeartbeatToken joinToken) throws Exception{
        // adding the client to the server
        this.controller.getClientHeartbeatConnections().put(connection, joinToken.port);

        // sending JOIN_ACK to Client
        connection.sendMessage(Protocol.getJoinAckMessage());
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
     * @throws NotEnoughDstoresException If there are not enough Dstores connected to the Controller to handle the request.
     * @throws FileAlreadyExistsException If there is already a file under this name in the Index.
     * @throws MessageSendException If a message could not be sent through the connection.
     * @throws OperationTimeoutException If the store operation did not complete within the Controller timeout period.
     */
    public void handleStoreRequest(Connection connection, String filename, int filesize) throws Exception{
        // starting to store the file
        ArrayList<Integer> dstores = this.controller.getIndex().startStoring(filename, filesize);

        // sending the message to the client
        connection.sendMessage(Protocol.getStoreToMessage(dstores));

        // waiting for the store to be complete
        this.controller.getIndex().waitForFileState(filename, OperationState.STORE_ACK_RECIEVED, this.controller.getTimeout());

        // store complete, sending STORE_COMPLETE message to Client
        connection.sendMessage(Protocol.getStoreCompleteMessage());

        // logging
        this.controller.handleEvent(new StoreCompleteEvent(filename, filesize));
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
     * @throws NotEnoughDstoresException If there are not enough Dstores connected to the Controller to handle the request.
     * @throws FileDoesNotExist If there is no file in the Index with this name.
     * @throws NoValidDstoresException If there are no valid Dstores remaining to load the file form 
     * (exhausted all possible Dstores).
     * @throws MessageSendException If a message could not be sent through the connection.
     */
    private void handleLoadRequest(Connection connection, String filename, boolean isReload) throws Exception{
        // getting the dstore to store on
        int dstoreToLoadFrom = this.controller.getIndex().getDstoreToLoadFrom(connection, filename, isReload);

        // getting the file size
        int filesize = this.controller.getIndex().getFileSize(filename);

        // sending LOAD_FROM to the Client
        connection.sendMessage(Protocol.getLoadFromMessage(dstoreToLoadFrom, filesize));

        // logging
        this.controller.handleEvent(new LoadCompleteEvent(filename));
    }


    ////////////
    // REMOVE //
    ////////////


    /**
     * Handles a REMOVE request.
     * 
     * @param connection The connection associated witht the request.
     * @param filename The name of the file being removed.
     * @throws NotEnoughDstoresException If there are not enough Dstores connected to the Controller to handle the request.
     * @throws FileDoesNotExist If there is no file in the index with this name.
     * @throws MessageSendException If a message could not be sent through the connection.
     * @throws OperationTimeoutException If the remove operation did not complete within the Controller timeout period.
     */
    private void handleRemoveRequest(Connection connection, String filename) throws Exception{
        // starting to remove the file
        ArrayList<Connection> dstores = this.controller.getIndex().startRemoving(filename);

        // looping through Dstores
        for(Connection dstore : dstores){
            // sending REMOVE message to the Dstore
            dstore.sendMessage(Protocol.getRemoveMessage(filename));
        }

        // waiting for the REMOVE to be complete
        this.controller.getIndex().waitForFileState(filename, OperationState.REMOVE_ACK_RECIEVED, this.controller.getTimeout());

        // store complete, sending REMOVE_COMPLETEE message to Client
        connection.sendMessage(Protocol.getRemoveCompleteMessage());

        // logging
        this.controller.handleEvent(new RemoveCompleteEvent(filename));
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
     * 
     * @param connection The connection associated with the request.
     * @throws MessageSendException If a message could not be sent through the connection.
     * @throws NotEnoughDstoresException If there are not enough Dstores connected to handle the request.
     */
    private void handleListRequest(Connection connection) throws Exception{
        // sending message to client
        connection.sendMessage(Protocol.getListOfFilesMessage(this.controller.getIndex().getFileList()));

        // logging
        this.controller.handleEvent(new ListCompleteEvent());
    }

    ///////////////////
    // LIST OF FILES //
    ///////////////////

    /**
     * Handles the reception of a list of files from a Dstore (rebalancing).
     * 
     * @param connection The connection associated with the message.
     * @param files The list of files provided in the message.
     */
    private void handleListFilesRequest(Connection connection, HashMap<String, Integer> files){
        this.controller.getIndex().rebalanceListRecieved(connection, files);
    }

    /**
     * Handles the reception of a REBALANCE_COMPLETE message from a DSTORE.
     * 
     * @param connection The connection the message was received from.
     * @param files The list of files receieved in the message.
     */
    private void handleRebalanceCompleteRequest(Connection connection){
        this.controller.getIndex().rebalanceCompleteReceived(connection);
    }

    /////////////
    // INVALID //
    /////////////

    /**
     * Handles an invalid request.
     * 
     * @param connection The connection associated with the request.
     * @param request The tokenized form of the request.
     * @throws InvalidMessageException Always thrown after the request is "handeled".
     */
    public void handleInvalidRequest(Connection connection, Token request) throws Exception{
        // Nothing to do...

        // throwing exception
        throw new InvalidMessageException(request.message, connection.getPort());
    }
}