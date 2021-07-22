package Network.Protocol.Exception;

import Network.Server.Server.ServerType;

/**
 * Exception for when ServerSocket cannot be setup for a server.
 */
public class ServerWaitForConnectionException extends NetworkException{

    // member variables
    private ServerType serverType;
    private int port;

    /**
     * Class constructor.
     * 
     * @param serverType
     * @param port
     */
    public ServerWaitForConnectionException(ServerType serverType, int port, Exception cause){
        super(serverType.toString() + " on port : " + port + " unable to setup ServerSocket.", cause); 
        this.serverType = serverType;
        this.port = port;
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
