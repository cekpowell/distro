package Dstore;

import java.util.ArrayList;
import java.io.File;

import Logger.*;
import Server.*;
import Token.*;
import Token.TokenType.*;

/**
 * Handles the requests coming in from the connector.
 * 
 * The connector could be either a Client (e.g., STORE), or a 
 * Controller (e.g., REBALANCE, LIST).
 * 
 * In this connection, the Dstore is the Server, and the 
 * connector is the "Client".
 */
public class DstoreRequestHandler extends RequestHandler{
    
    // member variables
    Dstore dstore;

    public DstoreRequestHandler(Dstore dstore){
        // initializing member variables
        super(dstore);
        this.dstore = dstore;
    }

    /**
     * Handles a given request.
     * 
     * @param request Tokenized request to be handled.
     */
    public void handleRequest(ServerConnection connection, Token request){

        /////////////////////
        // Logging request //
        /////////////////////

        DstoreLogger.getInstance().messageReceived(connection.getConnection(), request.request);

        //////////////////////
        // Handling Request //
        //////////////////////

        if(request instanceof ListToken){
            this.handleListRequest(connection);
        }

        // TODO Handle rest of requests

        connection.noFurtherRequests(); // TODO Does it need to handle further requests? (at the moment i cant think of a case where it would)
    }

    /**
     * Handles a LIST request.
     */
    public void handleListRequest(ServerConnection connection){
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
        connection.getTextOut().println(message);
        connection.getTextOut().flush();

        // Logging
        DstoreLogger.getInstance().messageSent(connection.getConnection(), message);
    }
}
