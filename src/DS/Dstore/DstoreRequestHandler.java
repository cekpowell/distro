package DS.Dstore;

import java.util.ArrayList;
import java.util.HashMap;

import DS.Protocol.Protocol;
import DS.Protocol.Event.Operation.ListCompleteEvent;
import DS.Protocol.Event.Operation.LoadCompleteEvent;
import DS.Protocol.Event.Operation.RemoveCompleteEvent;
import DS.Protocol.Event.Operation.StoreCompleteEvent;
import DS.Protocol.Event.Rebalance.RebalanceCompleteEvent;
import DS.Protocol.Event.Rebalance.RebalanceStoreCompleteEvent;
import DS.Protocol.Exception.*;
import DS.Protocol.Token.*;
import DS.Protocol.Token.TokenType.*;
import Network.*;
import Network.Client.Client.ClientType;
import Network.Protocol.Event.ServerConnectionEvent;
import Network.Protocol.Exception.MessageSendException;
import Network.Protocol.Exception.RequestHandlingException;
import Network.Server.RequestHandler;
import Network.Server.Server.ServerType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

/**
 * Handles requests sent to a Dstore by a DSClient and Controller.
 */
public class DstoreRequestHandler extends RequestHandler{
    
    // member variables
    private Dstore dstore;

    /**
     * Class constructor.
     * 
     * @param dstore The Dstore associated with the request handler.
     */
    public DstoreRequestHandler(Dstore dstore){
        // initializing
        super(dstore);
        this.dstore = dstore;
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
            // JOIN_CLIENT //
            if(request instanceof JoinClientToken){
                this.handleJoinClientRequest(connection);
            }

            // JOIN_DSTORE //
            else if(request instanceof JoinDstoreToken){
                this.handleJoinDstoreRequest(connection);
            }

            // STORE //
            else if(request instanceof StoreToken){
                StoreToken storeToken = (StoreToken) request;
                this.handleStoreRequest(connection, storeToken.filename, storeToken.filesize);
            }

            // LOAD_DATA //
            else if(request instanceof LoadDataToken){
                LoadDataToken loadToken = (LoadDataToken) request;
                this.handleLoadDataRequest(connection, loadToken.filename);
            }

            // REMOVE //
            else if(request instanceof RemoveToken){
                RemoveToken removeToken = (RemoveToken) request;
                this.handleRemoveRequest(connection, removeToken.filename);
            }

            // LIST //
            else if(request instanceof ListToken){
                this.handleListRequest(connection);
            }

            // REBALANCE //
            else if(request instanceof RebalanceToken){
                RebalanceToken rebalanceToken = (RebalanceToken) request;
                this.handleRebalanceRequest(connection, rebalanceToken.filesToSend, rebalanceToken.filesToRemove);
            }

            // REBALANCE_STORE //
            else if(request instanceof RebalanceStoreToken){
                RebalanceStoreToken rebalanceStoreToken = (RebalanceStoreToken) request;
                this.handleRebalanceStoreRequest(connection, rebalanceStoreToken.filename, rebalanceStoreToken.filesize);
            }

            // Invalid //
            else{
                this.handleInvalidRequest(connection, request);
            }
        }
        catch(Exception e){
            // loggiing error
            this.dstore.handleError(new RequestHandlingException(request.message, e));

            // Handling Specific Cases //

            try{
                // No Such File Exception //
                if(e instanceof NoSuchFileException){
                    RemoveToken token = (RemoveToken) request;
    
                    // sending error to controller
                    connection.sendMessage(Protocol.getErrorFileDoesNotExistMessage(token.filename));
                }
            }
            catch(MessageSendException ex){
                this.dstore.handleError(ex);
            }
        }
    }

    /////////////////
    // JOIN_CLIENT //
    /////////////////

    /**
     * Handles a JOIN_CLIENT request.
     * 
     * @param connection The connection associcated with the request.
     * @throws MessageSendException If a message couldn't be sent through the connection.
     */
    private void handleJoinClientRequest(Connection connection) throws Exception{
        // adding the connection to the server
        this.dstore.getClientConnections().add(connection);

        // logging
        this.dstore.handleEvent(new ServerConnectionEvent(ClientType.CLIENT, connection.getPort()));

        // sending join ack back to client
        connection.sendMessage(Protocol.getJoinAckMessage());
    }

    /////////////////
    // JOIN_DSTORE //
    /////////////////

    private void handleJoinDstoreRequest(Connection connection) throws Exception{
        // adding the connection to the server
        this.dstore.getServerConnections().add(connection);

        // logging
        this.dstore.handleEvent(new ServerConnectionEvent(ClientType.DSTORE, connection.getPort()));

        // sending the join ack back to the Dstore
        connection.sendMessage(Protocol.getJoinAckMessage());
    }

    ///////////
    // STORE //
    ///////////

    /**
     * Handles a STORE request.
     * 
     * @param connection The connection associated with the request.
     * @param filename The name of the file being stored.
     * @param filesize The size of the file being stored.
     * @throws MessageSendException If a message couldn't be sent through the connection.
     * @throws MessageReceievedException If a message could not be receieved through the connection.
     */
    private void handleStoreRequest(Connection connection, String filename, int filesize) throws Exception{
        // sending ACK back to client
        connection.sendMessage(Protocol.getAckMessage());

        // reading file data
        byte[] fileContent = connection.getNBytesWithinTimeout(filesize, this.dstore.getTimeout());

        // storing file data
        File file = new File(this.dstore.getFolderPath() + File.separatorChar + filename);
        FileOutputStream fileOutput = new FileOutputStream(file);
        fileOutput.write(fileContent);
        fileOutput.flush();
        fileOutput.close();

        // sending STORE_ACK to contoller
        this.dstore.getControllerThread().getConnection().sendMessage(Protocol.getStoreAckMessage(filename));

        // logging
        this.dstore.handleEvent(new StoreCompleteEvent(filename, filesize));
    }

    ////////// 
    // LOAD //
    //////////

    /**
     * Handles a LOAD request. 
     * 
     * @param connection The connection associated with the request.
     * @param filename The name of the file being loaded.
     * @throws MessageSendException If a message couldn't be sent through the connection.
     * @throws FileDoesNotExistException If the file being requested does not exist.
     */
    private void handleLoadDataRequest(Connection connection, String filename) throws Exception{
        // getting file
        File file = new File(this.dstore.getFolderPath() + File.separatorChar + filename);

        // file exists - sending file to client
        if(file.exists()){
            // gathering file
            FileInputStream fileInput = new FileInputStream(file);

            // sending file to client
            byte[] fileContent = fileInput.readAllBytes();
            connection.sendBytes(fileContent);
            fileInput.close();

            // logging
            this.dstore.handleEvent(new LoadCompleteEvent(filename));
        }
        // file does not exist - closing connection
        else{
            connection.close();

            // throwing exception
            throw new FileDoesNotExistException(filename);
        }
    }

    ////////////
    // REMOVE //
    ////////////

    /**
     * Handles a REMOVE request.
     * 
     * @param connection The connection associated with the request.
     * @param filename The name of the file being removed.
     * @throws MessageSendException If a message couldn't be sent through the connection.
     * @throws FileDoesNotExistException If the file being requested does not exist.
     */
    private void handleRemoveRequest(Connection connection, String filename) throws Exception{
        // creating file object
        File file = new File(this.dstore.getFolderPath() + File.separatorChar + filename);

        // deleting file
        try{
            Files.delete(Paths.get(file.getAbsolutePath()));
        }
        catch(Exception e){
            throw new FileDoesNotExistException(filename);
        }

        // sending acknowleddgement to controller
        connection.sendMessage(Protocol.getRemoveAckMessage(filename));

        // logging
        this.dstore.handleEvent(new RemoveCompleteEvent(filename));
    }

    //////////
    // LIST //
    //////////

    /**
     * Handles a LIST request.
     * 
     * @param connection The connection associated with the request.
     * @throws MessageSendException If a message couldn't be sent through the connection.
     */
    private void handleListRequest(Connection connection) throws Exception{
        // creating hashmap of files
        HashMap<String, Integer> files = this.dstore.getFiles();

        // creating message
        String message = Protocol.getListOfFilesMessage(files);

        // sending the list of files back to the connector
        connection.sendMessage(message);

        // logging
        this.dstore.handleEvent(new ListCompleteEvent());
    }

    ///////////////
    // REBALANCE //
    ///////////////

    /**
     * Handles a REBALANCE request.
     * 
     * @param connection The connection associated with the request.
     * @param filesToSend The files that must be sent to other Dstores.
     * @param filesToRemove The files to be removed from the Dstore.
     * @throws MessageSendException If a message could not be sent through a connection.
     * @throws MessageRecievedException If a message could not be receieved from a connection.
     * @throws FileDoesNotExistException If a file referenced in the request does not exist.
     * @throws InvalidMessageException If an invalid message is receieved from a Dstore whilst
     * sending files.
     */
    private void handleRebalanceRequest(Connection connection, ArrayList<FileToSend> filesToSend, ArrayList<String> filesToRemove) throws Exception{
        // FILES TO SEND //

        // iterate over files
        for(FileToSend fileToSend : filesToSend){
            // forming REBALANCE_STORE message
            String message = Protocol.getRebalanceStoreMessage(fileToSend.filename, fileToSend.filesize);

            // loading file to be sent
            File file = new File(this.dstore.getFolderPath() + File.separatorChar + fileToSend.filename);

            // file exists - sending file to dstore's that need it
            if(file.exists()){
                // gathering file
                FileInputStream fileInput = new FileInputStream(file);

                // iterating over Dstores to send to
                for(int dstore : fileToSend.dStores){
                    // setting up the connection
                    Connection dstoreConnection = new Connection(this.dstore.getNetworkInterface(), dstore, ServerType.DSTORE);

                    // adding connection to server
                    this.dstore.getServerConnections().add(dstoreConnection);

                    // sending dstore join message
                    dstoreConnection.sendMessage(Protocol.getJoinDstoreMessage(this.dstore.getPort()));

                    try{
                        // wait for acknowledgement
                        Token response = RequestTokenizer.getToken(dstoreConnection.getMessageWithinTimeout(this.dstore.getTimeout()));

                        // making sure response is JOIN_ACK
                        if(response instanceof JoinAckToken){
                            // sending rebalance message
                            dstoreConnection.sendMessage(message);

                            try{
                                // waiting for acknowledgement
                                response = RequestTokenizer.getToken(dstoreConnection.getMessageWithinTimeout(this.dstore.getTimeout()));
                    
                                // making sure acknowledgement was receieved
                                if(response instanceof AckToken){
                                    // sending file to client
                                    byte[] fileContent = fileInput.readAllBytes();
                                    dstoreConnection.sendBytes(fileContent);
                                    fileInput.close();
                
                                    // closing streams
                                    dstoreConnection.close();
                                    fileInput.close();
                                }
                                // invalid response received
                                else{
                                    // closing streams
                                    dstoreConnection.close();
                                    fileInput.close();
                
                                    // throwing exception
                                    throw new InvalidMessageException(response.message, dstoreConnection.getPort());
                                }
                            }
                            catch(Exception e){
                                // closing streams
                                dstoreConnection.close();
                                fileInput.close();
                    
                                // throwing exception
                                throw e;
                            }
                        }
                        else{
                            // closing streams
                            dstoreConnection.close();
                            fileInput.close();
        
                            // throwing exception
                            throw new InvalidMessageException(response.message, dstoreConnection.getPort());
                        }
                    }
                    catch(Exception e){
                        // closing streams
                        dstoreConnection.close();
                        fileInput.close();
            
                        // throwing exception
                        throw e;
                    }
                }
            }
            // file does not exist - throwing exception
            else{
                // throwing exception
                throw new FileDoesNotExistException(fileToSend.filename);
            }
        }

        // FILES TO REMOVE //

        for(String fileToRemove : filesToRemove){
            // creating file object
            File file = new File(this.dstore.getFolderPath() + File.separatorChar + fileToRemove);

            // deleting file
            try{
                Files.delete(Paths.get(file.getAbsolutePath()));
            }
            catch(Exception e){
                throw new FileDoesNotExistException(fileToRemove);
            }
        }

        // REBALANCE COMPLETE //

        // creating hashmap of files
        HashMap<String, Integer> files = this.dstore.getFiles();

        // sending message to controller
        connection.sendMessage(Protocol.getRebalanceCompleteMessage(files));

        // logging
        this.dstore.handleEvent(new RebalanceCompleteEvent());
    }

    /////////////////////
    // REBALANCE STORE //
    /////////////////////

    /**
     * Handles a REBALANCE_STORE request.
     * 
     * @param connection The connection associated with the request.
     * @param filename The name of the file being sent.
     * @param filesize The size of the file being sent.
     */
    private void handleRebalanceStoreRequest(Connection connection, String filename, int filesize) throws Exception{
        // sending ACK back to dstore
        connection.sendMessage(Protocol.getAckMessage());

        // reading file data
        byte[] fileContent = connection.getNBytesWithinTimeout(filesize, this.dstore.getTimeout());

        // storing file data
        File file = new File(this.dstore.getFolderPath() + File.separatorChar + filename);
        FileOutputStream fileOutput = new FileOutputStream(file);
        fileOutput.write(fileContent);
        fileOutput.flush();
        fileOutput.close();

        // logging
        this.dstore.handleEvent(new RebalanceStoreCompleteEvent(filename, filesize));
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
