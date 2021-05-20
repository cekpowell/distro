package Controller; 

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

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
    //private ArrayList<ControllerDstoreReciever> dstores;
    private HashMap<ServerConnection,Integer> dstores;
    private ArrayList<ServerConnection> clients;
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
        super(ServerType.CONTROLLER, port);
        this.port = port;
        this.r = r;
        this.timeout = timeout;
        this.rebalancePeriod = rebalancePeriod;

        // indexes
        this.dstores = new HashMap<ServerConnection,Integer>();
        this.clients = new ArrayList<ServerConnection>();
        this.files = new ArrayList<String>();

        // starting the Controller
        this.setupAndStart(new ControllerRequestHandler(this));
    }

    /**
     * Sets up the Controller ready for use.
     * 
     * Nothing to set up.
     */
    public void setup(){
        // nothing to do...
    }

    /**
     * Handles the disconnection of a Connector at the specified port.
     * @param port The port of the connector.
     */
    public void handleDisconnect(int port){
        // Checking for Client disconnect
        for(ServerConnection clientConnection : this.clients){
            if(clientConnection.getConnection().getPort() == port){
                MyLogger.logError("Client on port : " + port + " disconnected.");
                this.removeClient(clientConnection);
                return;
            }
        }

        // checking for Dstore disconnect
        for(ServerConnection dstoreConnection : this.dstores.keySet()){
            if(dstoreConnection.getConnection().getPort() == port){
                MyLogger.logError("Dstore listening on port : " + this.dstores.get(dstoreConnection)+ " disconnected.");
                this.removeDstore(dstoreConnection);
                return;
            }
        }

        // Unknown connector
        MyLogger.logError("Unknown connector on port : " + port + " disconnected (most likley a new client).");
    }

    /**
     * Adds the given Dstore to the index.
     * 
     * @param dstore The connection to the Dstore to be added.
     */
    public void addDstore(ServerConnection dstoreConnection, int dstorePort){
        this.dstores.put(dstoreConnection,dstorePort);
    }

    /**
     * Adds a given client to the index.
     * 
     * @param client The connection to the client.
     */
    public void addClient(ServerConnection clientConnection){
        this.clients.add(clientConnection);
    }

    /**
     * Removes a given Dstore from the index.
     * 
     * @param dstore The connection to the Dstore to be removed.
     */
    private void removeDstore(ServerConnection dstore){
        this.dstores.remove(dstore);
    }

    /**
     * Removes a given client from the index.
     * 
     * @param client The connection to the client.
     */
    private void removeClient(ServerConnection clientConnection){
        this.clients.remove(clientConnection);
    }


    /////////////////////////
    // GETTERS AND SETTERS //
    /////////////////////////


    public int getPort(){
        return this.port;
    }

    public ArrayList<String> getFiles(){
        return this.files;
    }

    public HashMap<ServerConnection, Integer> getdstores(){
        return this.dstores;
    }

    public ArrayList<ServerConnection> getClients(){
        return this.clients;
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