package Server;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

import Dstore.Dstore;
import Logger.*;
import Token.*;
import Token.TokenType.ListToken;

/**
 * Represents a connection from a Dstore to a Connector.
 * 
 * Handles the requests coming in from the connector.
 * 
 * The connector could be either a Client (e.g., STORE), or a 
 * Controller (e.g., REBALANCE, LIST).
 * 
 * In this connection, the Dstore is the Server, and the 
 * connector is the "Client".
 */
public class DstoreServerConnection extends ServerConnection{
    
    // member variables
    private Dstore dstore;

    /**
     * Class constructor.
     * 
     * @param dstore The Dstore involved in the connection.
     * @param connection The connection between the Dstore and the client
     */
    public DstoreServerConnection(Dstore dstore, Socket connection){
        // initialising member variables
        super(dstore, connection);
        this.dstore = dstore;
    }

    /**
     * Starts listening for incoming requests from the Client.
     */
    public void waitForRequest(){
        try{
            while(this.hasFurtherRequests()){
                // getting request from connection
                Token request = RequestTokenizer.getToken(this.getTextIn().readLine());

                // handling request
                this.handleRequest(request);
            }
        }
        catch(NullPointerException e){
            // Connnector disconnected - nothing to do.
            MyLogger.logEvent("Connector disconnected on port : " + this.getConnection().getPort()); // MY LOG
        }
        catch(Exception e){
            MyLogger.logError("DStore on port : " + this.dstore.getPort() + " unable to connect to new connector.");
        }
    }

    /**
     * Handles a given request.
     * 
     * @param request Tokenized request to be handled.
     */
    public void handleRequest(Token request){

        /////////////////////
        // Logging request //
        /////////////////////

        DstoreLogger.getInstance().messageReceived(this.getConnection(), request.request);

        //////////////////////
        // Handling Request //
        //////////////////////

        if(request instanceof ListToken){
            this.handleListRequest();
        }

        // TODO Handle rest of requests

        this.noFurtherRequests(); // TODO Does it need to handle further requests? (at the moment i cant think of a case where it would)
    }

    /**
     * Handles a LIST request.
     */
    public void handleListRequest(){
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
        this.getTextOut().println(message);
        this.getTextOut().flush();

        // Logging
        DstoreLogger.getInstance().messageSent(this.getConnection(), message);
    }
}
