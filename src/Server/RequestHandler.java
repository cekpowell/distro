package Server;

import java.net.Socket;

import Token.Token;

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
