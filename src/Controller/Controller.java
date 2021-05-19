package Controller; 

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import Logger.ControllerLogger;
import Token.*;
import Token.TokenType.JoinToken;
import Logger.*;

/**
 * Data store controller. Servers requests from Clients and connects Clients to DStores.
 */
public class Controller {

    // member variables
    private int port;
    private int r;
    private double timeout;
    private double rebalancePeriod;

    // indexes
    private ArrayList<ControllerDstoreConnection> connectedDstores;
    private ArrayList<ControllerClientConnection> connectedClients;

    /**
     * Class constructor.
     * @param port The port the controller should listen on.
     * @param r The number of data stores to replicate files across.
     * @param timeout The timeout length for communication.
     * @param rebalancePeriod The rebalance period.
     */
    public Controller(int port, int r, double timeout, double rebalancePeriod){
        // initializing new member variables
        this.port = port;
        this.r = r;
        this.timeout = timeout;
        this.rebalancePeriod = rebalancePeriod;
        this.connectedDstores = new ArrayList<ControllerDstoreConnection>();
        this.connectedClients = new ArrayList<ControllerClientConnection>();

        // creating logger
        try{
            ControllerLogger.init(Logger.LoggingType.ON_TERMINAL_ONLY);
        }
        catch(Exception e){
            MyLogger.logError("Unable to create Controller Logger for Controller on port : " + this.port);
        }

        // waiting for new connection
        this.startListening();
    }

    /**
     * Handles incoming communication between Controller and DStore or Client.
     */
    public void startListening(){
        try{
            ServerSocket listener = new ServerSocket(this.port);

            // listening for connections
            while (true){
                Socket connector = listener.accept();

                // setting up the connection
                this.setUpConnection(connector);
            }
        }
        catch(Exception e){
            MyLogger.logError("Controller on port : " + this.port + " unable to connect to new connector." + e.toString());
        }
    }

    /**
     * Sets up a connection between the connector and the controller.
     * @param connection The object connecting the controller
     * @throws Exception Thrown when connection could not be setup.
     */
    public void setUpConnection(Socket connection) throws Exception{

        BufferedReader connectionIn = new BufferedReader( new InputStreamReader(connection.getInputStream()));

        // getting request
        Token requestToken = RequestTokenizer.getToken(connectionIn.readLine());

        // Connector is a DStore //

        if(requestToken instanceof JoinToken){
            MyLogger.logEvent("New DStore connected on port : " + connection.getPort()); // MY LOG
            // setting up connection to dstore
            this.connectToDStore((JoinToken) requestToken, connection);
        }

        // Connector is a Client //

        else{
            MyLogger.logEvent("New Client connected on port : " + connection.getPort()); // MY LOG
            // Setting up connection to Client
            this.connectToClient(requestToken, connection);
        }
    }

    /**
     * Sets up a connnection to between a DStore and the Controller.
     * @param joinRequest The tokenized request from the DStore.
     * @param connection The connection to the DStore
     */
    public void connectToDStore(JoinToken joinRequest, Socket connection){
        // gathering DStore port
        int port = joinRequest.port;

        // Setting up connection to DStore
        ControllerDstoreConnection dstoreConnection = new ControllerDstoreConnection(this, port, connection);
        dstoreConnection.start();

        // logging the JOIN of the new DStore
        ControllerLogger.getInstance().dstoreJoined(connection, port);

        // adding the connection to the index
        this.connectedDstores.add(dstoreConnection);
    }

    /**
     * Sets up a connnection between a Client and the Controller.
     * @param request The tokenized request from the Client.
     * @param connection The connection to the Client.
     */
    public void connectToClient(Token request, Socket connection){

        // Setting up connnection to client
        ControllerClientConnection clientConnection = new ControllerClientConnection(this, connection, request);
        clientConnection.start();

        this.connectedClients.add(clientConnection);
    }

    /**
     * Removes a given Dstore from the index.
     * @param dstoreConnection The Dstore connection to be removed.
     */
    public void dropDstore(ControllerDstoreConnection dstoreConnection){
        this.connectedDstores.remove(dstoreConnection);
    }

    /**
     * Removes a given Client from the index.
     * @param clientConnection The Client connection to be removed.
     */
    public void dropClient(ControllerClientConnection clientConnection){
        this.connectedClients.remove(clientConnection);
    }

    /////////////////
    // MAIN METHOD //
    /////////////////

    /**
     * Main method - instantiates a new Controller instance using the command line parammeters.
     * @param args Parameters for the new Controller.
     */
    public static void main(String[] args){
        try{
            // gathering parameters
            int cPort = Integer.parseInt(args[0]);
            int r = Integer.parseInt(args[1]);
            double timeout = Double.parseDouble(args[2]);
            double rebalancePeriod = Double.parseDouble(args[3]);

            // Creating new DStore instance
            Controller controller = new Controller(cPort, r, timeout, rebalancePeriod);
        }
        catch(Exception e){
            MyLogger.logError("Unable to create Controller.");
        }
    }
}