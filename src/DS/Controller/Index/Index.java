package DS.Controller.Index;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import DS.Controller.Controller;
import DS.Controller.Index.State.OperationState;
import DS.Protocol.Exception.*;
import Network.Connection;
import Network.Client.Client.ClientType;
import Network.Protocol.Event.ServerConnectionEvent;

/**
 * Object that manages an Index model.
 * 
 * The Index keeps track of the Dstores currently connected to the
 * controller, the files stored on these Dstores and their corresponding
 * states. The Controller interacts with the Index to make changes to the system
 * as requests come in from Clients.
 * 
 * Methods are syncrhonized and properties are volatile to support concurrent access
 * that may occur as the Controller serves requests from multiple Clients concurrently.
 */
public class Index {

    // member variables
    private Controller controller;
    private volatile CopyOnWriteArrayList<Connection> clients;
    private volatile ConcurrentHashMap<Connection, Integer> clientHeartbeats; // heartbeat connection -> controller-client connection end port
    private volatile CopyOnWriteArrayList<DstoreIndex> dstores;
    private volatile int minDstores;
    private volatile ConcurrentHashMap<Connection, ConcurrentHashMap<String, CopyOnWriteArrayList<Integer>>> loadRecord;

    /**
     * Class constructor.
     * 
     * Creates a new Index instance to be managed.
     * @param controller The Controller instance that manages this Index.
     */
    public Index(Controller controller){
        this.controller = controller;
        this.clients = new CopyOnWriteArrayList<Connection>();
        this.clientHeartbeats = new ConcurrentHashMap<Connection,Integer>();
        this.minDstores = controller.getMinDstores();
        this.dstores = new CopyOnWriteArrayList<DstoreIndex>();
        this.loadRecord = new ConcurrentHashMap<Connection, ConcurrentHashMap<String, CopyOnWriteArrayList<Integer>>>();
    }

    /////////////////////////
    // CONFIGURING CLIENTS //
    /////////////////////////

    /**
     * Adds the given Client into the Index.
     * 
     * @param connection The connection to the Client.
     */
    public synchronized void addClient(Connection connection){
        // adding the client to the index
        this.clients.add(connection);

        // logging
        this.controller.handleEvent(new ServerConnectionEvent(ClientType.CLIENT, connection.getPort()));
    }

    /**
     * Adds the given Client Heartbeat to the Index.
     * 
     * @param connection The Connection for the heartbeat.
     * @param clientPort The port of the Client connection the heartbeat is for.
     */
    public synchronized void addClientHeartbeat(Connection connection, int clientPort){
        this.clientHeartbeats.put(connection, clientPort);
    }

    /**
     * Removes the given client from the Index.
     * 
     * @param connection The connection to the Client.
     */
    public synchronized void removeClient(Connection connection){
        this.clients.remove(connection);
    }

    /**
     * Removes the given Client heartbeat from the Index.
     * 
     * @param connection The connection to the Client heartbeat.
     */
    public synchronized void removeClientHeartbeat(Connection connection){
        this.clientHeartbeats.remove(connection);
    }


    //////////////////////////
    // CONFIGURING DSTORES ///
    //////////////////////////


    /**
     * Adds the given Dstore to the index.
     * 
     * @param port The port of the Dstore to be added (listen port).
     * @param connection The connection between the Controller and the Dstore.
     * 
     * @throws DstorePortInUseException If the port of the Dstore is already in use by another Dstore
     */
    public synchronized void addDstore(Integer port, Connection connection) throws DstorePortInUseException{
        // ERROR CHECKING //

        // Dstore Port already in use
        if(this.getDstorePorts().contains(port)){
            throw new DstorePortInUseException(port);
        }

        // adding the dstore to the list of dstores
        this.dstores.add(new DstoreIndex(port, connection));

        // logging
        this.controller.handleEvent(new ServerConnectionEvent(ClientType.DSTORE, port));
    }

    /**
     * Removes the given Dstore from the system.
     * 
     * @param port The port of the Dstore to be removed from the system (listen port).
     */
    public synchronized void removeDstore(Connection dstore){
        // removing the Dstore from the list of Dstores
        this.dstores.remove(this.getIndexFromConnection(dstore));
    }


    //////////
    // LIST //
    //////////

    /**
     * Returns a list of all files stored in the system.
     * 
     * @return ArrayList of all files stored in the system.
     * @throws NotEnoughDstoresException In the case where there are not enough Dstores connected.
     */
    public ArrayList<String> getFileList() throws Exception{
        // ERROR CHECKING //

        // not enough dstores
        if(!this.hasEnoughDstores()){
            throw new NotEnoughDstoresException();
        }

        // getting list of all files
        ArrayList<String> allFiles = new ArrayList<String>();
        for(DstoreIndex dstore : this.dstores){
            for(DstoreFile file: dstore.getFiles()){
                allFiles.add(file.getFilename());
            }
        }

        // removing duplicates and returning
        return new ArrayList<String>(new HashSet<String>(allFiles));
    }


    ///////////////////
    // STORING FILES //
    ///////////////////


    /**
     * Starts the process of adding a given file to the system by adding it to
     * the index.
     * 
     * @param file The name of the file being added.
     * @param filesize The size of the file being added in bytes.
     * @throws NotEnoughDstoresException If there are not enough Dstores connected to the controller to handle the request.
     * @throws FileAlreadyExists If the file being stored already exists in the Index.
     */
    public synchronized ArrayList<Integer> startStoring(String filename, int filesize) throws Exception{
        // ERROR CHECKING //

        // not enough dstores
        if(!this.hasEnoughDstores()){
            throw new NotEnoughDstoresException();
        }

        // file already exists
        if(this.hasFile(filename)){
            throw new FileAlreadyExistsException(filename);
        }

        // ADDING FILE //

        // getting the list of dstores that the file needs to be stored on.
        ArrayList<Integer> dstoresToStoreOn = this.getDstoresToStoreOn();

        // adding dstores the file is stored on to the the index 
        for(Integer port : dstoresToStoreOn){
            // adding the file to the dstore state
            this.getIndexFromPort(port).addFile(filename, filesize);
        }

        // returning the list of dstores the file needs to be stored on
        return dstoresToStoreOn;
    }

    /**
     * Updates the index based on a STORE_ACK that was recieved from the given Dstore.
     * 
     * @param dstore The connection to the Dstore that the STORE_ACK was receieved from.
     * @param filename The filename referenced by the STORE_ACK.
     */
    public synchronized void storeAckRecieved(Connection dstore, String filename){
        // updatiing the dstore index
        this.getIndexFromConnection(dstore).updateFileState(filename, OperationState.STORE_ACK_RECIEVED);
    }

    ///////////////////
    // LOADING FILES //
    ///////////////////

    /**
     * Gathers a Dstore that the provided file should be loaded from.
     * 
     * @param connection The connection to the Client that sent the LOAD request.
     * @param filename The name of the file being requested.
     * @param isReload Boolean representing if this load operation is a LOAD or RELOAD.
     * @return The Dstore the file should be loaded from.
     * @throws NotEnoughDstoresException If there are not enough Dstores connected to the controller to handle the request.
     * @throws FileDoesNotExistException If the file being requested is not stored within the Index.
     * @throws NoValidDstoresException If there are no Dstores left to try to load from (exhausted all possible Dstores).
     */
    public synchronized int getDstoreToLoadFrom(Connection connection, String filename, boolean isReload) throws Exception{

        // ERROR CHECKING //

        // not enough dstores
        if(!this.hasEnoughDstores()){
            throw new NotEnoughDstoresException();
        }

        // file does not exist
        if((!this.hasFile(filename) || !this.fileHasState(filename, OperationState.IDLE))){
            throw new FileDoesNotExistException(filename);
        }

        // GETTING DSTORE //

        // list of all ports
        ArrayList<DstoreIndex> dstores = this.getDstoresStoredOn(filename);

        // load record for the connection
        ConcurrentHashMap<String,CopyOnWriteArrayList<Integer>> fileLoadRecord = this.loadRecord.get(connection);

        // Load record is null (Client never performed a LOAD before)
        if(fileLoadRecord == null){
            // selecting port to load from
            int selectedPort = dstores.get(0).getPort();

            // creating new file load record
            this.loadRecord.put(connection, new ConcurrentHashMap<String,CopyOnWriteArrayList<Integer>>());

            // adding this file to the load record.
            this.loadRecord.get(connection).put(filename, new CopyOnWriteArrayList<Integer>());

            // adding the port to the fileLoadRecord
            this.loadRecord.get(connection).get(filename).add(selectedPort);

            // returning selected
            return selectedPort;
        }
        // Load record is non-null (Client has done performed a LOAD before)
        else{
            // LOAD command
            if(!isReload){
                // placing/replacing the mapping in the load record
                fileLoadRecord.put(filename, new CopyOnWriteArrayList<Integer>());

                // selecting port to load from
                int selectedPort = dstores.get(0).getPort();

                // adding the selected port to the load record
                fileLoadRecord.get(filename).add(selectedPort);

                // returning the selected port
                return selectedPort;
            }
            // RELOAD command
            else{
                // list of attempted ports
                CopyOnWriteArrayList<Integer> attemptedPorts = fileLoadRecord.get(filename);

                // case where attempted is null
                if(attemptedPorts == null){
                    return dstores.get(0).getPort();
                }

                // finding Dstore that has not already been tried
                for(DstoreIndex dstore : dstores){
                    if(!attemptedPorts.contains(dstore.getPort())){
                        // adding the port to the list of attempted ports
                        attemptedPorts.add(dstore.getPort());

                        // returning the port
                        return dstore.getPort();
                    }
                }

                // throwing Exception if no suitable Dstore is found
                throw new NoValidDstoresException();
            }
        }
    }

    /**
     * Gathers the size of a file stored in the Index.
     * 
     * @param filename The name of the file being searched.
     * @return The size of the searched file in bytes.
     * @throws Exception If the file is not stored in the Index.
     */
    public synchronized int getFileSize(String filename) throws Exception{
        // file exists
        if(this.hasFile(filename)){
            // gathering a dstore the file is stored on
            int port = this.getDstoresStoredOn(filename).get(0).getPort();

            // returning the size of the file
            return this.getIndexFromPort(port).getFile(filename).getFilesize();
        }
        // file does not exist
        else{
            throw new Exception();
        }
    }


    ////////////////////
    // REMOVING FILES //
    ////////////////////


    /**
     * Starts the process of removing a give file from the system by updating the system index.
     * 
     * @param file The file being removed.
     * @throws NotEnoughDstoresException If there are not enough Dstores connected to the controller to handle the request.
     * @throws FileDoesNotExistException If the file being requested is not stored within the Index.
     */
    public synchronized ArrayList<Connection> startRemoving(String filename) throws Exception{

        // ERROR CHECKING //

        // not enough dstores
        if(!this.hasEnoughDstores()){
            throw new NotEnoughDstoresException();
        }

        // file does not exist
        if((!this.hasFile(filename) || !this.fileHasState(filename, OperationState.IDLE))){
            throw new FileDoesNotExistException(filename);
        }

        // getting the list of dstores the file is stored on
        ArrayList<DstoreIndex> dstores = this.getDstoresStoredOn(filename);
        ArrayList<Connection> connections = new ArrayList<Connection>();

        // updating the states of the dstores
        for(DstoreIndex dstore : dstores){
            // updating the dstore state
            dstore.updateFileState(filename, OperationState.REMOVE_IN_PROGRESS);

            // adding the connection to the list
            connections.add(dstore.getConnection());
        }

        // returning the dstores the file is to be removed from
        return connections;
    }

    /**
     * Updates the index after a REMOVE_ACK was recieved.
     * 
     * @param dstore The Connection for the Dstore that the REMOVE_ACK was recieved from.
     * @param filename The name of the file referenced by the REMOVE_ACK.
     */
    public synchronized void removeAckRecieved(Connection dstore, String filename){
        // updating the dstore index
        this.getIndexFromConnection(dstore).updateFileState(filename, OperationState.REMOVE_ACK_RECIEVED);
    }

    //////////////////////////////////////
    // WAITING FOR OPERATION COMPLETION //
    //////////////////////////////////////

    /**
     * Waits for the state of the given file across all Dstores to match the provided expected state. Will
     * only wait for the provided amount of time.
     * 
     * @param filename The name of the file being tracked.
     * @param expectedState The expected state of the file.
     * @param timeout The timeout for the tracking.
     * @return True if the operation completed, false if not.
     * @throws OperationTimeoutException When the state of the file does not match the expected state within the timeout.
     */
    public boolean waitForFileState(String filename, OperationState expectedState, int timeout) throws Exception{

        // Waiting for File to have State //
        
        long timeoutStamp = System.currentTimeMillis() + timeout;

        while(!this.fileHasState(filename, expectedState)){
            if(System.currentTimeMillis() < timeoutStamp){
                Thread.onSpinWait();
            }
            else{
                // timeout occured
                this.handleOperationTimeout(filename, expectedState);

                // throwing exception
                throw new OperationTimeoutException();
            }
        }

        // Operation Complete //

        this.handleOperationComplete(filename, expectedState);

        // returning true
        return true;
    }

    /**
     * Updates the index to reflect an operation having been completed.
     * 
     * @param filename The name of the file that the operation was completed on.
     * @param stateFileIsIn The state that the file is in now that the operation has completed.
     */
    private synchronized void handleOperationComplete(String filename, OperationState stateFileIsIn){
        
        // STORE 
        if(stateFileIsIn == OperationState.STORE_ACK_RECIEVED){
            // updating file state to the new state
            for(DstoreIndex dstore : this.dstores){
                for(DstoreFile file : dstore.getFiles()){
                    if(file.getFilename().equals(filename)){
                        dstore.updateFileState(filename, OperationState.IDLE);
                    }
                }
            }
        }

        // REMOVE
        else if(stateFileIsIn == OperationState.REMOVE_ACK_RECIEVED){
            // removing the file from the index
            for(DstoreIndex dstore : this.dstores){
                dstore.removeFile(filename);
            }
        }
    }

    /**
     * Handles the case where an operation did not complete wthin the given timeout.
     * 
     * @param filename The filename for which the operation did not complete.
     * @param expectedState The state the file should have been in if the operation had compeleted.
     */
    private synchronized void handleOperationTimeout(String filename, OperationState expectedState){
        // STORE
        if(expectedState == OperationState.STORE_ACK_RECIEVED){
            // removing the file from the index
            for(DstoreIndex dstore : this.getDstoresStoredOn(filename)){
                dstore.removeFile(filename);
            }
        }

        // REMOVE
        if(expectedState == OperationState.REMOVE_ACK_RECIEVED){
            // removing the file from the index
            for(DstoreIndex dstore : this.dstores){
                dstore.removeFile(filename);
            }
        }
    }


    /////////////////
    // REBALANCING //
    /////////////////


    /**
     * Starts the process of rebalancing by updating the rebalance index to
     * REBALANCE_IN_PROGRESS.
     */
    public void startRebalancing(){
        // TODO
    }

    /**
     * Waits for all REBALANCE_COMPLETE messages to be recieved for a given file within the 
     * given timeout.
     * 
     * If not all REBALANCE_COMPLETE messages are recieved, the Dstores that did not send them
     * are considered to have disconnected and so are removed from the system. A TimeoutException is
     *  also thrown in this case.
     * 
     * @param timeout The timeout to wait for the REBALANCE_COMPELTE messages to be recieved.
     * @throws OperationTimeoutException Thrown if not all REBALANCE_COMPLETE messages were recieved within
     * the timeout.
     */
    public boolean waitForRebalanceComplete(int timeout) throws OperationTimeoutException{
        // TODO
        return false;
    }


    //////////////////////
    // HELPER FUNCTIONS //
    //////////////////////

    /**
     * Determines if the index has enough Dstores connectedd to it.
     * 
     * @return True if there are enough Dstores, false otherwise.
     */
    private synchronized boolean hasEnoughDstores(){
        if(this.dstores.size() < this.minDstores){
            // not enough dstores
            return false;
        }
        else{
            // enough dstores
            return true;
        }
    }

    /**
     * Determines if the given file is stored on the system.
     * 
     * @param filename The file being checked.
     * @return True if the file is on the system, false otherwise
     */
    private synchronized boolean hasFile(String filename){
        for(DstoreIndex dstore : this.dstores){
            if(dstore.hasFile(filename)){
                // file found
                return true;
            }
        }

        // file not found.
        return false;
    }

    /**
     * Gets the DstoreIndex object associated with the provided Dstore Server Port.
     * 
     * @param port The port the Dstore is listening on.
     * @return The DstoreIndex object associated wth the provided port, null if there 
     * was no match.
     */
    public synchronized DstoreIndex getIndexFromPort(int port){
        // findiing the matching DstoreIndex
        for(DstoreIndex dstore : this.dstores){
            if(dstore.getPort() == port){
                return dstore;
            }
        }

        // returning null if no match found
        return null;
    }

    /**
     * Gets the DstoreIndex object assicitaed with the providedd Connection object.
     * 
     * @param connection The connection object between the Dstore and the Controller.
     * @return The DstorerIndex object associated with the provided Connectoin object, null
     * if there was no match.
     */
    public synchronized DstoreIndex getIndexFromConnection(Connection connection){
        // finding the matching DstoreIndex
        for(DstoreIndex dstore : this.dstores){
            if(dstore.getConnection().getPort() == connection.getPort()){
                return dstore;
            }
        }

        // returning null if no match found
        return null;
    }

    /**
     * Gets a list of Dstores that a file can be stored on. Returns only Dstores
     * that will remain balanced after storing the file.
     * 
     * @return The list of Dstore ports that the new file can be stored on.
     */
    private synchronized ArrayList<Integer> getDstoresToStoreOn(){
        // sorting the dstores based on the number of files they contain
        Collections.sort(this.dstores);

        ArrayList<Integer> ports = new ArrayList<Integer>();

        // picking the first r dstores to store on
        for(int i =0; i < this.minDstores; i++){
            ports.add(this.dstores.get(i).getPort());
        }

        // returning the list of dstores
        return ports;
    }

    /**
     * Gathers a list of Indexes which store the provided file.
     * 
     * @param filename The name of the file being searched.
     * @return The list of DstoreIndexes that store the file.
     */
    private synchronized ArrayList<DstoreIndex> getDstoresStoredOn(String filename){
        ArrayList<DstoreIndex> indexes = new ArrayList<DstoreIndex>();
        
        // looping through all dstores and seeing if they contain the file
        for(DstoreIndex dstore : this.dstores){
            if(dstore.hasFile(filename)){
                indexes.add(dstore);
            }
        }

        // file not stored on the system.
        return indexes;
    }

    /**
     * Determines if the given file has the given state across all of the Dstores that
     * it is stored on.
     * 
     * @param filename The file being checked.
     * @param state The state of the file.
     * @return True if the state of the file is the provided state, false if not.
     */
    public synchronized boolean fileHasState(String filename, OperationState state){
        for(DstoreIndex dstore : this.getDstoresStoredOn(filename)){
            for(DstoreFile file : dstore.getFiles()){
                if(file.getFilename().equals(filename) && file.getState() != state){
                    return false;
                }
            }
        }

        return true;
    }

    /////////////////////////
    // GETTERS AND SETTERS //
    /////////////////////////

    public CopyOnWriteArrayList<Connection> getClients(){
        return this.clients;
    }

    public ConcurrentHashMap<Connection, Integer> getClientHeartbeats(){
        return this.clientHeartbeats;
    }

    public CopyOnWriteArrayList<DstoreIndex> getDstores(){
        return this.dstores;
    }

    /**
     * Returns the list of ports for all Dstores on the system.
     * 
     * @return The list of ports for all Dstores on the system.
     */
    public ArrayList<Integer> getDstorePorts(){
        ArrayList<Integer> ports = new ArrayList<Integer>();

        for(DstoreIndex dstore : this.dstores){
            ports.add(dstore.getPort());
        }

        return ports;
    }

    /**
     * Returns a list of all files stored in the system.
     * 
     * @return ArrayList of all files stored in the system.
     */
    private ArrayList<String> getFiles(){
        // getting list of all files
        ArrayList<String> allFiles = new ArrayList<String>();
        for(DstoreIndex dstore : this.dstores){
            for(DstoreFile file: dstore.getFiles()){
                allFiles.add(file.getFilename());
            }
        }

        // removing duplicates and returning
        return new ArrayList<String>(new HashSet<String>(allFiles));
    }
}