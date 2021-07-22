package Dstore;

import java.util.ArrayList;

import Logger.*;
import Protocol.Exception.*;
import Protocol.Token.*;
import Protocol.Token.TokenType.*;
import Network.*;
import Network.Protocol.Exception.MessageSendException;
import Network.Protocol.Exception.RequestHandlingException;
import Network.Server.RequestHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

/**
 * Handles requests sent to a Dstore by a DSClient and Controller.
 */
public class DstoreRequestHandler implements RequestHandler{
    
    // member variables
    private Dstore dstore;

    /**
     * Class constructor.
     * 
     * @param dstore The Dstore associated with the request handler.
     */
    public DstoreRequestHandler(Dstore dstore){
        // initializing member variables
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
    public void handleRequest(Connection connection, Token request){
        try{
            // STORE //
            if(request instanceof StoreToken){
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
                    connection.sendMessage(Protocol.ERROR_FILE_DOES_NOT_EXIST_TOKEN + " " + token.filename);                
                }
            }
            catch(MessageSendException ex){
                this.dstore.handleError(ex);
            }
        }
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
        connection.sendMessage(Protocol.ACK_TOKEN);

        // reading file data
        byte[] fileContent = connection.getNBytesWithinTimeout(filesize, this.dstore.getTimeout());

        // storing file data
        File file = new File(this.dstore.getFolderPath() + File.separatorChar + filename);
        FileOutputStream fileOutput = new FileOutputStream(file);
        fileOutput.write(fileContent);
        fileOutput.flush();
        fileOutput.close();

        // sending STORE_ACK to contoller
        this.dstore.getControllerThread().getConnection().sendMessage(Protocol.STORE_ACK_TOKEN + " " + filename);
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
        connection.sendMessage(Protocol.REMOVE_ACK_TOKEN + " " + filename);
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
        // gathering list of files
        File[] fileList = this.dstore.getFileStore().listFiles();

        // creating message elements
        ArrayList<String> messageElements = new ArrayList<String>();
        messageElements.add("LIST");

        for(File file : fileList){
            messageElements.add(file.getName());
        }

        // creating message
        String message = String.join(" ", messageElements);

        // sending the list of files back to the connector
        connection.sendMessage(message);
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
