package Interface;

import Server.Client;

/**
 * Abstract class to represnt the interface betwen a Client and the user.
 * 
 * The interface must implement the 'handleResponse' method, which is called
 * by the Client when a response is recieved from a request.
 */
public abstract class ClientInterface implements NetworkInterface{

    /**
     * Tries to run the underlying Client.
     * 
     * Passes an error to the interface's error logger if the start was unsuccessful.
     * 
     * Is really just a convnience method.
     */
    public void startClient(Client client){
        try{
            // trying to start the server
            client.start();
        }
        catch(Exception e){
            // handling error if server could not be started
            this.handleError(e.getMessage());
        }
    }
}
