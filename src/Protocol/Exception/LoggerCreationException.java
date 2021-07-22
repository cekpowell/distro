package Protocol.Exception;

import Network.Protocol.Exception.NetworkException;
import Network.Server.Server.ServerType;

/**
 * Exception for the case where the Logger class cannot be instantiated.
 */
public class LoggerCreationException extends NetworkException{

    // member variables
    private ServerType serverType;
    
    /**
     * Class constructor.
     * 
     * @param serverType The type of server that couldn't create the logger.
     */
    public LoggerCreationException(ServerType serverType){
        super("Unable to create " + serverType.toString() + " Logger.");
    }

    /**
     * Class constructor.
     * 
     * @param serverType The type of server that couldn't create the logger.
     */
    public LoggerCreationException(ServerType serverType, Exception cause){
        super("Unable to create " + serverType.toString() + " Logger.", cause);
    }

    /////////////////////////
    // GETTERS AND SETTERS //
    /////////////////////////

    public ServerType getServerType(){
        return this.serverType;
    }
}
