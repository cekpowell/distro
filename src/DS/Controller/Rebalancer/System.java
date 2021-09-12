package DS.Controller.Rebalancer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import DS.Protocol.Token.TokenType.FileToSend;

/**
 * Stores the contents of the Distributed File Storage System.
 * 
 * The contents of the distributed File Storage System include the 
 * minimum number of Dstores that each file must be replicated over
 * and the mapping of files across Dstores (file distribution).
 */
public class System{

    // member variables
    private int minDstores;
    private HashMap<Integer, HashMap<String, Integer>> fileDistribution;

    /**
     * Class constructor.
     * 
     * @param minDstores The minimum number of Dstores that each file must be replicated
     * over.
     * @param fileDistribution The distribution of files across Dstores within the System.
     */
    public System(int minDstores, HashMap<Integer, HashMap<String, Integer>> fileDistribution){
        // initializing
        this.minDstores = minDstores;
        this.fileDistribution = fileDistribution;
    }

    ///////////////////////////////////
    // CONFIGURING FILE DISTRIBUTION //
    ///////////////////////////////////

    /**
     * Adds the provided file to the provided Dstore within the file 
     * distribution.
     * 
     * @param dstore The Dstore the file is being added to.
     * @param file The file being added to the Dstore.
     */
    public void addFileToDstore(Integer dstore, String file, int filesize){
        // adding the file to the dstore's file list
        this.fileDistribution.get(dstore).put(file, filesize);
    }

    /**
     * Removes the provided file from the provided Dstore within the file
     * distribution.
     * 
     * @param dstore The Dstore the file is being removed from.
     * @param file The file being removed from the Dstore.
     */
    public void removeFileFromDstore(Integer dstore, String file){
        // removing the file from the dstore's file list
        this.fileDistribution.get(dstore).remove(file);
    }

    /**
     * Updates the provided system state to reflect the changes made in the provided
     * rebalance information.
     * 
     * @param rebalanceInformation The description of the changes made to the system state.
     */
    public void updateFromRebalanceInformation(HashMap<Integer, RebalanceInformation> rebalanceInformation){
        // dealing with rebalance information for each dstore
        for(Integer dstore : rebalanceInformation.keySet()){

            // DEALING WITH FILES SENT //

            for(FileToSend fileToSend : rebalanceInformation.get(dstore).getFilesToSend()){
                for(int dstorePort : fileToSend.dStores){
                    this.addFileToDstore(dstorePort, fileToSend.filename, fileToSend.filesize);
                }
            }

            // DEALING WITH FILES REMOVED //
            
            for(String fileToRemove : rebalanceInformation.get(dstore).getFilesToRemove()){
                // removing the file from the file distribution
                this.removeFileFromDstore(dstore, fileToRemove);
            }
        }
    }

    //////////////////////////
    // CHECKING IF BALANCED //
    //////////////////////////

    /**
     * Determines if the System is balanced.
     * 
     * The System is balanced if all files are replicated the minimum
     * number of times, and if files are stored evenly across Dstores.
     * 
     * @return True if the system is balanced, false if not.
     */
    public boolean isBalanced(){
        return (this.filesStoredMinTimes() && this.filesStoredEvenly());
    }

    /**
     * Determines if all files within the system are replicated the 
     * minimum number of times.
     * 
     * @return True if all files are replicated the minnimum number of times, 
     * false if not.
     */
    public boolean filesStoredMinTimes(){
        // if files are stored r times, then there are no files not stored R times (duh...)
        return (this.getFilesNotStoredMinTimes().size() == 0);
    }

    /**
     * Determines if the files within the system are stored evenly across the Dstores.
     * 
     * Files are stored evenly if each Dstore stores between Floor(R * F / N) and 
     * Ceiling(R * F / N) files, where R is the replication factor, F is the number
     * of files, and N is the number of Dstores (i.e., each Dstore stores the average
     * amount of files).
     * 
     * @return True if the files are stored evenly, false if not.
     */
    public boolean filesStoredEvenly(){
        // calculating min and max values
        double r = this.minDstores;
        double f = this.getNumberOfFiles();
        double n = this.getNumberOfDstores();
        double averageFiles = r * f / n;
        double minFiles = Math.floor(averageFiles);
        double maxFiles = Math.ceil(averageFiles);

        // looping over dstores and checking the number of files they store
        for(int dstoreCount : this.getDstoreFileCount().values()){
            if(dstoreCount < minFiles || dstoreCount > maxFiles){
                return false;
            }
        }

        // not returned false - file spread is good - returning true
        return true;
    }

    ///////////////////////////
    // DSTORE HELPER METHODS //
    ///////////////////////////

    /**
     * Returns a list of all Dstores in the file distribution.
     * 
     * @return A list of all Dstores in the file distribution as a list
     * of port numbers.
     */
    public ArrayList<Integer> getDstores(){
        return new ArrayList<Integer>(this.fileDistribution.keySet());
    }

    /**
     * Returns the number of Dstores within the System.
     * 
     * @return The number of Dstores within the System.
     */
    public int getNumberOfDstores(){
        return this.fileDistribution.size();
    }

    /**
     * Returns a Dstore that stores the provided file.
     * 
     * @param filename The name of the file for which a Dstore is being 
     * searched for.
     * @return A Dstore that stores the provided file, or null if no
     * matching Dstore was found.
     */
    public Integer getDstoreThatHasFile(String file){
        // finding dstore that stores the file
        for(Integer dstore : this.getDstores()){
            if(this.getFilesOnDstore(dstore).keySet().contains(file)){
                return dstore;
            }
        }

        // no dstore found - returning null
        return null;
    }

    /**
     * Returs a mapping of Dstores to the number of files they store.
     * 
     * @return A mapping of Dstores to the number of files they store.
     */
    public HashMap<Integer, Integer> getDstoreFileCount(){
        // map to store the count for each Dstore
        HashMap<Integer, Integer> dstoreCount = new HashMap<Integer, Integer>();

        // populating the dstore count
        for(Integer dstore : this.fileDistribution.keySet()){
            dstoreCount.put(dstore, this.fileDistribution.get(dstore).keySet().size());
        }

        // returning the dstore count
        return dstoreCount;
    }

    /**
     * Returns a list of all Dstores in the System, sorted by the number of files they 
     * contain, in ascending order.
     * 
     * @return A list of all Dstores in the System, sorted according to the number of files 
     * they contain.
     */
    public ArrayList<Integer> getDstoresSortedByFiles(){
        // creating mapping for sorting (cannot sort a map of object -> map, but can sort map of object -> list)
        HashMap<Integer, ArrayList<String>> dstoresList = new HashMap<Integer, ArrayList<String>>();
        for(Integer dstore : this.fileDistribution.keySet()){
            dstoresList.put(dstore, new ArrayList<String>(this.fileDistribution.get(dstore).keySet()));
        }

        // getting list of dstores in order of files they store
        List<Integer> sortedDstoresList = dstoresList.entrySet().stream()
            .collect(
                    Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> entry.getValue().size()
                        ) // get a map where key=original key and value list size
            )
            .entrySet()
            .stream() // sort map by value - list size
            .sorted(Map.Entry.<Integer, Integer>comparingByValue())
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());

        // returning the sorted list of dstores
        return new ArrayList<Integer>(sortedDstoresList);
    }

    /////////////////////////
    // FILE HELPER METHODS //
    /////////////////////////

    /**
     * Returns a list of all files in the System as a mapping of their 
     * filename to filesize.
     * 
     * @return A mapping of filename to filesize for all files in the system.
     */
    public HashMap<String, Integer> getFiles(){
        // hashmap to hold the list of files
        HashMap<String, Integer> files = new HashMap<String, Integer>();

        // iterating over dstores and adding each ones files to the map
        for(HashMap<String, Integer> dstoreFiles : this.fileDistribution.values()){
            files.putAll(dstoreFiles);
        }

        // returning the created file list
        return files;
    }

    /**
     * Returns a list of all files stored in the System.
     * 
     * @return All files stored in the system as a list of filenames.
     */
    public ArrayList<String> getFileNames(){
        return new ArrayList<String>(this.getFiles().keySet());
    }

    /**
     * Returns the number of files stored in the System.
     * 
     * @return The number of file stored in the System.
     */
    public int getNumberOfFiles(){
        // list to hold all files
        ArrayList<String> files = new ArrayList<String>();

        // iterating through dstordes and adding files to list
        for(Integer dstore : this.fileDistribution.keySet()){
            for(String file : this.fileDistribution.get(dstore).keySet()){
                // only adding file if it's not yet been added
                if(!files.contains(file)){
                    files.add(file);
                }
            }
        }

        // returning number of files
        return files.size();
    }

    /**
     * Returns the list of files stored on the provided Dstore.
     * 
     * @param dstore The Dstore the list of files is being gathered for.
     * @return A list of the files stored on the provided Dstore as a mapping
     * of filename to filesize.
     */
    public HashMap<String, Integer> getFilesOnDstore(Integer dstore){
        return this.fileDistribution.get(dstore);
    }

    /**
     * Returns the filesize for the provided file. 
     * 
     * Returns -1 if there is no record of the file in the System.
     * 
     * @return The filesize of the provided file, or -1 if the file is not 
     * found in the file distribution.
     */
    public int getFileSize(String file){
        // setting initial value for the filesize
        int filesize = -1;

        // iterating through the file distributio to find the file
        for(Integer dstore : this.fileDistribution.keySet()){
            for(String f : this.fileDistribution.get(dstore).keySet()){
                if(f.equals(file)){
                    filesize = this.fileDistribution.get(dstore).get(f);
                }
            }
        }

        // returning the filesize
        return filesize;
    }
    
    /**
     * Returns a mapping of files to the number of Dstores they are replicated
     * over.
     * 
     * @return A list of files in the system mapped to the number of Dstores the
     * files are replicated across.
     */
    public HashMap<String, Integer> getFileCount(){
        // map to hold all mappings
        HashMap<String, Integer> fileCount = new HashMap<String, Integer>();

        // iterating through dstordes and adding files to list
        for(Integer dstore : this.fileDistribution.keySet()){
            for(String file : this.fileDistribution.get(dstore).keySet()){
                // file not yet added - has a count of one
                if(!fileCount.keySet().contains(file)){
                    fileCount.put(file, 1);
                }
                // file already added - increment count by 1
                else{
                    fileCount.put(file, fileCount.get(file) + 1);
                }
            }
        }

        // returning number of files
        return fileCount;
    }

    /**
     * Returs a list of files that are not replicated over the minimum number
     * of Dstoed as a mapping of filename to the number of times they ARE stored.
     * 
     * @return A mapping of filenames to the number of times they are stored, for all files
     * that are not stored the minimum number of times.
     */
    public HashMap<String, Integer> getFilesNotStoredMinTimes(){
        // gettinng mapping of files to the number of times they are stored
        HashMap<String, Integer> allFilesCount = this.getFileCount();

        // creating map for files that are not stored R times
        HashMap<String, Integer> filesNotStoredMinTimes = new HashMap<String, Integer>();

        // iterating through files and adding those that are not stored R times to the list
        for(String file : allFilesCount.keySet()){
            if(allFilesCount.get(file) < this.minDstores){
                filesNotStoredMinTimes.put(file, allFilesCount.get(file));
            }
        }

        // returning the list of files that are not stored r times
        return filesNotStoredMinTimes;
    }

    /////////////////////////
    // GETTERS AND SETTERS //
    /////////////////////////

    public int getMinDstores(){
        return this.minDstores;
    }

    public HashMap<Integer, HashMap<String, Integer>> getFileDistribution(){
        return this.fileDistribution;
    }

    public String toString(){
        return this.fileDistribution.toString();
    }
}