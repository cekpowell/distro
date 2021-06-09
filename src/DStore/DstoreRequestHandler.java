package Dstore;

import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import Logger.Protocol;
import Network.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

import Token.*;
import Token.TokenType.*;

/**
 * Handles requests sent to a Dstore by a DSClient and Controller.
 */
public class DstoreRequestHandler implements RequestHandler{
    
    // member variables
    private Dstore dstore;

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
     * @param request Tokenized request to be handled.
     */
    public void handleRequest(Connection connection, Token request){

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
            this.handleInvalidRequest(connection);
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
     */
    private void handleStoreRequest(Connection connection, String filename, int filesize){
        try{
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
        catch(TimeoutException e){
            this.dstore.getServerInterface().handleError("Timeout occured on STORE request sent by Client on port : " + connection.getSocket().getPort());
        }
        catch(Exception e){
            this.dstore.getServerInterface().handleError("Unable to handle STORE request sent by Client on port : " + connection.getSocket().getPort());
        }
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
    private void handleLoadDataRequest(Connection connection, String filename){
        try{
            // getting file
            File file = new File(this.dstore.getFolderPath() + File.separatorChar + filename);

            // file exists
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
            }
        }
        catch(Exception e){
            this.dstore.getServerInterface().handleError("Unable to handle LOAD request sent by Client on port : " + connection.getSocket().getPort());
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
     */
    private void handleRemoveRequest(Connection connection, String filename){
        try{
            // creating file object
            File file = new File(this.dstore.getFolderPath() + File.separatorChar + filename);

            // deleting file
            Files.delete(Paths.get(file.getAbsolutePath()));

            // sending acknowleddgement to controller
            connection.sendMessage(Protocol.REMOVE_ACK_TOKEN + " " + filename);
        }
        catch(NoSuchFileException e){
            // logging error
            this.dstore.getServerInterface().handleError("Unable to handle REMOVE sent by Controller on port : " + this.dstore.getCPort() + " as the file does not exist.");

            try{
                // sending error to controller
                connection.sendMessage(Protocol.ERROR_FILE_DOES_NOT_EXIST_TOKEN + " " + filename);
            }
            catch(Exception ex){
                this.dstore.getServerInterface().handleError("Unable to send error message to controller on port : " + this.dstore.getCPort());
            }
        }
        catch(Exception e){
            this.dstore.getServerInterface().handleError("Unable to handle REMOVE request sent by Controller on port : " + this.dstore.getCPort());
        }
    }

    //////////
    // LIST //
    //////////

    /**
     * Handles a LIST request.
     * 
     * @param connection The connection associated with the request.
     */
    private void handleListRequest(Connection connection){
        try{
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
        catch(Exception e){
            //TODO need to test for different types of exception to know where the error occuredd - e.g., SocketTimeoutException, NullPointerException, etc...
            this.dstore.getServerInterface().handleError("Unable to handle LIST request from Controller on port : " + this.dstore.getCPort());
        }
    }

    /////////////
    // INVALID //
    /////////////

    /**
     * Handles an invalid request.
     */
    public void handleInvalidRequest(Connection connection){
        this.dstore.getServerInterface().handleError("Invalid request recieved from connector on port : " + connection.getSocket().getPort());
    }
}
