package Network.Protocol.Exception;

import Network.Connection;

/**
 * Exception to represent case where the connection between two objects is
 * terminated.
 */
public class ConnectionTerminatedException extends NetworkException{
    
    // member variables
    private Connection connection;

    /**
     * Class constructor.
     */
    public ConnectionTerminatedException(Connection connection){
        super("The connection to connector on port : " + connection.getPort() + " was terminated.");
        this.connection = connection;
    }

    /**
     * Class constructor.
     */
    public ConnectionTerminatedException(Connection connection, Exception cause){
        super("The connection to connector on port : " + connection.getPort() + " was terminated.", cause);
        this.connection = connection;
    }

    /////////////////////////
    // GETTERS AND SETTERS //
    /////////////////////////

    public Connection getConnection(){
        return this.connection;
    }
}
