package Controller; 

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import Logger.ControllerLogger;
import Server.*;
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
    private ArrayList<ControllerDstoreReciever> dstores;
    private ArrayList<String> files;

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
        this.dstores = new ArrayList<ControllerDstoreReciever>();
        this.files = new ArrayList<String>();

        // starting the Controller
        this.setupAndRun();
    }

    /**
     * Sets up and starts the Controller for the system.
     * 
     * Trys to setup a Logger and then starts listening for new connections.
     */
    public void setupAndRun(){
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
                Socket connection = listener.accept();

                // setting up the connection
                this.setUpConnection(connection);
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
        // Setting up connnection to connector
        ControllerServerConnection controllerConnection = new ControllerServerConnection(this, connection);
        controllerConnection.start();
    }

    /**
     * Adds the given Dstore to the index.
     * @param dstore The connection to the Dstore to be added.
     */
    public void addDstore(ControllerDstoreReciever dstore){
        this.dstores.add(dstore);
    }

    /**
     * Removes a given Dstore from the index.
     * 
     * @param dstore The connection to the Dstore to be removed.
     */
    public void removeDstore(ControllerDstoreReciever dstore){
        this.dstores.remove(dstore);
    }


    /////////////////////////
    // GETTERS AND SETTERS //
    /////////////////////////


    public int getPort(){
        return this.port;
    }

    public ArrayList<ControllerDstoreReciever> getdstores(){
        return this.dstores;
    }

    public ArrayList<String> getFiles(){
        return this.files;
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