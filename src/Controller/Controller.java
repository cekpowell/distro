package Controller; 

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import Token.*;
import Token.TokenType.JoinToken;


/**
 * 
 */
public class Controller {

    // member variables
    private int port;
    private int r;
    private double timeout;
    private double rebalancePeriod;

    /**
     * Class constructor.
     * @param port The port the controller should listen on.
     * @param r The number of data stores to replicate files across.
     * @param timeout The timeout length for communication.
     * @param rebalancePeriod The rebalance period.
     */
    public Controller(int port, int r, double timeout, double rebalancePeriod){
        // initializing new member variables
        this.port = port;
        this.r = r;
        this.timeout = timeout;
        this.rebalancePeriod = rebalancePeriod;

        // waiting for new connection
        this.startListening();
    }

    /**
     * Handles incoming communication between Controller and DStore or Client.
     */
    public void startListening(){
        try{
            ServerSocket listener = new ServerSocket(this.port);

            // listening for connections
            while (true){
                Socket connector = listener.accept();

                // setting up the connection
                this.setUpConnection(connector);
            }
        }
        catch(Exception e){
            System.out.println("ERROR : Unable to establish connection.");
        }
    }

    /**
     * Sets up a connection between the connector and the controller.
     * @param connector The object connecting the controller
     * @throws Exception Thrown when connection could not be setup.
     */
    public void setUpConnection(Socket connector) throws Exception{

        BufferedReader clientIn = new BufferedReader( new InputStreamReader(connector.getInputStream()));
        clientIn.mark(0); // marking the reader to be able to reset it later

        // getting token of request
        Token request = RequestTokenizer.getToken(clientIn.readLine());

        // Connector is a DStore //
        if(request instanceof JoinToken){
            // Setting up connection to DStore

            System.out.println("A new DStore joined the system on port : " + connector.getPort());
        }
        // Connector is a Client //
        else{
            // Setting up connection to Client
            
            // TODO
        }
    }
}