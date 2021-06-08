package DSClient;

import java.io.File;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeoutException;

import Interface.NetworkInterface;
import Logger.Protocol;
import Network.*;
import Token.*;
import Token.TokenType.AckToken;
import Token.TokenType.ListToken;
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

            // List Request //
            if(requestToken instanceof ListToken){
                this.handleListRequest();
            }

            // Store request //
            else if(requestToken instanceof StoreToken){
                StoreToken storeToken = (StoreToken) requestToken;
                this.handleStoreRequest(storeToken.filename, storeToken.filesize);
            }

            // invalid request or request not expected by Client
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
        
        // checking response is of correct form
        if(response instanceof StoreToToken){
            StoreToToken storeToToken = (StoreToToken) response;
            
            // sending file to each dstore
            for(int dstore : storeToToken.ports){
                this.sendFileToDstore(filename, filesize, dstore);
            }

            // waiting for STORE_COMPLETE from controller
            response = RequestTokenizer.getToken(this.getServerConnection().getMessageWithinTimeout(this.getTimeout()));

            // checking response is of correct form
            if(response instanceof StoreCompleteToken){
                // nothing to do...
            }

            // invalid response receieved
            else{
                this.getClientInterface().handleError("Invalid response receieved from controller on port : " + this.getCPort());
            }
        }

        // invalid response receieved
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

                // closing the connection
                connection.close();
                fileInput.close();
            }
            // invalid response received
            else{
                // closing the connection
                connection.close();
                fileInput.close();

                // throwing exception
                throw new Exception();
            }
        }
        catch(SocketTimeoutException e){
            connection.close();
            throw new TimeoutException();
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