package Server;

import java.net.Socket;

import Logger.DstoreLogger;
import Logger.Protocol;
import Token.Token;
import Dstore.Dstore;

/**
 * Represents a connection from Dstore to Controller.
 */
public class DstoreControllerConnection extends Connection{

    // member variable
    private Dstore dStore;

    /**
     * Class constructor
     * 
     * @param dStore
     * @param connection
     */
    public DstoreControllerConnection(Dstore dStore, Socket connection, Token initialRequest){
        // initialising member variables
        super(dStore, connection, initialRequest);
        this.dStore = dStore;
    }

    /**
     * Starts listening for incoming requests.
     */
    public void startListening(){
        // TODO
    }

    /**
     * Handles the given request from the Controller.
     */
    public void handleRequest(Token request){
        // Logging request
        DstoreLogger.getInstance().messageReceived(this.getConnection(), request.request);

        // handling request
        // TODO
    }
}
