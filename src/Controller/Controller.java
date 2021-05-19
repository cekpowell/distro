package Controller; 

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import Logger.ControllerLogger;
import Server.*;
import Token.*;
import Token.TokenType.JoinToken;
import Logger.*;

/**
 * Data store controller. Servers requests from Clients and connects Clients to DStores.
 */
public class Controller extends Server{

    // member variables
    private int port;
    private int r;
    private double timeout;
    private double rebalancePeriod;

    // indexes
    private ArrayList<Integer> dstores;
    private ArrayList<ControllerClientConnection> clients;

    /**
     * Class constructor.
     * 
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
        this.dstores = new ArrayList<Integer>();
        this.clients = new ArrayList<ControllerClientConnection>();

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
            MyLogger.logError("Controller on port : " + this.port + " unable to connect to new connector.");
        }
    }
    

    /**
     * Sets up a connection between the connector and the controller.
     * 
     * @param connection The object connecting the controller
     * @throws Exception Thrown when connection could not be setup.
     */
    public void setUpConnection(Socket connection){
        try{
            // getting request from connnection
            BufferedReader connectionIn = new BufferedReader( new InputStreamReader(connection.getInputStream()));
            Token requestToken = RequestTokenizer.getToken(connectionIn.readLine());

            // Connector is a DStore //

            if(requestToken instanceof JoinToken){
                // logging new dstore connection
                MyLogger.logEvent("New DStore connected on port : " + connection.getPort()); // MY LOG
                
                // Setting up connection to new Dstore
                    // (doesnt really set up a connection, as it just adds the dstor to the index (dont need an active connection at this point))
                this.connectToDstore((JoinToken) requestToken); 
            }

            // Connector is a Client //

            else{
                // logging new client connection
                MyLogger.logEvent("New Client connected on port : " + connection.getPort()); // MY LOG

                // Setting up connection to Client
                this.connectToClient(requestToken, connection);
            }
        }
        catch(Exception e){
            MyLogger.logError("Controller on port : " + this.port + " unable to connect to new connector.");
        }
    }

    /**
     * Sets up a connnection to between a DStore and the Controller.
     * 
     * Doesnt really set up a connection as it just adds the Dstore's port number 
     * to the index. 
     * 
     * This is because an active connection is not required at this point.
     * 
     * @param joinRequest The tokenized request from the DStore.
     * @param connection The connection to the DStore
     */
    public void connectToDstore(JoinToken joinRequest){
        // gathering DStore port
        int dstorePort = joinRequest.port;

        // adding the dstore to the index
        this.dstores.add(dstorePort);
    }

    /**
     * Sets up a connnection between a Client and the Controller.
     * 
     * @param request The tokenized request from the Client.
     * @param connection The connection to the Client.
     */
    public void connectToClient(Token request, Socket connection){

        // Setting up connnection to client
        ControllerClientConnection client = new ControllerClientConnection(this, connection, request);
        client.start();

        // adding the client to the index
        this.clients.add(client);
    }

    /**
     * Removes a given Dstore from the index.
     * 
     * @param dstorePort The port of the dstore to be removed.
     */
    public void dropDstore(int dstorePort){
        this.dstores.remove(dstorePort);
    }

    /**
     * Removes a given Client from the index.
     * 
     * @param client The Client connection to be removed.
     */
    public void dropClient(ControllerClientConnection client){
        this.clients.remove(client);
    }

    /////////////////
    // MAIN METHOD //
    /////////////////

    /**
     * Main method - instantiates a new Controller instance using the command line parammeters.
     * 
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