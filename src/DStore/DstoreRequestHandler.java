package Dstore;

import java.util.ArrayList;
import java.io.File;

import Server.*;
import Token.*;
import Token.TokenType.*;

/**
 * Handles requests sent to a Dstore by a Client.
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

        this.dstore.getServerInterface().logMessageReceived(connection.getConnection(), request.message);

        //////////////////////
        // Handling Request //
        //////////////////////

        if(request instanceof ListToken){
            this.handleListRequest(connection);
        }

        // TODO Handle rest of requests

        connection.close(); // TODO Does it need to handle further requests? (at the moment i cant think of a case where it would as any request to the Dstore from a client is onlly requerst and response, with nothing after the response)
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
        this.dstore.getServerInterface().logMessageSent(connection.getConnection(), message);
    }
}
