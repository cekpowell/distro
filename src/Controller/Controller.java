package Controller;

import java.util.ArrayList;
import java.util.HashMap;

import Index.DstoreIndex;
import Index.Index;
import Network.*;

/**
 * Data store controller. 
 * 
 * Connects to Dstores and servers requests from DSClients.
 */
public class Controller extends Server{

    // member variables
    private volatile int port;
    private volatile int minDstores;
    private volatile int timeout;
    private volatile int rebalancePeriod;
    private volatile ControllerInterface controllerInterface; // TODO This is only required because Controller must log a JOIN request from a server seperatley - when this request is not needed, this property can be removed.

    // indexes
    private Index index;

    /**
     * Class constructor.
     * 
     * @param port The port the controller should listen on.
     * @param minDstores The number of data stores to replicate files across.
     * @param timeout The timeout length for communication.
     * @param rebalancePeriod The rebalance period.
     */
    public Controller(int port, int r, int timeout, int rebalancePeriod, ControllerInterface controllerInterface){
        // initializing new member variables
        super(ServerType.CONTROLLER, port, controllerInterface);
        this.port = port;
        this.minDstores = r;
        this.timeout = timeout;
        this.rebalancePeriod = rebalancePeriod;
        this.controllerInterface = controllerInterface;
        this.index = new Index(this.minDstores);
        this.setRequestHandler(new ControllerRequestHandler(this));
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
     * Adds the given Dstore to the index.
     * 
     * @param port The port of the Dstore to be added.
     * @param connection The connection to the Dstore to be added.
     */
    public void addDstore(int port, Connection connection){
        // adding dstore to the index
        this.index.addDstore(port, connection);

        // logging
        this.controllerInterface.logDstoreJoined(connection.getSocket(), port);
    }

    /**
     * Adds the given file to the index provided there are enough Dstores in the system.
     * 
     * @param file The name of the file being added.
     */
    public ArrayList<Integer> addFile(String filename, int filesize) throws Exception{
        // throwing exception if not enougn dstores have joined yet
        if(this.index.getDstores().size() < this.minDstores){
            throw new Exception();
        }

        // getting the list of dstores that the file needs to be stored on.
        ArrayList<Integer> dstoresToStoreOn = this.index.getDstoresToStoreOn();

        // calling the store method in the index
        this.index.startStoring(filename, filesize, dstoresToStoreOn);

        // returning the list of dstores the file needs to be stored on
        return dstoresToStoreOn;
    }

    /**
     * Handles the disconnection of a Connector at the specified port.
     * 
     * @param port The port of the connector.
     */
    public void handleDisconnect(int port){
        // checking for Dstore disconnect
        for(DstoreIndex dstore : this.index.getDstores()){
            if(dstore.getConnection().getSocket().getPort() == port){
                this.controllerInterface.handleError("Dstore listening on port : " + dstore.getPort() + " disconnected.");
                this.index.removeDstore(dstore.getConnection());
                return;
            }
        }

        // Unknown connector
        this.controllerInterface.handleError("Unknown connector on port : " + port + " disconnected (most likley a client).");
    }


    /////////////////////////
    // GETTERS AND SETTERS //
    /////////////////////////


    public int getPort(){
        return this.port;
    }

    public int getMinDstores(){
        return this.minDstores;
    }

    public int getTimeout(){
        return this.timeout;
    }

    public int getRebalancePeriod(){
        return this.rebalancePeriod;
    }

    public Index getIndex(){
        return this.index;
    }
}