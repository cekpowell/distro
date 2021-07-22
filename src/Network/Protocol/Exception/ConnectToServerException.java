package Network.Protocol.Exception;

import Network.Server.Server.ServerType;

/**
 * Exception for the case when a client cannot connect to the server.
 */
public class ConnectToServerException extends NetworkException{

    // member variables
    private ServerType serverType;
    private int port;

    /**
     * Class constructor.
     */
    public ConnectToServerException(ServerType serverType, int port){
        super("Unable to connect to " + serverType + " on port : " + port);
        this.serverType = serverType;
        this.port = port;
    }

    /**
     * Class constructor.
     */
    public ConnectToServerException(ServerType serverType, int port, Exception cause){
        super("Unable to connect to " + serverType + " on port : " + port, cause);
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
