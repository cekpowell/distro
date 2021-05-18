package Client;

import java.net.Socket;

/**
 * 
 */
public class Client {

    // member variables
    int cPort;
    double timeout;

    /**
     * 
     * @param cPort
     * @param timeout
     */
    public Client(int cPort, double timeout) {
        // initialising member variables
        this.cPort = cPort;
        this.timeout = timeout;

        // waiting for user input
        this.waitForInput();
    }

    /**
     * Waits for user to input a request into the terminal.
     */
    public void waitForInput(){
        // TODO
    }

    /**
     * Sends a given request to the given port.
     */
    public void sendRequest(int port, String message){
        // TODO
    }

    /**
     * Waits for a response on the given socket.
     */
    public void waitForResponse(Socket socket){
        // TODO
    }
}