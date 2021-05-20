package Server;

import Token.Token;

/**
 * Abstract class to represent the component of a Server that handles requests.
 * 
 * Server handlers implement the 'handleRequest' method, and recieve requests
 * from their 'ServerConnection's.
 * 
 * Each type of server has it's own type of RequestHandler as they all handle requests
 * in different ways.
 */
public abstract class RequestHandler {
    
    // member variables
    private Server server;

    /**
     * Class constructor.
     * 
     * @param server The server associated with this request handler.
     */
    public RequestHandler(Server server){
        // initialising member variables
        this.server = server;
    }

    /**
     * Handles a given request.
     * 
     * @param request Tokenized request to be handled.
     * @param connection The connection associated with the request.
     */
    public abstract void handleRequest(ServerConnection connection, Token request);
}
