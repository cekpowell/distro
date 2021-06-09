package DSClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

import Interface.NetworkInterface;
import Logger.Protocol;
import Network.*;
import Token.*;
import Token.TokenType.AckToken;
import Token.TokenType.ErrorFileAlreadyExistsToken;
import Token.TokenType.ErrorFileDoesNotExistToken;
import Token.TokenType.ErrorLoadToken;
import Token.TokenType.ErrorNotEnoughDStoresToken;
import Token.TokenType.ListToken;
import Token.TokenType.LoadFromToken;
import Token.TokenType.LoadToken;
import Token.TokenType.RemoveCompleteToken;
import Token.TokenType.RemoveToken;
import Token.TokenType.StoreCompleteToken;
import Token.TokenType.StoreToToken;
import Token.TokenType.StoreToken;


/**
 * Abstract class to represent a DSClient within the system.
 * 
 * A DSClient connects to a Data Store by connecting to a Controller.
 * 
 * The DSClient recieves input requests from the associated DSClientInterface
 * into the 'handleInputRequest' method, where it handles the request appropriatley.
 * 
 * Handling the request could involve just sending the message to the Controller and 
 * recieving the response (e.g., LIST), or could involve more in that the Client needs
 * to send further messages (e.g., STORE).
 * 
 * The class is essentially a request handler for the DSClient.
 */
public class DSClient extends Client{

    /**
     * Class Constructor.
     * 
     * @param cPort The port of the Controller.
     * @param timeout The message timeout period.
     */
    public DSClient(int cPort, int timeout, NetworkInterface clientInterface) {
        // initialising member variables
        super(cPort, timeout, clientInterface);
    }

    //////////
    // MAIN //
    //////////

    /**
     * Handles the given response.
     * 
     * @param connection The socket the response was recieved from.
     * @param message The recieved response.
     */
    public void handleInputRequest(String request){
        try{
            Token requestToken = RequestTokenizer.getToken(request);

            // STORE //
            if(requestToken instanceof StoreToken){
                StoreToken storeToken = (StoreToken) requestToken;
                this.handleStoreRequest(storeToken.filename, storeToken.filesize);
            }

            // LOAD //
            else if(requestToken instanceof LoadToken){
                LoadToken loadToken = (LoadToken) requestToken;
                this.handleLoadRequest(loadToken.filename, false);
            }

            // REMOVE //
            else if(requestToken instanceof RemoveToken){
                RemoveToken removeToken = (RemoveToken) requestToken;
                this.handleRemoveRequest(removeToken.filename);
            }

            // LIST //
            else if(requestToken instanceof ListToken){
                this.handleListRequest();
            }

            // Invalid Request
            else{
                this.handleInvalidRequest(request);
            }
        }
        catch(Exception e){
            // unable to handle input request
            this.getClientInterface().handleError("Unable to handle input request : " + request + " sent to Controller on port : " + this.getCPort());
        }
    }

    ///////////
    // STORE //
    ///////////

    /**
     * Handles a STORE request.
     * 
     * @param filename
     * @param filesize
     */
    private void handleStoreRequest(String filename, int filesize) throws Exception{
        // sending the store message to the controller
        this.getServerConnection().sendMessage(Protocol.STORE_TOKEN + " " + filename + " " + filesize);

        // gathering response
        Token response = RequestTokenizer.getToken(this.getServerConnection().getMessageWithinTimeout(this.getTimeout()));
        
        // STORE_TO
        if(response instanceof StoreToToken){
            // gathering the token
            StoreToToken storeToToken = (StoreToToken) response;
            
            // sending file to each dstore
            for(int dstore : storeToToken.ports){
                this.sendFileToDstore(filename, filesize, dstore);
            }

            // waiting for response from Controller
            response = RequestTokenizer.getToken(this.getServerConnection().getMessageWithinTimeout(this.getTimeout()));

            // STORE_COMPLETE
            if(response instanceof StoreCompleteToken){
                // nothing to do... as the store is complete
            }

            // Invalid Response
            else{
                this.getClientInterface().handleError("Invalid response receieved from controller on port : " + this.getCPort());
            }
        }

        // ERROR_NOT_ENOUGH_DSTORES
        else if(response instanceof ErrorNotEnoughDStoresToken){
            this.getClientInterface().handleError("The file '" + filename + "' could not be stored as there are not enough Dstores connected.");
        }

        // ERROR_FILE_ALREADY_EXISTS
        else if(response instanceof ErrorFileAlreadyExistsToken){
            this.getClientInterface().handleError("The file '" + filename + "' could not be stored as it already exists.");
        }

        // Invalid Response
        else{
            this.getClientInterface().handleError("Invalid response receieved from controller on port : " + this.getCPort());
        }
    }

    /**
     * Sends the file with the given name to the Dstore listening on the provided port.
     * 
     * @param filename The name of the file to be send to the Sstore.
     * @param dstore The Dstore the file is being send to.
     */
    private void sendFileToDstore(String filename, int filesize, int dstore) throws Exception{
        // loading the file
        File file = new File(filename);
        FileInputStream fileInput = new FileInputStream(file);

        // setting up the connection
        Connection connection = new Connection(this.getClientInterface(), InetAddress.getLocalHost(), dstore);

        // sending initial message
        connection.sendMessage(Protocol.STORE_TOKEN + " " + filename + " " + filesize);

        try{
            // waiting for acknowledgement
            Token response = RequestTokenizer.getToken(connection.getMessageWithinTimeout(this.getTimeout()));

            // making sure acknowledgement was receieved
            if(response instanceof AckToken){
                // sending the file to the dstore
                byte[] fileContent = fileInput.readNBytes(filesize);
                connection.sendBytes(fileContent);

                // closing streams
                connection.close();
                fileInput.close();
            }
            // invalid response received
            else{
                // closing streams
                connection.close();
                fileInput.close();

                // throwing exception
                throw new Exception();
            }
        }
        catch(SocketTimeoutException e){
            connection.close();
            throw new SocketTimeoutException();
        }
    }

    //////////
    // LOAD //
    //////////


    /**
     * Handles a LOAD request.
     * 
     * @param filename The name of the file being removed.
     * @param isReload Determines if this LOAD operation is a Reload or not.
     */
    private void handleLoadRequest(String filename, boolean isReload) throws Exception{
        // gathering the protocol command
        String command = "";
        if(!isReload){
            command = Protocol.LOAD_TOKEN;
        }
        else{
            command = Protocol.RELOAD_TOKEN;
        }

        // sending LOAD message to controller
        this.getServerConnection().sendMessage(command+ " " + filename);

        // gathering response
        Token response = RequestTokenizer.getToken(this.getServerConnection().getMessageWithinTimeout(this.getTimeout()));
        
        // LOAD_FROM
        if(response instanceof LoadFromToken){
            // gathering the token
            LoadFromToken loadFromToken = (LoadFromToken) response;

            // LOADING FILE
            try{
                // loading file from Dstore
                byte[] fileContent = this.loadFileFromDstore(loadFromToken.port, filename, loadFromToken.filesize);

                // storing the file
                File file = new File(filename);
                FileOutputStream fileOutput = new FileOutputStream(file);
                fileOutput.write(fileContent);
                fileOutput.flush();
                fileOutput.close();
            }
            catch(SocketTimeoutException e){
                // logging error
                this.getClientInterface().handleError("The file '" + filename + "' could not be loaded as the Dstore on port : " + loadFromToken.port +  " timed-out.");

                // reloading the file
                this.handleLoadRequest(filename, true);
            }
            // unable to store file
            catch(IOException e){
                // logging error
                this.getClientInterface().handleError("The file '" + filename + "' could not be loaded due to an IOException.");
            }
            // unable to load file content
            catch(Exception e){
                // Logging error
                this.getClientInterface().handleError("The file content for file '" + filename +  "' could not be loaded from Dstore on port : " + loadFromToken.port);

                // reloading if data could not be gathered
                this.handleLoadRequest(filename, true);
            }
        }

        // ERROR_NOT_ENOUGH_DSTORES
        else if(response instanceof ErrorNotEnoughDStoresToken){
            this.getClientInterface().handleError("The file '" + filename + "' could not be loaded as there are not enough Dstores connected.");
        }

        // ERROR_FILE_DOES_NOT_EXIST
        else if(response instanceof ErrorFileDoesNotExistToken){
            this.getClientInterface().handleError("The file '" + filename + "' could not be loaded as it does not exist.");
        }

        // ERROR_LOAD
        else if(response instanceof ErrorLoadToken){
            this.getClientInterface().handleError("The file '" + filename + "' could not be loaded as no Dstores are available.");
        }

        // Invalid Response
        else{
            this.getClientInterface().handleError("Invalid response receieved from controller on port : " + this.getCPort() + response.message);
        }
    }

    /**
     * Loads a given file from the provided Dstore.
     * 
     * @param port
     * @param filename
     * @param filesize
     * @return The file loaded from the Dstore
     */
    private byte[] loadFileFromDstore(int port, String filename, int filesize) throws Exception{
        // setting up the connection
        Connection connection = new Connection(this.getClientInterface(), InetAddress.getLocalHost(), port);

        // sending LOAD_DATA message
        connection.sendMessage(Protocol.LOAD_DATA_TOKEN + " " + filename);

        try{
            // reading file data
            byte[] fileContent = connection.getNBytesWithinTimeout(filesize, this.getTimeout());

            // closing connection
            connection.close();

            return fileContent;
        }
        catch(Exception e){
            connection.close();
            throw e;
        }
    }


    ////////////
    // REMOVE //
    ////////////


    /**
     * Handles a REMOVE request.
     * 
     * @param filename The name of the file being removed.
     */
    private void handleRemoveRequest(String filename) throws Exception{
        // sending remove to controller
        this.getServerConnection().sendMessage(Protocol.REMOVE_TOKEN + " " + filename);

        // gathering response
        Token response = RequestTokenizer.getToken(this.getServerConnection().getMessageWithinTimeout(this.getTimeout()));

        // LOAD_COMPLETE
        if(response instanceof RemoveCompleteToken){
            // nothing to do ...
        }

        // ERROR_NOT_ENOUGH_DSTORES
        else if(response instanceof ErrorNotEnoughDStoresToken){
            this.getClientInterface().handleError("The file '" + filename + "' could not be removed as there are not enough Dstores connected.");
        }

        // ERROR_FILE_DOES_NOT_EXIST
        else if(response instanceof ErrorFileDoesNotExistToken){
            this.getClientInterface().handleError("The file '" + filename + "' could not be removed as it does not exist.");
        }

        // Invalid Response
        else{
            this.getClientInterface().handleError("Invalid response receieved from controller on port : " + this.getCPort());
        }
    }


    //////////
    // LIST //
    //////////

    /**
     * Handles a LIST request.
     * 
     * @throws Exception Thrown if the request could not be handled (could not send request or recieve response).
     */
    private void handleListRequest() throws Exception{
        // sending message to Controller
        this.getServerConnection().sendMessage(Protocol.LIST_TOKEN);

        // gathering response (dont need to do anything with it)
        this.getServerConnection().getMessageWithinTimeout(this.getTimeout());
    }

    /////////////
    // INVALID //
    /////////////

    /**
     * Handles an invalid request.
     * 
     * @param request Handles an invalid request.
     * @throws Exception Thrown if the request could not be handled (could not send request or recieve response).
     */
    private void handleInvalidRequest(String request) throws Exception{
        // sending message to Controller
        this.getServerConnection().sendMessage(request);

        // gathering response
        this.getServerConnection().getMessageWithinTimeout(this.getTimeout());
    }
}