package Network.Protocol.Exception;

import Network.Server.Server.ServerType;

/**
 * Exception for case when a Server could not be started.
 */
public class ServerStartException extends NetworkException{
    
    // member variables
    private ServerType serverType;
    private int port;

    /**
     * Class constructor.
     * 
     * @param serverType The type of server that could not be started.
     * @param port The port the server was meant to be listening on.
     * @param cause The exception that caused the server to not start.
     */
    public ServerStartException(ServerType serverType, int port, Exception cause){
        super("Unable to start " + serverType.toString() + " on port : " + port + ".", cause);
    }

    /////////////////////////
    // GETTERS AND SETTERS //
    /////////////////////////

    public ServerType getServerType(){
        return this.serverType;
    }

    public int getPort(){
        return this.port;
    }
}
