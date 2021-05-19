package Server;

import java.net.Socket;

import Dstore.Dstore;
import Logger.DstoreLogger;
import Token.Token;

/**
 * Represents a connection from Dstore to Client.
 */
public class DstoreClientConnection extends Connection{
    
    // member variables
    private Dstore dstore;

    /**
     * Class constructor.
     * 
     * @param dstore The Dstore involved in the connection.
     * @param connection The connection between the Dstore and the client
     * @param initialRequest The initial request from the client.
     */
    public DstoreClientConnection(Dstore dstore, Socket connection, Token initialRequest){
        // initialising member variables
        super(dstore, connection, initialRequest);
        this.dstore = dstore;
    }

    /**
     * Starts listening for incoming requests.
     */
    public void startListening(){
        // TODO
    }

    /**
     * Handles a given request.
     * 
     * @param request Tokenized request to be handled.
     */
    public void handleRequest(Token request){
        // Logging request
        DstoreLogger.getInstance().messageReceived(this.getConnection(), request.request);

        // handling request
        // TODO
    }
}
