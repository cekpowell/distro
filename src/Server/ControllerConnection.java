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
 * Represents a connection from Controller to Client.
 */
public class ControllerConnection extends Connection{

    // member variables
    private Controller controller;

    /**
     * Class constructor.
     * 
     * @param controller The Controller involved in the connection.
     * @param connection The socket connecting the Controller to the Client.
     * @param initialRequest The initial request recieved by the Controller when the Client conected.
     */
    public ControllerConnection(Controller controller, Socket connection){
        // initialising member variables
        super(controller, connection);
        this.controller = controller;
    }

    public void waitForRequest(){
        try{
            // getting request from connnection
            Token request = RequestTokenizer.getToken(this.getTextIn().readLine());

            this.handleRequest(request);
        }
        catch(NullPointerException e){
            // Client disconnected - nothing to do.
            MyLogger.logEvent("Connector disconnected on port : " + this.getConnection().getPort()); // MY LOG

            // removing the client from the index
            this.controller.removeClient(this);
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

        // DStore Join request //

        if(request instanceof JoinToken){
            // gathering JOIN token
            JoinToken joinRequest = (JoinToken) request;
            int port = joinRequest.port;

            // Logging request 
            ControllerLogger.getInstance().dstoreJoined(this.getConnection(), port);

            // adding Dstore to controller
            this.controller.addDstore(port);
            return; // returning as there is nothing else to do after a Dstore has been added (no longer need the connection)
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

        /**
         * If here, then did not return and so not a JOIN request.
         * 
         * The connector must therefore be a Client.
         * 
         * Need to add the Client to the Controller and listen for further requests.
         */
        this.controller.addClient(this);
        this.waitForRequest();
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
            for(int dstore : this.controller.getdstores()){
                // creating new socket for the Dstore
                Socket dstoreSocket = new Socket(InetAddress.getLocalHost(), dstore);

                // sending request to dstore
                String request = Protocol.LIST_TOKEN;
                PrintWriter dStoreOut = new PrintWriter (new OutputStreamWriter(dstoreSocket.getOutputStream()));
                dStoreOut.println(request);
                dStoreOut.flush(); // closing the stream

                // Logging
                ControllerLogger.getInstance().messageSent(dstoreSocket, request);

                // gathering response
                BufferedReader dStoreIn = new BufferedReader(new InputStreamReader(dstoreSocket.getInputStream()));
                Token response = RequestTokenizer.getToken(dStoreIn.readLine());

                // Logging 
                ControllerLogger.getInstance().messageReceived(dstoreSocket, response.request);

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
