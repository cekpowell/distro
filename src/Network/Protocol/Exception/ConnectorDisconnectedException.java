package Network.Protocol.Exception;

public class ConnectorDisconnectedException extends NetworkException{
    
    // member variables
    private int port;
    
    /**
     * Class constructor.
     * 
     * @param clientType
     * @param port
     */
    public ConnectorDisconnectedException(int port){
        super("The connector on port : " + port + " disconnected.");
        this.port = port;
    }

    /**
     * Class constructor.
     * 
     * @param clientType
     * @param port
     */
    public ConnectorDisconnectedException(int port, Exception cause){
        super("A connector on port : " + port + " disconnected.", cause);
        this.port = port;
    }

    /////////////////////////
    // GETTERS AND SETTERS //
    /////////////////////////

    public int getPort(){
        return this.port;
    }
}
