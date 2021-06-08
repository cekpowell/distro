package Token;

import java.util.ArrayList;
import java.util.StringTokenizer;

import Token.TokenType.*;
import Logger.Protocol;

/**
 * Tokenizes message strings.
 */
public class RequestTokenizer {
    
    /**
     * Class constuctor. Private as class is static.
     */
    private RequestTokenizer(){}

    /**
     * Gathers a token from a message string.
     * @param message The message string.
     * @return The gathered token.
     */
    public static Token getToken(String message){
        StringTokenizer sTokenizer = new StringTokenizer(message); // tokenizer splits string up based on spaces

        if(!(sTokenizer.hasMoreTokens())){
            return null;
        }

        String firstToken = sTokenizer.nextToken();

        // ACK //
        if(firstToken.equals(Protocol.ACK_TOKEN)) {
            return new AckToken(message);
        }

        // STORE //
        else if(firstToken.equals(Protocol.STORE_TOKEN)){
            return getStoreToken(message, sTokenizer);
        }

        // STORE_TO //
        else if(firstToken.equals(Protocol.STORE_TO_TOKEN)){
            return getStoreToToken(message, sTokenizer);
        }

        // STORE_ACK //
        else if(firstToken.equals(Protocol.STORE_ACK_TOKEN)){
            return getStoreAckToken(message, sTokenizer);
        }

        // STORE_COMPLETE //
        else if(firstToken.equals(Protocol.STORE_COMPLETE_TOKEN)){
            return new StoreCompleteToken(message);
        }

        // LOAD //
        else if(firstToken.equals(Protocol.LOAD_TOKEN)){
            return getLoadToken(message, sTokenizer);
        }

        // LOAD_FROM //
        else if (firstToken.equals(Protocol.LOAD_FROM_TOKEN)){
            return getLoadFromToken(message, sTokenizer);
        }

        // LOAD_DATA //
        else if(firstToken.equals(Protocol.LOAD_DATA_TOKEN)){
            return getLoadDataToken(message, sTokenizer);
        }

        // RELOAD //
        else if(firstToken.equals(Protocol.RELOAD_TOKEN)){
            return getReloadToken(message, sTokenizer);
        }

        // REMOVE //
        else if (firstToken.equals(Protocol.REMOVE_TOKEN)){
            return getRemoveToken(message, sTokenizer);
        }

        // REMOVE_ACK //
        else if(firstToken.equals(Protocol.REMOVE_ACK_TOKEN)){
            return getRemoveAckToken(message, sTokenizer);
        }

        // REMOVE_COMPLETE //
        else if(firstToken.equals(Protocol.REMOVE_COMPLETE_TOKEN)){
            return new RemoveCompleteToken(message);
        }

        // LIST //
        else if(firstToken.equals(Protocol.LIST_TOKEN)){
            return getListToken(message, sTokenizer);
        }

        // JOIN //
        else if (firstToken.equals(Protocol.JOIN_TOKEN)){
            return getJoinToken(message, sTokenizer);
        }

        // REBALANCE //
        else if (firstToken.equals(Protocol.REBALANCE_TOKEN)){
            return getRebalanceToken(message, sTokenizer);
        }

        // REBALANCE_STORE //
        else if (firstToken.equals(Protocol.REBALANCE_STORE_TOKEN)){
            return getRebalanceStoreToken(message, sTokenizer);
        }

        // REBALANCE_COMPLETE //
        else if(firstToken.equals(Protocol.REBALANCE_COMPLETE_TOKEN)){
            return new RebalanceCompleteToken(message);
        }

        // ERROR_NOT_ENOUGH_DSTORES //
        else if(firstToken.equals(Protocol.ERROR_NOT_ENOUGH_DSTORES_TOKEN)){
            return new ErrorNotEnoughDStoresToken(message);
        }

        // ERROR_FILE_ALREADY_EXISTS //
        else if(firstToken.equals(Protocol.ERROR_FILE_ALREADY_EXISTS_TOKEN)){
            return new ErrorFileAlreadyExistsToken(message);
        }

        // ERROR_FILE_DOES_NOT_EXIST //
        else if(firstToken.equals(Protocol.ERROR_FILE_DOES_NOT_EXIST_TOKEN)){
            return getErrorFileDoesNotExistToken(message, sTokenizer);
        }

        // ERROR_LOAD //
        else if(firstToken.equals(Protocol.ERROR_LOAD_TOKEN)){
            return new ErrorLoadToken(message);
        }

        // Unrecognized //
        else{
            return new InvalidRequestToken(message);
        }
    }

    /**
     * Gathers a STORE token from a message string.
     * @param message
     * @param sTokenizer
     * @return
     */
    private static Token getStoreToken(String message, StringTokenizer sTokenizer) {
        try{
            String filename = sTokenizer.nextToken();
            int filesize = Integer.parseInt(sTokenizer.nextToken());
            return new StoreToken(message, filename, filesize);
        }
        catch(Exception e){
            return new InvalidRequestToken(message);
        }
    }

    /**
     * Gathers a STORE_TO token from a message string.
     * @param message
     * @param sTokenizer
     * @return
     */
    private static Token getStoreToToken(String message, StringTokenizer sTokenizer) {
        ArrayList<Integer> ports = new ArrayList<Integer>();
        
        try{
            while(sTokenizer.hasMoreTokens()){
                int port = Integer.parseInt(sTokenizer.nextToken());
                ports.add(port);
            }

            return new StoreToToken(message, ports);
        }
        catch(Exception e){
            return new InvalidRequestToken(message);
        }
    }

    /**
     * Gathers a STORE_ACK token from a message string.
     * @param message
     * @param sTokenizer
     * @return
     */
    public static Token getStoreAckToken(String message, StringTokenizer sTokenizer){
        try{
            String filename = sTokenizer.nextToken();

            return new StoreAckToken(message, filename);
        }
        catch(Exception e){
            return new InvalidRequestToken(message);
        }
    }

    /**
     * Gathers a LOAD token from a message string.
     * @param message
     * @param sTokenizer
     * @return
     */
    private static Token getLoadToken(String message, StringTokenizer sTokenizer){
        try{
            String filename = sTokenizer.nextToken();

            return new LoadToken(message, filename);
        }
        catch(Exception e){
            return new InvalidRequestToken(message);
        }
    }

    /**
     * Gathers a LOAD_FROM token from a message string.
     * @param message
     * @param sTokenizer
     * @return
     */
    private static Token getLoadFromToken(String message, StringTokenizer sTokenizer) {
        try{
            int port = Integer.parseInt(sTokenizer.nextToken());

            Double filesize = Double.parseDouble(sTokenizer.nextToken());

            return new LoadFromToken(message, port, filesize);
        }
        catch(Exception e){
            return new InvalidRequestToken(message);
        }
    }

    /**
     * Gathers a LOAD_DATA token from a message string.
     * @param message
     * @param sTokenizer
     * @return
     */
    private static Token getLoadDataToken(String message, StringTokenizer sTokenizer) {
        try{
            String filename = sTokenizer.nextToken();

            return new LoadDataToken(message, filename);
        }
        catch(Exception e){
            return new InvalidRequestToken(message);
        }
    }

    /**
     * Gathers a RELOAD token from a message string.
     * @param message
     * @param sTokenizer
     * @return
     */
    private static Token getReloadToken(String message, StringTokenizer sTokenizer) {
        try{
            String filename = sTokenizer.nextToken();

        return new ReloadToken(message, filename);
        }
        catch(Exception e){
            return new InvalidRequestToken(message);
        }
    }

    /**
     * Gathers a REMOVE token from a message string.
     * @param message
     * @param sTokenizer
     * @return
     */
    private static Token getRemoveToken(String message, StringTokenizer sTokenizer) {
        try{
            String filename = sTokenizer.nextToken();

            return new RemoveToken(message, filename);
        }
        catch(Exception e){
            return new InvalidRequestToken(message);
        }
    }

    /**
     * Gathers a REMOVE_ACK token from a message string.
     * @param message
     * @param sTokenizer
     * @return
     */
    private static Token getRemoveAckToken(String message, StringTokenizer sTokenizer) {
        try{
            String filename = sTokenizer.nextToken();

            return new RemoveAckToken(message, filename);
        }
        catch(Exception e){
            return new InvalidRequestToken(message);
        }
    }

    /**
     * Gathers a LIST token from a message string.
     * @param message
     * @param sTokenizer
     * @return
     */
    private static Token getListToken(String message, StringTokenizer sTokenizer) {
        try{
            if(sTokenizer.hasMoreTokens()){
                ArrayList<String> filenames = new ArrayList<String>();
                while(sTokenizer.hasMoreTokens()){
                    String filename = sTokenizer.nextToken();
    
                    filenames.add(filename);
                }
    
                return new ListFilesToken(message, filenames);
            }
            else if(message.length() == 5){ // ERROR FIX : Case where there are no files but is still a LIST filenames token - just LIST followed by space and therefore has 5 characters
                ArrayList<String> filenames = new ArrayList<String>();
                return new ListFilesToken(message, filenames);
            }
            else{
                return new ListToken(message);
            }
        }
        catch(Exception e){
            return new InvalidRequestToken(message);
        }
    }

    /**
     * Gathers a JOIN token from a message string.
     * @param message
     * @param sTokenizer
     * @return
     */
    private static Token getJoinToken(String message, StringTokenizer sTokenizer) {
        try{
            int port = Integer.parseInt(sTokenizer.nextToken());

            return new JoinToken(message, port);
        }
        catch(Exception e){
            return new InvalidRequestToken(message);
        }
    }

    /**
     * Gathers a REBALANC token from a message string.
     * @param message
     * @param sTokenizer
     * @return
     */
    private static Token getRebalanceToken(String message, StringTokenizer sTokenizer) {
        try{
            
            // Files to send //

            int numberOfFilesToSend = Integer.parseInt(sTokenizer.nextToken());

            ArrayList<FileToSend> filesToSend = new ArrayList<FileToSend>();

            for(int i = 0; i < numberOfFilesToSend; i++){
                String filename = sTokenizer.nextToken();

                int numberOfDStores = Integer.parseInt(sTokenizer.nextToken());

                ArrayList<Integer> ports = new ArrayList<Integer>();

                for(int j = 0; j < numberOfDStores; j++){
                    int port = Integer.parseInt(sTokenizer.nextToken());

                    ports.add(port);
                }

                FileToSend fileToSend = new FileToSend(filename, ports);
                filesToSend.add(fileToSend);
            }

            // Files to remove //

            int numberOfFilesToRemove = Integer.parseInt(sTokenizer.nextToken());

            ArrayList<String> filesToRemove = new ArrayList<String>();

            for(int i = 0; i < numberOfFilesToRemove; i++){
                String filename = sTokenizer.nextToken();

                filesToRemove.add(filename);
            }

            return new RebalanceToken(message, filesToSend, filesToRemove);
        }
        catch(Exception e){
            return new InvalidRequestToken(message);
        }
    }

    /**
     * Gathers a REBALANCE_STORE token from a message string.
     * @param message
     * @param sTokenizer
     * @return
     */
    private static Token getRebalanceStoreToken(String message, StringTokenizer sTokenizer) {
        try{
            String filename = sTokenizer.nextToken();
            Double filesize = Double.parseDouble(sTokenizer.nextToken());
            return new RebalanceStoreToken(message, filename, filesize);
        }
        catch(Exception e){
            return new InvalidRequestToken(message);
        }
    }

    /**
     * Gathers an ERROR_FILE_DOES_NOT_EXIST token from a message string.
     * @param message
     * @param sTokenizer
     * @return
     */
    private static Token getErrorFileDoesNotExistToken(String message, StringTokenizer sTokenizer) {
        if(sTokenizer.hasMoreTokens()){
            String filename = sTokenizer.nextToken();

            return new ErrorFileDoesNotExistFilenameToken(message, filename);
        }
        else{
            return new ErrorFileDoesNotExistToken(message);
        }
    }
}