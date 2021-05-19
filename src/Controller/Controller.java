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
 * 
 */
public class Controller {

    // member variables
    private int cPort;
    private int r;
    private double timeout;
    private double rebalancePeriod;

    // indexes
    private ArrayList<ControllerDStoreConnection> connectedDStores;
    private ArrayList<ControllerClientConnection> connectedClients;

    /**
     * Class constructor.
     * @param port The port the controller should listen on.
     * @param r The number of data stores to replicate files across.
     * @param timeout The timeout length for communication.
     * @param rebalancePeriod The rebalance period.
     */
    public Controller(int cPort, int r, double timeout, double rebalancePeriod){
        // initializing new member variables
        this.cPort = cPort;
        this.r = r;
        this.timeout = timeout;
        this.rebalancePeriod = rebalancePeriod;
        this.connectedDStores = new ArrayList<ControllerDStoreConnection>();
        this.connectedClients = new ArrayList<ControllerClientConnection>();

        try{
            ControllerLogger.init(Logger.LoggingType.ON_TERMINAL_ONLY);
        }
        catch(Exception e){
            ErrorLogger.logError("Unable to create Controller Logger for Controller on port : " + this.cPort);
        }

        // waiting for new connection
        this.startListening();
    }

    /**
     * Handles incoming communication between Controller and DStore or Client.
     */
    public void startListening(){
        try{
            ServerSocket listener = new ServerSocket(this.cPort);

            // listening for connections
            while (true){
                Socket connector = listener.accept();

                // setting up the connection
                this.setUpConnection(connector);
            }
        }
        catch(Exception e){
            ErrorLogger.logError("Controller on port : " + this.cPort + " unable to connect to new connector.");
        }
    }

    /**
     * Sets up a connection between the connector and the controller.
     * @param connection The object connecting the controller
     * @throws Exception Thrown when connection could not be setup.
     */
    public void setUpConnection(Socket connection) throws Exception{

        BufferedReader connectionIn = new BufferedReader( new InputStreamReader(connection.getInputStream()));
        connectionIn.mark(0); // marking the reader to be able to reset it later

        // getting token of request
        String message = connectionIn.readLine();
        Token request = RequestTokenizer.getToken(message);

        // Connector is a DStore //

        if(request instanceof JoinToken){
            this.connectToDStore(request, connection);
        }

        // Connector is a Client //

        else{
            // Setting up connection to Client
            connectionIn.reset();
            this.connectToClient(connection);
        }
    }

    /**
     * Sets up a connnection to between a DStore and the Controller.
     * @param request The request from the DStore.x
     */
    public void connectToDStore(Token request, Socket connection){
        // gathering DStore port
        JoinToken joinRequest = (JoinToken) request;
        int port = joinRequest.port;

        // Setting up connection to DStore
        ControllerDStoreConnection dStoreConnection = new ControllerDStoreConnection(this, port, connection);

        ControllerLogger.getInstance().dstoreJoined(connection, port);

        // adding the connection to the index
        this.connectedDStores.add(dStoreConnection);
    }

    /**
     * Sets up a connnection between a Client and the Controller.
     * @param connection
     */
    public void connectToClient(Socket connection){

        // TODO Setting up connnection to client
        ControllerClientConnection clientConnection = new ControllerClientConnection();

        this.connectedClients.add(clientConnection);
    }

    /**
     * Removes the DStore associated with the given port from the system.
     * @param dStorePort
     */
    public void dropDStore(ControllerDStoreConnection dStoreConnection){
        this.connectedDStores.remove(dStoreConnection);
    }

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
            ErrorLogger.logError("ERROR : Unable to create Controller.");
        }
    }
}