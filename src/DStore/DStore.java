package DStore;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import Logger.*;

 /**
  * 
  */
public class DStore {

    // member variables
    private int port;
    private int cPort;
    private double timeout;
    private String fileFolder;
    private DStoreControllerConnection controllerConnection;
    private DstoreLogger logger;

    /**
     * Class constructor.
     * @param port
     * @param cPort
     * @param timeout
     * @param fileFolder
     */
    public DStore(int port, int cPort, double timeout, String fileFolder){
        // initializing member variables
        this.port = port;
        this.cPort = cPort;
        this.timeout = timeout;
        this.fileFolder = fileFolder;

        try{
            DstoreLogger.init(Logger.LoggingType.ON_TERMINAL_ONLY, this.port);
        }
        catch(Exception e){
            ErrorLogger.logError("Cannot create DStore Logger for DStore on port : " + this.port);
        }
        

        // connecting to the controller
        this.connectToController();

        // waiting for client connection
        this.startListening();
    }

    /** 
     * Sets up a connection between the DStore and the Controller.
     */
    public void connectToController(){
        try{
            // creating socket for communication
            Socket socket = new Socket(InetAddress.getLocalHost(), this.cPort);
            
            // creating communication channel
            this.controllerConnection = new DStoreControllerConnection(this, socket);
            this.controllerConnection.start();
        }
        catch(Exception e){
            // handling error
            ErrorLogger.logError("DStore on port : " + this.port + " unable to connect to controller on port : " + this.cPort);
        }
    }

    /**
     * Handles incoming communication to the data store from a client.
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
            ErrorLogger.logError("DStore on port : " + this.port + " unable to connect to new client.");
        }
    }

    /**
     * Sets up a connection between the DStore and a Client.
     * @param connector The object connecting the controller
     * @throws Exception Thrown when connection could not be setup.
     */
    public void setUpConnection(Socket connector) throws Exception{
        // TODO create a new communication channel between the dstore and the client
    }

    /**
     * Getters and setters
     */

    
    public int getPort(){
        return this.port;
    }

    /**
     * Main method - instantiates a new DStore instance using the command line parammeters.
     * @param args Parameters for the new DStore.
     */
    public static void main(String[] args){
        try{
            // gathering parameters
            int port = Integer.parseInt(args[0]);
            int cPort = Integer.parseInt(args[1]);
            double timeout = Double.parseDouble(args[2]);
            String fileFolder = args[3];

            // Creating new DStore instance
            DStore dataStore = new DStore(port, cPort, timeout, fileFolder);
        }
        catch(Exception e){
            ErrorLogger.logError("Unable to create DStore.");
        }
    }
}