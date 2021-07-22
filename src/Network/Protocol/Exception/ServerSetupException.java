package Network.Protocol.Exception;

import Network.Server.Server.ServerType;

/**
 * Exception for case where Server object could not be 'setup()'.
 */
public class ServerSetupException extends NetworkException{

    // member variables
    private ServerType serverType;

    /**
     * Class Constructor.
     * 
     * @param serverType
     */
    public ServerSetupException(ServerType serverType, Exception cause){
        super("Unable to setup " + serverType.toString() + ".", cause);
        this.serverType = serverType;
    }
    
    /////////////////////////
    // GETTERS AND SETTERS //
    /////////////////////////

    public ServerType getServerType(){
        return this.serverType;
    }
}
