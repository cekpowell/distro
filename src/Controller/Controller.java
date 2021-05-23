package Controller;

import java.util.ArrayList;
import java.util.HashMap;

import Server.*;

/**
 * Data store controller. 
 * 
 * Connects to Dstores and servers requests from DSClients.
 */
public class Controller extends Server{

    // member variables
    private int port;
    private int r;
    private int timeout;
    private int rebalancePeriod;
    private ControllerInterface controllerInterface; // TODO This is only required because Controller must log a JOIN request from a server seperatley - when this request is not needed, this property can be removed.

    // indexes
    private HashMap<Connection,Integer> dstores;
    private ArrayList<Connection> clients;
    private ArrayList<String> files;

    /**
     * Class constructor.
     * 
     * @param port The port the controller should listen on.
     * @param r The number of data stores to replicate files across.
     * @param timeout The timeout length for communication.
     * @param rebalancePeriod The rebalance period.
     */
    public Controller(int port, int r, int timeout, int rebalancePeriod, ControllerInterface controllerInterface){
        // initializing new member variables
        super(ServerType.CONTROLLER, port, controllerInterface);
        this.port = port;
        this.r = r;
        this.timeout = timeout;
        this.rebalancePeriod = rebalancePeriod;
        this.controllerInterface = controllerInterface;
        this.setRequestHandler(new ControllerRequestHandler(this));

        // indexes
        this.dstores = new HashMap<Connection,Integer>();
        this.clients = new ArrayList<Connection>();
        this.files = new ArrayList<String>();
    }

    /**
     * Set's up the Controller ready for use.
     * 
     * Creates the logger.
     */
    public void setup() throws Exception{
        try{
            this.getServerInterface().createLogger();
        }
        catch(Exception e){
            throw new Exception("Unable to create Controller Logger for Controller on port : " + this.port);
        }
    }

    /**
     * Handles the disconnection of a Connector at the specified port.
     * @param port The port of the connector.
     */
    public void handleDisconnect(int port){
        // Checking for Client disconnect
        for(Connection clientConnection : this.clients){
            if(clientConnection.getSocket().getPort() == port){
                this.controllerInterface.handleError("Client on port : " + port + " disconnected.");
                this.removeClient(clientConnection);
                return;
            }
        }

        // checking for Dstore disconnect
        for(Connection dstoreConnection : this.dstores.keySet()){
            if(dstoreConnection.getSocket().getPort() == port){
                this.controllerInterface.handleError("Dstore listening on port : " + this.dstores.get(dstoreConnection)+ " disconnected.");
                this.removeDstore(dstoreConnection);
                return;
            }
        }

        // Unknown connector
        this.controllerInterface.handleError("Unknown connector on port : " + port + " disconnected (most likley a new client).");
    }

    /**
     * Adds the given Dstore to the index.
     * 
     * @param dstore The connection to the Dstore to be added.
     */
    public void addDstore(Connection dstoreConnection, int dstorePort){
        this.dstores.put(dstoreConnection,dstorePort);

        // logging
        this.controllerInterface.logDstoreJoined(dstoreConnection.getSocket(), dstorePort);
    }

    /**
     * Adds a given client to the index.
     * 
     * @param client The connection to the client.
     */
    public void addClient(Connection clientConnection){
        this.clients.add(clientConnection);
    }

    /**
     * Removes a given Dstore from the index.
     * 
     * @param dstore The connection to the Dstore to be removed.
     */
    private void removeDstore(Connection dstore){
        this.dstores.remove(dstore);
    }

    /**
     * Removes a given client from the index.
     * 
     * @param client The connection to the client.
     */
    private void removeClient(Connection clientConnection){
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

    public HashMap<Connection, Integer> getdstores(){
        return this.dstores;
    }

    public ArrayList<Connection> getClients(){
        return this.clients;
    }
}