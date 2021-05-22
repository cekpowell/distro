package Client;

import java.net.Socket;

import Server.*;

/**
 * 
 */
public abstract class ClientInterface implements NetworkInterface{

    /**
     * Method run to start the Client when is has been set up.
     */
    public void startClient(Client client){
        try{
            // trying to start the server
            client.start();
        }
        catch(Exception e){
            // handling error if server could not be started
            this.logError(e.getMessage());
        }
    }

    public abstract void handleResponse(Socket connection, String response);
}
