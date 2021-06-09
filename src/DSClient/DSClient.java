package DSClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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

    ///////////
    // STORE //
    ///////////

    /**
     * Handles a STORE request.
     * 
     * @param filename
     * @param filesize
     */
    private void handleStoreRequest(String filename, int filesize){
        try{
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
                    throw new Exception("Invalid response receieved from controller on port : " + this.getCPort());
                }
            }

            // ERROR_NOT_ENOUGH_DSTORES
            else if(response instanceof ErrorNotEnoughDStoresToken){
                throw new Exception("Not enough Dstores are connected to the Controller");
            }

            // ERROR_FILE_ALREADY_EXISTS
            else if(response instanceof ErrorFileAlreadyExistsToken){
                throw new Exception("A file with the same name already exists within the system");
            }

            // Invalid Response
            else{
                throw new Exception("Invalid response receieved from controller on port : " + this.getCPort());            
            }
        }
        catch(Exception e){
            // logging error
            this.getClientInterface().handleError("Unable to handle STORE request for file '" + filename + "'", e);
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
                throw new Exception("Invalid response receieved from Dstore on port : " + connection.getPort());
            }
        }
        catch(Exception e){
            // closing connection
            connection.close();

            // throwing exception
            throw e;
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
    private void handleLoadRequest(String filename, boolean isReload){
        try{
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
                // unable to load file content
                catch(Exception e){
                    // Logging error
                    this.getClientInterface().handleError("The file content for file '" + filename +  "' could not be loaded from Dstore on port : " + loadFromToken.port, e);

                    // reloading if data could not be gathered
                    this.handleLoadRequest(filename, true);
                }
            }

            // ERROR_NOT_ENOUGH_DSTORES
            else if(response instanceof ErrorNotEnoughDStoresToken){
                throw new Exception("Not enough Dstores are connected to the Controller");
            }

            // ERROR_FILE_DOES_NOT_EXIST
            else if(response instanceof ErrorFileDoesNotExistToken){
                throw new Exception("No file with this name is registered in the system");
            }

            // ERROR_LOAD
            else if(response instanceof ErrorLoadToken){
                throw new Exception("All possible Dstores have been attempted and have failed");
            }

            // Invalid Response
            else{
                throw new Exception("Invalid response receieved from controller on port : " + this.getCPort() + response.message);
            }
        }
        catch(Exception e){
            // Logging error
            this.getClientInterface().handleError("Unable to handle LOAD request for file '" + filename + "'", e);
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
            // closing connection
            connection.close();

            // throwing exception
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
    private void handleRemoveRequest(String filename){
        try{
            // sending remove to controller
            this.getServerConnection().sendMessage(Protocol.REMOVE_TOKEN + " " + filename);

            // gathering response
            Token response = RequestTokenizer.getToken(this.getServerConnection().getMessageWithinTimeout(this.getTimeout()));

            // LOAD_COMPLETE
            if(response instanceof RemoveCompleteToken){
                // Nothing to do...
            }

            // ERROR_NOT_ENOUGH_DSTORES
            else if(response instanceof ErrorNotEnoughDStoresToken){
                throw new Exception("Not enough Dstores are connected to the Controller");
            }

            // ERROR_FILE_DOES_NOT_EXIST
            else if(response instanceof ErrorFileDoesNotExistToken){
                throw new Exception("No file with this name is registered in the system");
            }

            // Invalid Response
            else{
                throw new Exception("Invalid response receieved from controller on port : " + this.getCPort());
            }
        }
        catch(Exception e){
            // Logging error
            this.getClientInterface().handleError("Unable to handle REMOVE request for file '" + filename + "'" , e);
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
    private void handleListRequest(){
        try{
            // sending message to Controller
            this.getServerConnection().sendMessage(Protocol.LIST_TOKEN);

            // gathering response (dont need to do anything with it)
            this.getServerConnection().getMessageWithinTimeout(this.getTimeout());
        }
        catch(Exception e){
            // Logging error
            this.getClientInterface().handleError("Unable to handle LIST request", e);
        }
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
    private void handleInvalidRequest(String request){
        try{
            // sending message to Controller
            this.getServerConnection().sendMessage(request);

            // gathering response
            this.getServerConnection().getMessageWithinTimeout(this.getTimeout());
        }
        catch(Exception e){
            // Logging error
            this.getClientInterface().handleError("Unable to handle request '" + request + "'", e);
        }
    }
}