package Dstore;

import Server.*;

/**
 * 
 */
public abstract class DstoreInterface implements ServerInterface{

    /**
     * Starts the Dstore.
     */
    public void startDstore(Dstore dstore){
        try{
            // trying to start the server
            dstore.start();
        }
        catch(Exception e){
            // handling error if server could not be started
            this.logError(e.getMessage());
        }
    }
}