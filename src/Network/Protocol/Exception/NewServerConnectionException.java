package Network.Protocol.Exception;

import Network.Server.Server.ServerType;

/**
 * Exception for case where Server cannot set up connection to new connector.
 */
public class NewServerConnectionException extends NetworkException{

    // member variables
    private ServerType serverType;
    private int port;

    /**
     * Class constructor.
     * 
     * @param serverType
     * @param port
     */
    public NewServerConnectionException(ServerType serverType, int port, Exception cause){
        super(serverType.toString() + " on port : " + port + " unable to setup connection to new connector.", cause); 
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
