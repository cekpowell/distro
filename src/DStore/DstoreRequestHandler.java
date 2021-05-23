package Dstore;

import java.util.ArrayList;
import java.io.File;

import Server.*;
import Token.*;
import Token.TokenType.*;

/**
 * Handles requests sent to a Dstore by a DSClient and Controller.
 */
public class DstoreRequestHandler implements RequestHandler{
    
    // member variables
    Dstore dstore;

    public DstoreRequestHandler(Dstore dstore){
        // initializing member variables
        this.dstore = dstore;
    }

    /**
     * Handles a given request.
     * 
     * @param request Tokenized request to be handled.
     */
    public void handleRequest(Connection connection, Token request){

        //////////////////////
        // Handling Request //
        //////////////////////

        if(request instanceof ListToken){
            this.handleListRequest(connection);
        }

        // TODO Handle rest of requests
    }

    /**
     * Handles a LIST request.
     */
    public void handleListRequest(Connection connection){
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
}
