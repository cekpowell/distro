package DS.Controller.Rebalancer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import DS.Controller.Controller;
import DS.Controller.Index.DstoreIndex;
import DS.Controller.Index.State.RebalanceState;
import DS.Protocol.Protocol;
import DS.Protocol.Event.Rebalance.RebalanceCompleteEvent;
import DS.Protocol.Event.Rebalance.RebalanceFileListGatheredEvent;
import DS.Protocol.Event.Rebalance.RebalanceNotRequiredEvent;
import DS.Protocol.Event.Rebalance.RebalanceStartedEvent;
import DS.Protocol.Exception.RebalanceFailureException;
import DS.Protocol.Token.TokenType.FileToSend;
import Network.Protocol.Exception.NetworkException;

/**
 * Handles the rebalancing of the file system.
 */
public class Rebalancer extends Thread{
    
    // member variables
    private Controller controller;

    /**
     * Class constructor.
     * 
     * @param controller The Controller associated with this Rebalancer.
     */
    public Rebalancer(Controller controller){
        // initializing
        this.controller = controller;
    }

    /**
     * Method run when thread started.
     */
    public void run(){
        // waiting for rebalance
        this.waitForRebalance();
    }

    ///////////////////////////
    // WAITING FOR REBALANCE //
    ///////////////////////////

    /**
     * Continually waits for the 'rebalance period' of time
     * to pass before rebalancing the ssytem.
     */
    private void waitForRebalance(){
        while(controller.isActive()){
            try{
                // waiting for rebalance-period amount of time
                Thread.sleep(this.controller.getRebalancePeriod());

                // rebalance system
                this.rebalance();
            }
            catch(Exception e){
                // handling failure through controller
                this.controller.handleError(new RebalanceFailureException(e));
            }
        }
    }

    ///////////////////////////
    // MAIN REBALANCE METHOD //
    ///////////////////////////

    /**
     * Rebalances the system.
     * 
     * @throws NotEnoughDstoresException If there are not enough Dstores connected
     * to the system to carry out the rebalance operation.
     * @throws RebalanceAlreadyInProgressException If there is already a rebalance 
     * operation in progess.
     * @throws NetworkTimeoutException If the system does not become idle within
     * the timeout.
     * @throws MessageSendException If a message could not be sent through a connection
     * channel.
     */
    public void rebalance() throws NetworkException{

        // event for rebalance starting
        this.controller.handleEvent(new RebalanceStartedEvent());

        // GATHERING FILE LIST //

        // starting rebalance list
        this.controller.getIndex().startRebalanceList();

        // sending LIST requests to dstores
        for(DstoreIndex dstore : this.controller.getIndex().getDstores()){
            dstore.getConnection().sendMessage(Protocol.getListMessage());
        }

        // waiting for all Dstores to respond
        this.controller.getIndex().waitForRebalanceState(RebalanceState.REBALANCE_LIST_RECIEVED, this.controller.getTimeout());
    
        // creating System instance
        System system = new System(this.controller.getMinDstores(), this.controller.getIndex().getFileDistribution());

        // event for file list gathered 
        this.controller.handleEvent(new RebalanceFileListGatheredEvent());

        // CHECKING IF SYSTEM IS BALANCED //

        if(!system.isBalanced()){

            // REBALANCING //

            // starting the moving process
            this.controller.getIndex().startRebalanceMove();

            // calculating the adjustments
            RebalancedSystem rebalancedSystem = Rebalancer.getRebalancedSystem(system);

            // sending rebalance messages
            for(Integer dstore : rebalancedSystem.getRebalanceInformation().keySet()){
                // forming message
                String rebalanceMessage = rebalancedSystem.getRebalanceInformation().get(dstore).getRebalanceMessage();

                // sending message
                this.controller.getIndex().getIndexFromPort(dstore).getConnection().sendMessage(rebalanceMessage);
            }

            // waiting for rebalance complete responses
            this.controller.getIndex().waitForRebalanceState(RebalanceState.REBALANCE_COMPLETE_RECIEVED, this.controller.getTimeout());

            // REBALANCE COMPLETE //

            // updating the index
            this.controller.getIndex().setFileDistribution(rebalancedSystem.getSystem().getFileDistribution());

            // creating event to show rebalance successfull
            this.controller.handleEvent(new RebalanceCompleteEvent());
        }
        else{
            // event for rebalance not required
            this.controller.handleEvent(new RebalanceNotRequiredEvent());
        }
    }

    /////////////////////////////
    // CALCULATING ADJUSTMENTS //
    /////////////////////////////

    /**
     * Creates a RebalancedSystem object for an unbalanced system.
     * 
     * @param system The unbalanced system that is being rebalanced.
     * @return A RebalancedSystem object that contains the rebalanced
     * file distribution and rebalance information.
     */
    private static RebalancedSystem getRebalancedSystem(System system){
        // creating rebalance information object
        HashMap<Integer, RebalanceInformation> rebalanceInformation = new HashMap<Integer, RebalanceInformation>(); 

        // adding a mapping for each connection into the rebalance information object
        for(Integer dstore : system.getDstores()){
            rebalanceInformation.put(dstore, new RebalanceInformation());
        }

        // creating rebalanced system object
        RebalancedSystem rebalancedSystem = new RebalancedSystem(system, rebalanceInformation);

        // FILES OT STORED R TIMES //

        if(!system.filesStoredMinTimes()){
            // rebalancing system for files not stored evenly
            rebalancedSystem = Rebalancer.rebalanceForNotStoredMinTimes(rebalancedSystem);
        }

        // FILES NOT STORED EVENLY //

        if(!system.filesStoredEvenly()){
            // rebalancing system for files not stored evenly
            rebalancedSystem = Rebalancer.rebalanceForNotStoredEvenly(rebalancedSystem);
        }

        return rebalancedSystem;
    }

    /**
     * Rebalances the given system in the case where one or more files are not stored R times.
     * 
     * @param rebalancedSystem The RebalancedSystem object that is being rebalanced due to not
     * all files beign stored R times.
     * @return The RebalancedSystem object that has been updated so that all files are stored
     * R times.
     */
    private static RebalancedSystem rebalanceForNotStoredMinTimes(RebalancedSystem rebalancedSystem){
        // CALCUATING REBALANCE INFORMATION //

        // gathering list of files not stored r times
        HashMap<String, Integer> filesNotStoredMinTimes = rebalancedSystem.getSystem().getFilesNotStoredMinTimes();

        // iterating through files not stored R times
        for(String file : filesNotStoredMinTimes.keySet()){
            int neededDstores = rebalancedSystem.getSystem().getMinDstores() - filesNotStoredMinTimes.get(file);

            // gathering dstores to store the file on
            ArrayList<Integer> dstoresToStoreOn = Rebalancer.getDstoresToSendTo(rebalancedSystem.getSystem(), file, neededDstores);

            // gathering dstore to send the file to the others
            Integer dstoreToSendFrom = rebalancedSystem.getSystem().getDstoreThatHasFile(file);

            // creating file to send object
            FileToSend fileToSend = new FileToSend(file, rebalancedSystem.getSystem().getFileSize(file), dstoresToStoreOn);

            // adding FileToSend object to rebalance information
            rebalancedSystem.addFileToSend(dstoreToSendFrom, fileToSend);
        }

        // RETURNING RESULT //

        return rebalancedSystem;
    }

    /**
     * Rebalances the given system in the case where files are not stored evenly across 
     * the Dstores.
     * 
     * @param rebalancedSystem The RebalancedSystem object that is being rebalanced due to
     * files not being stored evenly across Dstores.
     * @return The RebalancedSystem object that has been updated so that files are stored
     * across dstores evenly.
     */
    private static RebalancedSystem rebalanceForNotStoredEvenly(RebalancedSystem rebalancedSystem){
        // calculating min and max values
        double r = rebalancedSystem.getSystem().getMinDstores();
        double f = rebalancedSystem.getSystem().getNumberOfFiles();
        double n = rebalancedSystem.getSystem().getNumberOfDstores();
        double averageFiles = r * f / n;
        double minFiles = Math.floor(averageFiles);
        double maxFiles = Math.ceil(averageFiles);

        // iterating over dstores
        for(Integer dstore : rebalancedSystem.getSystem().getDstores()){
            
            // DEALING WITH TOO LITTLE FILES //

            while(rebalancedSystem.getSystem().getFilesOnDstore(dstore).size() < minFiles){

                // FINDING FILE THAT CAN BE STOLEN //

                FileOnDstore fileOnDstoreToSteal = Rebalancer.getFileToSteal(rebalancedSystem.getSystem(), dstore);

                // CREATING FILE TO SEND OBJECT //

                FileToSend fileToSend = new FileToSend(fileOnDstoreToSteal.getFilename(), fileOnDstoreToSteal.getFileSize(), new ArrayList<Integer>(List.of(dstore)));

                // UPDATING REBALANCE INFORMATION //

                // the dstore the file is being stolen from must send the file
                rebalancedSystem.addFileToSend(fileOnDstoreToSteal.getDstore(), fileToSend);

                // the dstore the file is being stolen from must remove the file
                rebalancedSystem.addFileToRemove(fileOnDstoreToSteal.getDstore(), fileToSend.filename);
            }

            // DEALING WITH TOO MANY FILES //

            while(rebalancedSystem.getSystem().getFilesOnDstore(dstore).size() > maxFiles){

                // FINDING FILE THAT CAN BE SENT //

                FileOnDstore fileOnDstoreToSend = Rebalancer.getFileToSend(rebalancedSystem.getSystem(), dstore);

                // CREATING FILE TO SEND OBJECT //

                FileToSend fileToSend = new FileToSend(fileOnDstoreToSend.getFilename(), fileOnDstoreToSend.getFileSize(), new ArrayList<Integer>(List.of(fileOnDstoreToSend.getDstore())));

                // UPDATING REBALANCE INFORMATION //

                // the dstore the file is being stolen from must send the file
                rebalancedSystem.addFileToSend(dstore, fileToSend);

                // the dstore the file is being stolen from must remove the file
                rebalancedSystem.addFileToRemove(dstore, fileToSend.filename);
            }
        }

        // RETURNING RESULT //

        return rebalancedSystem;
    }

    ////////////////////
    // HELPER METHODS //
    ////////////////////

    /**
     * Gathers a specified number of Dstores that the provided file can be sent. The file
     * can be sent to any Dstores that it is not already stored on.
     * 
     * @param system The System being rebalanced.
     * @param filename The name of the file being sent to other Dstores.
     * @param neededDstores The number of Dstores the file must be sent to.
     * @return A list of dstores the file can be sent to.
     */
    public static ArrayList<Integer> getDstoresToSendTo(System system, String filename, int neededDstores){
        // getting sorted list of dstores
        ArrayList<Integer> sortedDstores = system.getDstoresSortedByFiles();

        // forming list of dstores that the file can be sent to
        ArrayList<Integer> dstoresToSendTo = new ArrayList<Integer>();
        for(Integer dstore : sortedDstores){
            // only chosing dstores that do not already store the file
            if(!system.getFilesOnDstore(dstore).keySet().contains(filename)){
                // adding dstoe to the list
                dstoresToSendTo.add(dstore);

                // breaking from the loop if the needed amount have been found.
                if(dstoresToSendTo.size() == neededDstores){
                    break;
                }
            }
        }

        // returning the list of dstores
        return dstoresToSendTo;
    }

    /**
     * Finds a Dstore and file within the provided System that can be stolen
     * by the provided Dstore. A file can be stolen if the file is not already
     * contained on the Dstore that is stealing the file.
     * 
     * @param system The system the Dstore stealing a file is contained within.
     * @param dstoreStealing The Dstore stealing the file.
     * @return A FileOnDstore object representing a file on a Dstore that can be stolen
     * by the provided Dstore.
     */
    public static FileOnDstore getFileToSteal(System system, Integer dstoreStealing){
        // sort dstores based on files 
        ArrayList<Integer> sortedDstores = system.getDstoresSortedByFiles();

        // reversing the list - most-to-fewest
        Collections.reverse(sortedDstores);

        // list of the files on the dstore stealing
        ArrayList<String> filesOnDstoreStealing = new ArrayList<String>(system.getFilesOnDstore(dstoreStealing).keySet());

        // finding file that can be stolen (first file on highest ranking dstore that is not already on the dstore stealing)
        for(Integer dstoreToStealFrom : sortedDstores){
            // only picking dstore that is not the one stealing
            if(dstoreToStealFrom != dstoreStealing){
                // iterating over this dstore's files
                for(String fileToSteal : system.getFilesOnDstore(dstoreToStealFrom).keySet()){
                    // finding file that is not on the dstore stealing
                    if(!filesOnDstoreStealing.contains(fileToSteal)){
                        // returning the FileOnDstore
                        return new FileOnDstore(dstoreToStealFrom, fileToSteal, system.getFileSize(fileToSteal));
                    }
                }
            }
        }

        // no suitable file found - returning null
        return null;
    }

    /**
     * Finds a Dstore within the System, and File within the provided Dstore that can be sent
     * to this Dstore. A file can be sent to another Dstore if it is not already containned on
     * the Dstore it is being sent to.
     * 
     * @param system The System the Dstore sending a file is contained within.
     * @param dstoreSending The Dstore sending a file.
     * @return A FileOnDstore object representing a file on the Dstore that can be sent
     * to another Dstore within the System.
     */
    public static FileOnDstore getFileToSend(System system, Integer dstoreSending){
        // sort dstores based on files 
        ArrayList<Integer> sortedDstores = system.getDstoresSortedByFiles();

        // list of the files on the dstore sending
        ArrayList<String> filesOnDstoreSending = new ArrayList<String>(system.getFilesOnDstore(dstoreSending).keySet());

        // finding file that can be stolen (first file on highest ranking dstore that is not already on the dstore stealing)
        for(Integer dstoreToSendTo : sortedDstores){
            // only picking dstore that is not the one stealing
            if(dstoreToSendTo != dstoreSending){
                // list of files on the dstore to send to
                ArrayList<String> filesOnDstoreToSendTo = new ArrayList<String>(system.getFilesOnDstore(dstoreToSendTo).keySet());

                // seeinng if dstore sending has a file that can be send to this dstore
                for(String fileToSend : filesOnDstoreSending){
                    if(!filesOnDstoreToSendTo.contains(fileToSend)){
                        // suitable file found - returning the file on dstore object
                        return new FileOnDstore(dstoreToSendTo, fileToSend, system.getFileSize(fileToSend));
                    }
                }
            }
        }

        // no suitable file found - returning null
        return null;
    }
}