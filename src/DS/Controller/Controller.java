package DS.Controller;

import DS.Controller.Index.*;
import DS.Controller.Rebalancer.Rebalancer;
import DS.Protocol.Exception.*;
import Network.NetworkInterface;
import Network.Protocol.Event.HandeledNetworkEvent;
import Network.Protocol.Event.NetworkEvent;
import Network.Protocol.Exception.*;
import Network.Server.*;

/**
 * Data store controller. 
 * 
 * Connects to Dstores and serves requests from DSClients.
 */
public class Controller extends Server{

    // member variables
    private int port;
    private int minDstores;
    private int timeout;
    private int rebalancePeriod;
    private NetworkInterface networkInterface; 
    private volatile Index index;
    private volatile Rebalancer rebalancer;

    /**
     * Class constructor.
     * 
     * @param port The port the controller should listen on.
     * @param minDstores The number of data stores to replicate files across.
     * @param timeout The timeout length for communication.
     * @param rebalancePeriod The rebalance period.
     * @param networkInterface The NetworkInterface associated with the controller.
     */
    public Controller(int port, int r, int timeout, int rebalancePeriod, NetworkInterface networkInterface){
        // initializing new member variables
        super(ServerType.CONTROLLER, port, networkInterface);
        this.port = port;
        this.minDstores = r;
        this.timeout = timeout;
        this.rebalancePeriod = rebalancePeriod;
        this.networkInterface = networkInterface;
        this.index = new Index(this);
        this.rebalancer = new Rebalancer(this);
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
     * @throws ServerSetupException If the Controller could not be setup.
     */
    public void setup() throws ServerSetupException{
        try{
            // starting rebalance thread
            this.rebalancer.start();
        }
        catch(Exception e){
            throw new ServerSetupException(ServerType.CONTROLLER, e);
        }
    }

    ////////////////////
    // EVENT HANDLING //
    ////////////////////

    /**
     * Handles an event that has occured.
     * 
     * @param event The event that has occured.
     */
    public void handleEvent(NetworkEvent event){
        // handling the event
        // ?? nothing to handle

        // logging the event
        this.getNetworkInterface().logEvent(new HandeledNetworkEvent(event));
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
            ConnectionTerminatedException exception = (ConnectionTerminatedException) error;

            // Dstore Disconnected //

            for(DstoreIndex dstore : this.index.getDstores()){
                if(dstore.getConnection() == exception.getConnection()){
                    // removing the dstore from the index
                    this.index.removeDstore(exception.getConnection());

                    // removing the dstore from the server
                    this.getServerConnections().remove(exception.getConnection());

                    // logging the disconnect
                    this.getNetworkInterface().logError(new HandeledNetworkException(new DstoreDisconnectException(dstore.getPort(), exception)));

                    // rebalancing
                    try{
                        this.getRebalancer().rebalance();
                    }
                    catch(NetworkException e){
                        this.handleError(new RebalanceFailureException(e));
                    }

                    return; // nothing else to do
                }
            }

            // Client Disconnected //

            if(this.getClientConnections().contains(exception.getConnection())){
                // removing the client from the server
                this.getClientConnections().remove(exception.getConnection());

                // logging the disconnect
                this.getNetworkInterface().logError(new HandeledNetworkException(new ClientDisconnectException(exception.getConnection().getPort(), exception)));
            }

            // Client Heartbeat Disconnect //

            else if(this.getClientHeartbeatConnections().containsKey(exception.getConnection())){
                // getting the client of the heartbeat's port
                int clientPort = this.getClientHeartbeatConnections().get(exception.getConnection());

                // removing the client heartbeat from the server
                this.getClientHeartbeatConnections().remove(exception.getConnection());

                // logging the disconnect
                this.getNetworkInterface().logError(new HandeledNetworkException(new ClientHeartbeatDisconnectException(clientPort, exception)));
            }

            // Unknown connector disconnected //

            else{
                // nothing to handle

                // logging the disconnect
                this.getNetworkInterface().logError(new HandeledNetworkException(new UnknownConnectorDisconnectException(exception.getConnection().getPort(), exception)));
            }
        }

        // Non-important error - just need to log
        else{
            // logging error
            this.getNetworkInterface().logError(new HandeledNetworkException(error));
        } 
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

    public Rebalancer getRebalancer(){
        return this.rebalancer;
    }
}