package Network.Server;

import DS.Protocol.Exception.RequestHandlerDisabledException;
import DS.Protocol.Token.Token;
import Network.Connection;

/**
 * Abstract class to represent the component of a Server that handles requests.
 * 
 * Server handlers implement the 'handleRequest' method, and recieve requests
 * from their connections or input channels.
 * 
 * Each type of server has it's own type of RequestHandler as they all handle requests
 * in different ways.
 */
public abstract class RequestHandler {

    // member variables
    private Server server;
    private boolean enabled;

    ////////////////////////
    // CLASS CONSTRUUCTOR //
    ////////////////////////

    /**
     * Class constructor.
     * 
     * @param server The Server this request handler is handling requests for.
     */
    public RequestHandler(Server server){
        // initializing
        this.server = server;
        this.enabled = true;
    }

    //////////////////////
    // REQUEST HANDLING //
    //////////////////////

    /**
     * Handles a give request on a new thread. Runs the handle request method on
     * a new thread.
     * 
     * @param connection The connection associated with the request.
     * @param request The request being handeled.
     * @throws RequestHandlerDisabledException If the request has come from a client
     * and the request handler is disabled.
     */
    public void handleRequest(Connection connection, Token request) throws Exception{
        // throwing exception if handler is not enabled and request is from client
        if(!this.isEnabled() && this.server.getClientConnections().contains(connection)){
            throw new RequestHandlerDisabledException();
        }

        // runnable for the request thread
        Runnable runnable = () -> {
            // handling the request
            this.handleRequestAux(connection, request);
        };

        // starting a thread to handle the request
        new Thread(runnable).start();
    }

    /**
     * Handles a given request.
     * 
     * @param request Tokenized request to be handled.
     * @param connection The connection associated with the request.
     */
    public abstract void handleRequestAux(Connection connection, Token request);

    ////////////////////////////
    // ENABLING AND DISABLING //
    ////////////////////////////

    /**
     * Enables the request handler.
     * 
     * The request handler will continue serving requests.
     */
    public void enable(){
        this.enabled = true;
    }

    /**
     * Disables the request handler.
     * 
     * The request handler will finish serving it's current request and
     * serve no further requests until it is enabled again.
     */
    public void disable(){
        this.enabled = false;
    }

    /////////////////////////
    // GETTERS AND SETTERS //
    /////////////////////////

    public boolean isEnabled(){
        return this.enabled;
    }
}
