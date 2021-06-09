package Dstore;

import java.io.File;
import java.net.InetAddress;

import Interface.ServerInterface;
import Logger.*;
import Network.*;

 /**
  * Individual data store unit within the system. 
  *
  * Connects to a Controller to join a data store and servers requests from DSClients
  * and the Controller.
  */
public class Dstore extends Server{

    // member variables
    private int port;
    private int cPort;
    private int timeout;
    private String folderPath;
    private File fileStore;
    private ServerThread controllerThread;
    private ServerInterface dstoreInterface;

    /**
     * Class constructor.
     * 
     * @param port The port the DStore will listen on.
     * @param cPort The port the Controller that the DStore will connect to is on.
     * @param timeout The timout period for the DStore.
     * @param fileFolder The folder where the DStore will store files.
     */
    public Dstore(int port, int cPort, int timeout, String folderPath, ServerInterface dstoreInterface){
        // initializing member variables
        super(ServerType.DSTORE, port, dstoreInterface);
        this.port = port;
        this.cPort = cPort;
        this.timeout = timeout;
        this.folderPath = folderPath;
        this.dstoreInterface = dstoreInterface;
        this.setRequestHandler(new DstoreRequestHandler(this));
    }
    
    /**
     * Sets up the Dstore ready for use.
     * 
     * Creates the logger, connects to controller and creates file store.
     */
    public void setup() throws Exception{
        try{
            // creating the terminal logger
            this.getServerInterface().createLogger();
        }
        catch(Exception e){
            throw new Exception("Unable to create Dstore Logger on port : " + this.getPort(), e);
        }

        // connecting to controller
        try{
            // connecting to controller
            this.connectToController();

            // setting up file storage folder
            this.setupFileStore(this.folderPath);
        }
        catch(Exception e){
            throw new Exception("Unable to connect DStore on port : " + this.port + " to controller on port : " + this.cPort);
        }
    }

    /** 
     * Sets up a connection between the DStore and the Controller.
     */
    public void connectToController() throws Exception{
        // creating communicatoin channel
        Connection connection = new Connection(this.getServerInterface(), InetAddress.getLocalHost(), this.cPort);
        this.controllerThread = new ServerThread(this, connection);
        this.controllerThread.start();

        // sending JOIN message to Controller
        String message = Protocol.JOIN_TOKEN + " " + this.getPort();
        this.controllerThread.getConnection().sendMessage(message);
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
        if(!this.fileStore.exists()){
            this.fileStore.mkdir();
        }
    }

    /**
     * Handles the disconnection of a Connector at the specified port.
     * 
     * Only thing to do is pass the error onto the underlying interface to handle.
     * 
     * @param port The port of the connector.
     */
    public void handleDisconnect(int port, Exception cause){
        // logging disconnect
        this.getServerInterface().handleError("A connector on port : " + port + " has disconnected.", cause);
    }


    /////////////////////////
    // GETTERS AND SETTERS //
    /////////////////////////

    public int getPort(){
        return this.port;
    }

    public int getCPort(){
        return this.cPort;
    }

    public int getTimeout(){
        return this.timeout;
    }

    public String getFolderPath(){
        return this.folderPath;
    }

    public File getFileStore(){
        return this.fileStore;
    }

    public ServerThread getControllerThread(){
        return this.controllerThread;
    }
}