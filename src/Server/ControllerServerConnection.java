package Server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

import Controller.Controller;
import Logger.*;
import Token.*;
import Token.TokenType.*;

/**
 * Represents a connection from Controller to Connector.
 * 
 * Handles the requests coming in from the connector.
 * 
 * The connector could be either a Client (e.g., STORE), or a 
 * Controller (JOIN).
 * 
 * In this connection, the Controller is the Server, and the 
 * connector is the "Client".
 */
public class ControllerServerConnection extends ServerConnection{

    // member variables
    private Controller controller;

    /**
     * Class constructor.
     * 
     * @param controller The Controller involved in the connection.
     * @param connection The socket connecting the Controller to the Client.
     * @param initialRequest The initial request recieved by the Controller when the Client conected.
     */
    public ControllerServerConnection(Controller controller, Socket connection){
        // initialising member variables
        super(controller, connection);
        this.controller = controller;
    }

    public void waitForRequest(){
        try{
            while(this.hasFurtherRequests()){
                // getting request from connnection
                Token request = RequestTokenizer.getToken(this.getTextIn().readLine());

                // handling request
                this.handleRequest(request);
            }
        }
        catch(NullPointerException e){
            // Connector disconnected - nothing to do.
            MyLogger.logEvent("Connector disconnected on port : " + this.getConnection().getPort()); // MY LOG
        }
        catch(Exception e){
            MyLogger.logError("Controller on port : " + this.controller.getPort() + " unable to connect to new connector.");
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

        ControllerLogger.getInstance().messageReceived(this.getConnection(), request.request);

        //////////////////////
        // Handling request //
        //////////////////////

        // DStore JOIN Request //

        if(request instanceof JoinToken){
            // gathering JOIN token
            JoinToken joinRequest = (JoinToken) request;
            int dstorePort = joinRequest.port;

            // Logging request 
            ControllerLogger.getInstance().dstoreJoined(this.getConnection(), dstorePort);

            // creating connection between controller and DStore
            ControllerDstoreReciever dstoreConnection = new ControllerDstoreReciever(this.controller, this.getConnection(), dstorePort);
            dstoreConnection.start();

            this.noFurtherRequests(); // nothing else to do after a Dstore connection has been created (this object no longer needed)
        }

        // Client Requests //

        else if(request instanceof ListToken){
            this.handleListRequest();
        }

        // Invalid Request //
        else if(request instanceof InvalidRequestToken){
            this.handleInvalidRequest();
        }

        // TODO Handle rest of request types
    }

    /**
     * Handles a LIST request.
     * 
     * ### CURRENT LOGIC DONE FOR TESTING ###
     * 
     * CURRENTLY DOING WHAT IT DOESNT NEED TO DO - SENDS LIST COMMAND TO ALL INDIVIDUAL DSTORES.
     * NEEDS TO JUST USE THE INDEX IN THE CONTROLLER.
     */
    public void handleListRequest(){
        try{
            ArrayList<String> messageElements = new ArrayList<String>();
            messageElements.add("LIST");
    
            // looping through list of Dstores
            for(ControllerDstoreReciever dstore : this.controller.getdstores()){

                // setting up socket
                int dstoreListenPort = dstore.getDstoreListenPort();
                Socket dstoreConnection = new Socket(InetAddress.getLocalHost(), dstoreListenPort);

                // setting up streams
                PrintWriter out = new PrintWriter (new OutputStreamWriter(dstoreConnection.getOutputStream()));
                BufferedReader in = new BufferedReader (new InputStreamReader(dstoreConnection.getInputStream()));
    
                // sending request to dstore
                String request = Protocol.LIST_TOKEN;
                out.println(request);
                out.flush(); // closing the stream
    
                // Logging
                ControllerLogger.getInstance().messageSent(dstore.getConnection(), request);
    
                // gathering response
                Token response = RequestTokenizer.getToken(in.readLine());
    
                // Logging 
                ControllerLogger.getInstance().messageReceived(dstore.getConnection(), response.request);
    
                if(response instanceof ListFilesToken){
                    // adding response to message elements
                    ListFilesToken listFilesToken = (ListFilesToken) response;
                    messageElements.addAll(listFilesToken.filenames);
                }
            }
    
            // sending response back to client
            String message = String.join(" ", messageElements);
            this.getTextOut().println(message);
            this.getTextOut().flush();
    
            // Logging 
            ControllerLogger.getInstance().messageSent(this.getConnection(), message);
        }
        catch(Exception e){
            MyLogger.logError("Unable to perform list command for Client on port : " + this.getConnection().getPort());
        }
    }

    /**
     * Handles an invalid request.
     */
    public void handleInvalidRequest(){
        MyLogger.logError("Invalid request recieved from connector on port : " + this.getConnection().getPort());
    }
}
