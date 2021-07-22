package Network.Protocol.Exception;

public class ConnectionSetupException extends NetworkException{

    // member variables
    private int port;

    /**
     * Class constructor.
     * 
     * @param port The port associated with the connection.
     * @param cause The cause of the exception.
     */
    public ConnectionSetupException(int port, Exception cause){
        super("Unable to setup Connection to port : " + port + ".", cause);
        this.port = port;
    }

    /////////////////////////
    // GETTERS AND SETTERS //
    /////////////////////////

    public int getPort(){
        return this.port;
    }
}
