package Dstore;

import java.io.File;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import Logger.*;
import Server.*;

 /**
  * Individual data store unit within the system. Connects to a Controller to join a data store
  * and servers requests from clients.
  */
public class Dstore extends Server{

    // member variables
    private int port;
    private int cPort;
    private double timeout;
    private File fileStore;
    private DstoreControllerConnection controllerConnection;
    private DstoreLogger logger;

    /**
     * Class constructor.
     * 
     * @param port The port the DStore will listen on.
     * @param cPort The port the Controller that the DStore will connect to is on.
     * @param timeout The timout period for the DStore.
     * @param fileFolder The folder where the DStore will store files.
     */
    public Dstore(int port, int cPort, double timeout, String folderPath){
        // initializing member variables
        this.port = port;
        this.cPort = cPort;
        this.timeout = timeout;

        // creating logger
        try{
            DstoreLogger.init(Logger.LoggingType.ON_TERMINAL_ONLY, this.port);
        }
        catch(Exception e){
            MyLogger.logError("Cannot create DStore Logger for DStore on port : " + this.port);
        }

        // setting up file storage folder
        this.setupFileStore(folderPath);
        
        // connecting to the controller
        this.connectToController();

        // waiting for client connection
        this.startListening();
    }

    /**
     * Makes sure the DStores file store is ready to use by creating a directory
     * if one doesnt already exist.
     * 
     * @param folderPath The file store directory.
     */
    public void setupFileStore(String folderPath){
        // creating file object
        this.fileStore = new File(folderPath);

        // creating the file if it doesnt exist
        if(!fileStore.exists()){
            fileStore.mkdir();
        }
    }

    /** 
     * Sets up a connection between the DStore and the Controller.
     */
    public void connectToController(){
        try{
            // creating socket for communication
            Socket socket = new Socket(InetAddress.getLocalHost(), this.cPort);
            
            // Sending JOIN request to controller
            PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            String message = Protocol.JOIN_TOKEN + " " + this.port;
            out.println(message);
            out.flush(); // closing the stream
            DstoreLogger.getInstance().messageSent(socket, message);
        }
        catch(Exception e){
            MyLogger.logError("DStore on port : " + this.port + " unable to connect to controller on port : " + this.cPort);
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
            MyLogger.logError("DStore on port : " + this.port + " unable to connect to new connector.");
        }
    }

    /**
     * Sets up a connection between the DStore and a connector.
     * 
     * @param connector The object connecting the Dstore and the connector.
     * @throws Exception Thrown when connection could not be setup.
     */
    public void setUpConnection(Socket connector){
        // TODO create a new communication channel between the dstore and the connector
    }

    
    /**
     * Getters and setters
     */

    
    public int getPort(){
        return this.port;
    }

    /////////////////
    // MAIN METHOD //
    /////////////////

    /**
     * Main method - instantiates a new DStore instance using the command line parammeters.
     * 
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
            Dstore dataStore = new Dstore(port, cPort, timeout, fileFolder);
        }
        catch(Exception e){
            MyLogger.logError("Unable to create DStore.");
        }
    }
}