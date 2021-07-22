package Network.Server;

import Network.Connection;
import Protocol.Token.Token;

/**
 * Abstract class to represent the component of a Server that handles requests.
 * 
 * Server handlers implement the 'handleRequest' method, and recieve requests
 * from their connections or input channels.
 * 
 * Each type of server has it's own type of RequestHandler as they all handle requests
 * in different ways.
 */
public interface RequestHandler {

    /**
     * Handles a given request.
     * 
     * @param request Tokenized request to be handled.
     * @param connection The connection associated with the request.
     */
    public abstract void handleRequest(Connection connection, Token request);
}
