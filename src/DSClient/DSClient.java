package DSClient;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

import Logger.*;
import Network.*;
import Network.Client.Client;
import Network.Protocol.Exception.ClientSetupException;
import Network.Protocol.Exception.ConnectionTerminatedException;
import Network.Protocol.Exception.HandeledNetworkException;
import Network.Protocol.Exception.MessageSendException;
import Network.Protocol.Exception.NetworkException;
import Protocol.Exception.*;
import Protocol.Token.*;
import Protocol.Token.TokenType.*;

/**
 * Abstract class to represent a DSClient within the system.
 * 
 * A DSClient connects to a Data Store by connecting to a Controller.
 * 
 * The DSClient recieves input requests from the associated DSClientInterface
 * into the 'handleInputRequest' method.
 * 
 * The class is essentially a request handler for the DSClient.
 */
public class DSClient extends Client{

    /**
     * Class Constructor.
     * 
     * @param cPort The port of the Controller.
     * @param timeout The message timeout period.
     * @param networkInterface The interface component for the Client.
     */
    public DSClient(int cPort, int timeout, NetworkInterface networkInterface) {
        // initialising member variables
        super(cPort, timeout, networkInterface);
    }

    ///////////
    // SETUP //
    ///////////

    /**
     * Sets up the DSClient ready for use.
     * 
     * @throws ClientStartException If the Client could not be setup.
     */
    public void setup() throws ClientSetupException{
        try{
            // sending JOIN_CLIENT message to controller
            this.getServerConnection().sendMessage(Protocol.JOIN_CLIENT_TOKEN);
        }
        catch(MessageSendException e){
            throw new ClientSetupException(e);
        }
    }

    ////////////////////
    // ERROR HANDLING //
    ////////////////////

    /**
     * Handles an error that has occured for the client
     * 
     * @param error The error that has occured.
     */
    public void handleError(NetworkException error){
        // Connection Termination
        if(error instanceof ConnectionTerminatedException){
            ConnectionTerminatedException exception = (ConnectionTerminatedException) error;

            // Controller Disconnected
            if(exception.getConnection().getPort() == this.getServerPort()){
                // logging error
                this.getClientInterface().logError(new HandeledNetworkException(new ControllerDisconnectException(this.getServerPort(), exception)));
            }
            // Dstore disconnected
            else{
                // logging error
                this.getClientInterface().logError(new HandeledNetworkException(new DstoreDisconnectException(exception.getConnection().getPort(), exception)));
            }
        }
        // Non-important error - just need to log
        else{
            // logging error
            this.getClientInterface().logError(new HandeledNetworkException(error));
        }
    }

    //////////////////////
    // REQUEST HANDLING //
    //////////////////////

    ///////////
    // STORE //
    ///////////

    /**
     * Handles a STORE request.
     * 
     * @param file The file object to be stored.
     * @param filesize The size of the file being stored.
     * 
     * @throws MessageSendException If a message couldn't be sent through the connection.
     * @throws MessageReceievedException If a message could not be receieved through the connection.
     * @throws NotEnoughDstoresException If there are not enough Dstores connected to the controller to handle 
     * the request.
     * @throws FileAlreadyExistsException If there is already a file with this name stored in the Dstore.
     * @throws InvalidMessageException If a message of the wrong form is receieved during the communication.
     */
    public void storeFile(File file, int filesize) throws Exception{
        // sending the store message to the controller
        this.getServerConnection().sendMessage(Protocol.STORE_TOKEN + " " + file.getName() + " " + filesize);

        // gathering response
        Token response = RequestTokenizer.getToken(this.getServerConnection().getMessageWithinTimeout(this.getTimeout()));
        
        // STORE_TO
        if(response instanceof StoreToToken){
            // gathering the token
            StoreToToken storeToToken = (StoreToToken) response;
            
            // sending file to each dstore
            for(int dstore : storeToToken.ports){
                this.sendFileToDstore(file, filesize, dstore);
            }

            // waiting for response from Controller
            response = RequestTokenizer.getToken(this.getServerConnection().getMessageWithinTimeout(this.getTimeout()));

            // STORE_COMPLETE
            if(response instanceof StoreCompleteToken){
                // nothing to do... as the store is complete
            }

            // Invalid Response
            else{
                throw new InvalidMessageException(response.message, this.getServerPort());
            }
        }

        // ERROR_NOT_ENOUGH_DSTORES
        else if(response instanceof ErrorNotEnoughDStoresToken){
            throw new NotEnoughDstoresException();
        }

        // ERROR_FILE_ALREADY_EXISTS
        else if(response instanceof ErrorFileAlreadyExistsToken){
            throw new FileAlreadyExistsException(file.getName());
        }

        // Invalid Response
        else{
            throw new InvalidMessageException(response.message, this.getServerPort());
        }
    }

    /**
     * Sends the file with the given name to the Dstore listening on the provided port.
     * 
     * @param file The file to be sent to the Dstore
     * @param dstore The Dstore the file is being send to.
     * 
     * @throws MessageSendException If a message couldn't be sent through the connection.
     * @throws MessageReceievedException If a message could not be receieved through the connection.
     * @throws InvalidMessageException If a message of the wrong form is receieved during the communication.
     */
    private void sendFileToDstore(File file, int filesize, int dstore) throws Exception{
        // loading the file
        FileInputStream fileInput = new FileInputStream(file);

        // setting up the connection
        Connection connection = new Connection(this.getClientInterface(), dstore);

        // sending initial message
        connection.sendMessage(Protocol.STORE_TOKEN + " " + file.getName() + " " + filesize);

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
                throw new InvalidMessageException(response.message, connection.getPort());
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
     * 
     * @return Byte array of the loaded file data.
     * 
     * @throws MessageSendException If a message couldn't be sent through the connection.
     * @throws MessageReceievedException If a message could not be receieved through the connection.
     * @throws NotEnoughDstoresException If there are not enough Dstores connected to the controller to handle 
     * the request.
     * @throws FileDoesNotExist If there is no file in the system with the provided filename.
     * @throws InvalidMessageException If a message of the wrong form is receieved during the communication.
     */
    public byte[] loadFile(String filename, boolean isReload) throws Exception{
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

                // returning the file content
                return fileContent;
            }
            // unable to load file content
            catch(Exception e){
                // Logging error
                this.handleError(new FileLoadException(filename, loadFromToken.port, e));

                // reloading if data could not be gathered
                return this.loadFile(filename, true);
            }
        }

        // ERROR_NOT_ENOUGH_DSTORES
        else if(response instanceof ErrorNotEnoughDStoresToken){
            throw new NotEnoughDstoresException();
        }

        // ERROR_FILE_DOES_NOT_EXIST
        else if(response instanceof ErrorFileDoesNotExistToken){
            throw new FileDoesNotExistException(filename);
        }

        // ERROR_LOAD
        else if(response instanceof ErrorLoadToken){
            throw new NoValidDstoresException();
        }

        // Invalid Response
        else{
            throw new InvalidMessageException(response.message, this.getServerPort());
        }
    }

    /**
     * Loads a given file from the provided Dstore.
     * 
     * @param port The port the file is being loaded from.
     * @param filename The name of the file being loaded from the Dstore.
     * @param filesize The size of the file being loaded.
     * @return The file loaded from the Dstore as a byte array.
     * 
     * @throws MessageSendException If a message couldn't be sent through the connection.
     * @throws MessageReceievedException If a message could not be receieved through the connection.
     */
    private byte[] loadFileFromDstore(int port, String filename, int filesize) throws Exception{
        // setting up the connection
        Connection connection = new Connection(this.getClientInterface(), port);

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
     * 
     * @throws MessageSendException If a message couldn't be sent through the connection.
     * @throws MessageReceievedException If a message could not be receieved through the connection.
     * @throws NotEnoughDstoresException If there are not enough Dstores connected to the controller to handle 
     * the request.
     * @throws FileDoesNotExist If there is no file in the system with the provided filename.
     * @throws InvalidMessageException If a message of the wrong form is receieved during the communication.
     */
    public void removeFile(String filename) throws Exception{
        // sending remove to controller
        this.getServerConnection().sendMessage(Protocol.REMOVE_TOKEN + " " + filename);

        // gathering response
        Token response = RequestTokenizer.getToken(this.getServerConnection().getMessageWithinTimeout(this.getTimeout()));

        // REMOVE
        if(response instanceof RemoveCompleteToken){
            // Nothing to do...
        }

        // ERROR_NOT_ENOUGH_DSTORES
        else if(response instanceof ErrorNotEnoughDStoresToken){
            throw new NotEnoughDstoresException();
        }

        // ERROR_FILE_DOES_NOT_EXIST
        else if(response instanceof ErrorFileDoesNotExistToken){
            throw new FileDoesNotExistException(filename);
        }

        // Invalid Response
        else{
            throw new InvalidMessageException(response.message, this.getServerPort());
        }
    }

    //////////
    // LIST //
    //////////

    /**
     * Handles a LIST request.
     * 
     * @throws MessageSendException If a message couldn't be sent through the connection.
     * @throws MessageReceievedException If a message could not be receieved through the connection.
     */
    public ArrayList<String> getFileList() throws Exception{
        // sending message to Controller
        this.getServerConnection().sendMessage(Protocol.LIST_TOKEN);

        // gathering response
        Token response = RequestTokenizer.getToken(this.getServerConnection().getMessageWithinTimeout(this.getTimeout()));

        // LIST file1 file2 ...
        if(response instanceof ListFilesToken){
            // getting the file list token
            ListFilesToken listFilesToken = (ListFilesToken) response;

            // returning the list of files
            return listFilesToken.filenames;
        }

        // ERROR_NOT_ENOUGH_DSTORES
        else if(response instanceof ErrorNotEnoughDStoresToken){
            throw new NotEnoughDstoresException();
        }

        // Invalid response
        else{
            throw new InvalidMessageException(response.message, this.getServerPort());
        }
    }
}

// ==================== //
// OLD REQUEST HANDLING //
// ==================== //


    //////////////////////
    // REQUEST HANDLING //
    //////////////////////

    // /**
    //  * Handles an input request that has come from the Client (User).
    //  * 
    //  * @param request The input request.
    //  */
    // public void handleInputRequest(String request){
    //     try{
    //         Token requestToken = RequestTokenizer.getToken(request);

    //         // STORE //
    //         if(requestToken instanceof StoreToken){
    //             StoreToken storeToken = (StoreToken) requestToken;
    //             this.handleStoreRequest(storeToken.filename, storeToken.filesize);
    //         }

    //         // LOAD //
    //         else if(requestToken instanceof LoadToken){
    //             LoadToken loadToken = (LoadToken) requestToken;
    //             this.handleLoadRequest(loadToken.filename, false);
    //         }

    //         // REMOVE //
    //         else if(requestToken instanceof RemoveToken){
    //             RemoveToken removeToken = (RemoveToken) requestToken;
    //             this.handleRemoveRequest(removeToken.filename);
    //         }

    //         // LIST //
    //         else if(requestToken instanceof ListToken){
    //             this.handleListRequest();
    //         }

    //         // Invalid Request
    //         else{
    //             this.handleInvalidRequest(request);
    //         }
    //     }
    //     catch(Exception e){
    //         this.handleError(new RequestHandlingException(request, e));
    //     }
    // }


    ///////////
    // STORE //
    ///////////

//      /**
//      * Handles a STORE request.
//      * 
//      * @param filename The name of the file being stored.
//      * @param filesize The size of the file being stored.
//      * @throws MessageSendException If a message couldn't be sent through the connection.
//      * @throws MessageReceievedException If a message could not be receieved through the connection.
//      * @throws NotEnoughDstoresException If there are not enough Dstores connected to the controller to handle 
//      * the request.
//      * @throws FileAlreadyExistsException If there is already a file with this name stored in the Dstore.
//      * @throws InvalidMessageException If a message of the wrong form is receieved during the communication.
//      */
//     private void handleStoreRequest(String filename, int filesize) throws Exception{
//         // sending the store message to the controller
//         this.getServerConnection().sendMessage(Protocol.STORE_TOKEN + " " + filename + " " + filesize);

//         // gathering response
//         Token response = RequestTokenizer.getToken(this.getServerConnection().getMessageWithinTimeout(this.getTimeout()));
        
//         // STORE_TO
//         if(response instanceof StoreToToken){
//             // gathering the token
//             StoreToToken storeToToken = (StoreToToken) response;
            
//             // sending file to each dstore
//             for(int dstore : storeToToken.ports){
//                 this.sendFileToDstore(filename, filesize, dstore);
//             }

//             // waiting for response from Controller
//             response = RequestTokenizer.getToken(this.getServerConnection().getMessageWithinTimeout(this.getTimeout()));

//             // STORE_COMPLETE
//             if(response instanceof StoreCompleteToken){
//                 // nothing to do... as the store is complete
//             }

//             // Invalid Response
//             else{
//                 throw new InvalidMessageException(response.message, this.getServerPort());
//             }
//         }

//         // ERROR_NOT_ENOUGH_DSTORES
//         else if(response instanceof ErrorNotEnoughDStoresToken){
//             throw new NotEnoughDstoresException();
//         }

//         // ERROR_FILE_ALREADY_EXISTS
//         else if(response instanceof ErrorFileAlreadyExistsToken){
//             throw new FileAlreadyExistsException(filename);
//         }

//         // Invalid Response
//         else{
//             throw new InvalidMessageException(response.message, this.getServerPort());
//         }
//     }

//     /**
//      * Sends the file with the given name to the Dstore listening on the provided port.
//      * 
//      * @param filename The name of the file to be send to the Sstore.
//      * @param dstore The Dstore the file is being send to.
//      * 
//      * @throws MessageSendException If a message couldn't be sent through the connection.
//      * @throws MessageReceievedException If a message could not be receieved through the connection.
//      * @throws InvalidMessageException If a message of the wrong form is receieved during the communication.
//      */
//     private void sendFileToDstore(String filename, int filesize, int dstore) throws Exception{
//         // loading the file
//         File file = new File(filename);
//         FileInputStream fileInput = new FileInputStream(file);

//         // setting up the connection
//         Connection connection = new Connection(this.getClientInterface(), dstore);

//         // sending initial message
//         connection.sendMessage(Protocol.STORE_TOKEN + " " + filename + " " + filesize);

//         try{
//             // waiting for acknowledgement
//             Token response = RequestTokenizer.getToken(connection.getMessageWithinTimeout(this.getTimeout()));

//             // making sure acknowledgement was receieved
//             if(response instanceof AckToken){
//                 // sending the file to the dstore
//                 byte[] fileContent = fileInput.readNBytes(filesize);
//                 connection.sendBytes(fileContent);

//                 // closing streams
//                 connection.close();
//                 fileInput.close();
//             }
//             // invalid response received
//             else{
//                 // closing streams
//                 connection.close();
//                 fileInput.close();

//                 // throwing exception
//                 throw new InvalidMessageException(response.message, connection.getPort());
//             }
//         }
//         catch(Exception e){
//             // closing connection
//             connection.close();

//             // throwing exception
//             throw e;
//         }
//     }

//////////
// LOAD //
//////////

//      /**
//      * Handles a LOAD request.
//      * 
//      * @param filename The name of the file being removed.
//      * @param isReload Determines if this LOAD operation is a Reload or not.
//      * 
//      * @throws MessageSendException If a message couldn't be sent through the connection.
//      * @throws MessageReceievedException If a message could not be receieved through the connection.
//      * @throws NotEnoughDstoresException If there are not enough Dstores connected to the controller to handle 
//      * the request.
//      * @throws FileDoesNotExist If there is no file in the system with the provided filename.
//      * @throws InvalidMessageException If a message of the wrong form is receieved during the communication.
//      */
//     private void handleLoadRequest(String filename, boolean isReload) throws Exception{
//         // gathering the protocol command
//         String command = "";
//         if(!isReload){
//             command = Protocol.LOAD_TOKEN;
//         }
//         else{
//             command = Protocol.RELOAD_TOKEN;
//         }

//         // sending LOAD message to controller
//         this.getServerConnection().sendMessage(command+ " " + filename);

//         // gathering response
//         Token response = RequestTokenizer.getToken(this.getServerConnection().getMessageWithinTimeout(this.getTimeout()));
        
//         // LOAD_FROM
//         if(response instanceof LoadFromToken){
//             // gathering the token
//             LoadFromToken loadFromToken = (LoadFromToken) response;

//             // LOADING FILE
//             try{
//                 // loading file from Dstore
//                 byte[] fileContent = this.loadFileFromDstore(loadFromToken.port, filename, loadFromToken.filesize);

//                 // storing the file
//                 File file = new File(filename);
//                 FileOutputStream fileOutput = new FileOutputStream(file);
//                 fileOutput.write(fileContent);
//                 fileOutput.flush();
//                 fileOutput.close();
//             }
//             // unable to load file content
//             catch(Exception e){
//                 // Logging error
//                 this.handleError(new FileLoadException(filename, loadFromToken.port, e));

//                 // reloading if data could not be gathered
//                 this.handleLoadRequest(filename, true);
//             }
//         }

//         // ERROR_NOT_ENOUGH_DSTORES
//         else if(response instanceof ErrorNotEnoughDStoresToken){
//             throw new NotEnoughDstoresException();
//         }

//         // ERROR_FILE_DOES_NOT_EXIST
//         else if(response instanceof ErrorFileDoesNotExistToken){
//             throw new FileDoesNotExistException(filename);
//         }

//         // ERROR_LOAD
//         else if(response instanceof ErrorLoadToken){
//             throw new NoValidDstoresException();
//         }

//         // Invalid Response
//         else{
//             throw new InvalidMessageException(response.message, this.getServerPort());
//         }
//     }

////////////
// REMOVE //
////////////

//      /**
//      * Handles a REMOVE request.
//      * 
//      * @param filename The name of the file being removed.
//      * 
//      * @throws MessageSendException If a message couldn't be sent through the connection.
//      * @throws MessageReceievedException If a message could not be receieved through the connection.
//      * @throws NotEnoughDstoresException If there are not enough Dstores connected to the controller to handle 
//      * the request.
//      * @throws FileDoesNotExist If there is no file in the system with the provided filename.
//      * @throws InvalidMessageException If a message of the wrong form is receieved during the communication.
//      */
//     private void handleRemoveRequest(String filename) throws Exception{
//         // sending remove to controller
//         this.getServerConnection().sendMessage(Protocol.REMOVE_TOKEN + " " + filename);

//         // gathering response
//         Token response = RequestTokenizer.getToken(this.getServerConnection().getMessageWithinTimeout(this.getTimeout()));

//         // REMOVE
//         if(response instanceof RemoveCompleteToken){
//             // Nothing to do...
//         }

//         // ERROR_NOT_ENOUGH_DSTORES
//         else if(response instanceof ErrorNotEnoughDStoresToken){
//             throw new NotEnoughDstoresException();
//         }

//         // ERROR_FILE_DOES_NOT_EXIST
//         else if(response instanceof ErrorFileDoesNotExistToken){
//             throw new FileDoesNotExistException(filename);
//         }

//         // Invalid Response
//         else{
//             throw new InvalidMessageException(response.message, this.getServerPort());
//         }
//     }

//////////
// LIST //
//////////

    // /**
    //  * Handles a LIST request.
    //  * 
    //  * @throws MessageSendException If a message couldn't be sent through the connection.
    //  * @throws MessageReceievedException If a message could not be receieved through the connection.
    //  */
    // private void handleListRequest() throws Exception{
    //     // sending message to Controller
    //     this.getServerConnection().sendMessage(Protocol.LIST_TOKEN);

    //     // gathering response (dont need to do anything with it)
    //     this.getServerConnection().getMessageWithinTimeout(this.getTimeout());
    // }

/////////////
// INVALID //
/////////////

    // /**
    //  * Handles an invalid request.
    //  * 
    //  * @param request Handles an invalid request.
    //  * @throws MessageSendException If a message couldn't be sent through the connection.
    //  * @throws MessageReceievedException If a message could not be receieved through the connection.
    //  */
    // private void handleInvalidRequest(String request) throws Exception{
    //     // sending message to Controller
    //     this.getServerConnection().sendMessage(request);

    //     // gathering response
    //     this.getServerConnection().getMessageWithinTimeout(this.getTimeout());
    // }