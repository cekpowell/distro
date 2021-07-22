package Controller;

import java.net.Socket;

import Controller.Index.*;
import Protocol.Exception.*;
import Network.Protocol.Exception.*;
import Network.Server.*;

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
    private ControllerInterface networkInterface; 
    private volatile Index index;

    /**
     * Class constructor.
     * 
     * @param port The port the controller should listen on.
     * @param minDstores The number of data stores to replicate files across.
     * @param timeout The timeout length for communication.
     * @param rebalancePeriod The rebalance period.
     * @param networkInterface The NetworkInterface associated with the controller.
     */
    public Controller(int port, int r, int timeout, int rebalancePeriod, ControllerInterface networkInterface){
        // initializing new member variables
        super(ServerType.CONTROLLER, port, networkInterface);
        this.port = port;
        this.minDstores = r;
        this.timeout = timeout;
        this.rebalancePeriod = rebalancePeriod;
        this.networkInterface = networkInterface;
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
     *
     * @throws ServerSertupException If the Controller could not be setup.
     */
    public void setup() throws ServerSetupException{
        try{
            this.getServerInterface().createLogger();
        }
        catch(Exception e){
            throw new ServerSetupException(ServerType.CONTROLLER, e);
        }
    }

    ////////////////////
    // ERROR HANDLING //
    ////////////////////

    /**
     * Handles an error that occured within the system.
     * 
     * @param error The error that has occured.
     */
    public void handleError(NetworkException error){
        // Connection Termination
        if(error instanceof ConnectionTerminatedException){
            // getting connection exception
            ConnectionTerminatedException connection = (ConnectionTerminatedException) error;

            // Dstore disconnecteed
            for(DstoreIndex dstore : this.index.getDstores()){
                if(dstore.getConnection().getPort() == connection.getPort()){
                    // removing the dstore
                    this.index.removeDstore(dstore.getConnection());

                    // logging the disconnect
                    this.getServerInterface().logError(new HandeledNetworkException(new DstoreDisconnectException(connection.getPort(), connection)));
                    return;
                }
            }

            // Client disconnected
            this.getServerInterface().logError(new HandeledNetworkException(new ClientDisconnectException(connection.getPort(), connection)));
        }
        // Non-important error - just need to log
        else{
            // logging error
            this.getServerInterface().logError(new HandeledNetworkException(error));
        } 
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
        this.networkInterface.logDstoreJoined(socket, port);
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