package Controller;

import java.net.Socket;

import Controller.Index.DstoreIndex;
import Controller.Index.Index;
import Network.*;

/**
 * Data store controller. 
 * 
 * Connects to Dstores and servers requests from DSClients.
 */
public class Controller extends Server{

    // member variables
    private int port;
    private int minDstores;
    private int timeout;
    private int rebalancePeriod;
    private ControllerInterface controllerInterface; 
    private volatile Index index;

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
        this.index = new Index(this);
        this.setRequestHandler(new ControllerRequestHandler(this));
    }

    ///////////
    // SETUP //
    ///////////

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

    ////////////////
    // DISCONNECT //
    ////////////////

    /**
     * Handles the disconnection of a Connector at the specified port.
     * 
     * @param port The port of the connector.
     */
    public void handleDisconnect(int port, Exception cause){
        // checking for Dstore disconnect
        for(DstoreIndex dstore : this.index.getDstores()){
            if(dstore.getConnection().getPort() == port){
                this.controllerInterface.handleError("Dstore listening on port : " + dstore.getPort() + " disconnected.", cause);
                this.index.removeDstore(dstore.getConnection());
                return;
            }
        }

        // Unknown connector
        this.controllerInterface.handleError("Unknown connector on port : " + port + " disconnected (most likley a client).", cause);
    }


    ////////////////////
    // DSTORE LOGGING //
    ////////////////////

    /**
     * Logs the joining of a Dstore into the system.
     * 
     * @param socket The connection to the Dstore.
     * @param port The port the Dstore listens on.
     */
    public void logDstoreJoined(Socket socket, int port){
        this.controllerInterface.logDstoreJoined(socket, port);
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