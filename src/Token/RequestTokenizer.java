package Token;

import java.util.ArrayList;
import java.util.StringTokenizer;

import Token.TokenType.*;
import Logger.Protocol;

/**
 * Tokenizes request strings.
 */
public class RequestTokenizer {
    
    /**
     * Class constuctor. Private as class is static.
     */
    private RequestTokenizer(){}

    /**
     * Gathers a token from a request string.
     * @param request The request string.
     * @return The gathered token.
     */
    public static Token getToken(String request){
        StringTokenizer sTokenizer = new StringTokenizer(request); // tokenizer splits string up based on spaces

        if(!(sTokenizer.hasMoreTokens())){
            return null;
        }

        String firstToken = sTokenizer.nextToken();

        // ACK //
        if(firstToken.equals(Protocol.ACK_TOKEN)) {
            return new AckToken(request);
        }

        // STORE //
        else if(firstToken.equals(Protocol.STORE_TOKEN)){
            return getStoreToken(request, sTokenizer);
        }

        // STORE_TO //
        else if(firstToken.equals(Protocol.STORE_TO_TOKEN)){
            return getStoreToToken(request, sTokenizer);
        }

        // STORE_ACK //
        else if(firstToken.equals(Protocol.STORE_ACK_TOKEN)){
            return new StoreAckToken(request);
        }

        // STORE_COMPLETE //
        else if(firstToken.equals(Protocol.STORE_COMPLETE_TOKEN)){
            return new StoreCompleteToken(request);
        }

        // LOAD //
        else if(firstToken.equals(Protocol.LOAD_TOKEN)){
            return getLoadToken(request, sTokenizer);
        }

        // LOAD_FROM //
        else if (firstToken.equals(Protocol.LOAD_FROM_TOKEN)){
            return getLoadFromToken(request, sTokenizer);
        }

        // LOAD_DATA //
        else if(firstToken.equals(Protocol.LOAD_DATA_TOKEN)){
            return getLoadDataToken(request, sTokenizer);
        }

        // RELOAD //
        else if(firstToken.equals(Protocol.RELOAD_TOKEN)){
            return getReloadToken(request, sTokenizer);
        }

        // REMOVE //
        else if (firstToken.equals(Protocol.REMOVE_TOKEN)){
            return getRemoveToken(request, sTokenizer);
        }

        // REMOVE_ACK //
        else if(firstToken.equals(Protocol.REMOVE_ACK_TOKEN)){
            return getRemoveAckToken(request, sTokenizer);
        }

        // REMOVE_COMPLETE //
        else if(firstToken.equals(Protocol.REMOVE_COMPLETE_TOKEN)){
            return new RemoveCompleteToken(request);
        }

        // LIST //
        else if(firstToken.equals(Protocol.LIST_TOKEN)){
            return getListToken(request, sTokenizer);
        }

        // JOIN //
        else if (firstToken.equals(Protocol.JOIN_TOKEN)){
            return getJoinToken(request, sTokenizer);
        }

        // REBALANCE //
        else if (firstToken.equals(Protocol.REBALANCE_TOKEN)){
            return getRebalanceToken(request, sTokenizer);
        }

        // REBALANCE_STORE //
        else if (firstToken.equals(Protocol.REBALANCE_STORE_TOKEN)){
            return getRebalanceStoreToken(request, sTokenizer);
        }

        // REBALANCE_COMPLETE //
        else if(firstToken.equals(Protocol.REBALANCE_COMPLETE_TOKEN)){
            return new RebalanceCompleteToken(request);
        }

        // ERROR_NOT_ENOUGH_DSTORES //
        else if(firstToken.equals(Protocol.ERROR_NOT_ENOUGH_DSTORES_TOKEN)){
            return new ErrorNotEnoughDStoresToken(request);
        }

        // ERROR_FILE_ALREADY_EXISTS //
        else if(firstToken.equals(Protocol.ERROR_FILE_ALREADY_EXISTS_TOKEN)){
            return new ErrorFileAlreadyExistsToken(request);
        }

        // ERROR_FILE_DOES_NOT_EXIST //
        else if(firstToken.equals(Protocol.ERROR_FILE_DOES_NOT_EXIST_TOKEN)){
            return getErrorFileDoesNotExistToken(request, sTokenizer);
        }

        // ERROR_LOAD //
        else if(firstToken.equals(Protocol.ERROR_LOAD_TOKEN)){
            return new ErrorLoadToken(request);
        }

        // Unrecognized //
        else{
            return new InvalidRequestToken(request);
        }
    }

    /**
     * Gathers a STORE token from a request string.
     * @param request
     * @param sTokenizer
     * @return
     */
    private static Token getStoreToken(String request, StringTokenizer sTokenizer) {
        String filename = sTokenizer.nextToken();

        try{
            Double filesize = Double.parseDouble(sTokenizer.nextToken());
            return new StoreToken(request, filename, filesize);
        }
        catch(Exception e){
            return new InvalidRequestToken(request);
        }
    }

    /**
     * Gathers a STORE_TO token from a request string.
     * @param request
     * @param sTokenizer
     * @return
     */
    private static Token getStoreToToken(String request, StringTokenizer sTokenizer) {
        ArrayList<Integer> ports = new ArrayList<Integer>();
        
        try{
            while(sTokenizer.hasMoreTokens()){
                int port = Integer.parseInt(sTokenizer.nextToken());
                ports.add(port);
            }

            return new StoreToToken(request, ports);
        }
        catch(Exception e){
            return new InvalidRequestToken(request);
        }
    }

    /**
     * Gathers a LOAD token from a request string.
     * @param request
     * @param sTokenizer
     * @return
     */
    private static Token getLoadToken(String request, StringTokenizer sTokenizer) {
        String filename = sTokenizer.nextToken();

        return new LoadToken(request, filename);
    }

    /**
     * Gathers a LOAD_FROM token from a request string.
     * @param request
     * @param sTokenizer
     * @return
     */
    private static Token getLoadFromToken(String request, StringTokenizer sTokenizer) {
        try{
            int port = Integer.parseInt(sTokenizer.nextToken());

            Double filesize = Double.parseDouble(sTokenizer.nextToken());

            return new LoadFromToken(request, port, filesize);
        }
        catch(Exception e){
            return new InvalidRequestToken(request);
        }
    }

    /**
     * Gathers a LOAD_DATA token from a request string.
     * @param request
     * @param sTokenizer
     * @return
     */
    private static Token getLoadDataToken(String request, StringTokenizer sTokenizer) {
        String filename = sTokenizer.nextToken();

        return new LoadDataToken(request, filename);
    }

    /**
     * Gathers a RELOAD token from a request string.
     * @param request
     * @param sTokenizer
     * @return
     */
    private static Token getReloadToken(String request, StringTokenizer sTokenizer) {
        String filename = sTokenizer.nextToken();

        return new ReloadToken(request, filename);
    }

    /**
     * Gathers a REMOVE token from a request string.
     * @param request
     * @param sTokenizer
     * @return
     */
    private static Token getRemoveToken(String request, StringTokenizer sTokenizer) {
        String filename = sTokenizer.nextToken();

        return new RemoveToken(request, filename);
    }

    /**
     * Gathers a REMOVE_ACK token from a request string.
     * @param request
     * @param sTokenizer
     * @return
     */
    private static Token getRemoveAckToken(String request, StringTokenizer sTokenizer) {
        String filename = sTokenizer.nextToken();

        return new RemoveAckToken(request, filename);
    }

    /**
     * Gathers a LIST token from a request string.
     * @param request
     * @param sTokenizer
     * @return
     */
    private static Token getListToken(String request, StringTokenizer sTokenizer) {
        if(sTokenizer.hasMoreTokens()){
            ArrayList<String> filenames = new ArrayList<String>();
            while(sTokenizer.hasMoreTokens()){
                String filename = sTokenizer.nextToken();

                filenames.add(filename);
            }

            return new ListFilesToken(request, filenames);
        }
        else if(request.length() == 5){ // ERROR FIX : Case where there are no files but is still a LIST filenames token - just LIST followed by space and therefore has 5 characters
            ArrayList<String> filenames = new ArrayList<String>();
            return new ListFilesToken(request, filenames);
        }
        else{
            return new ListToken(request);
        }
    }

    /**
     * Gathers a JOIN token from a request string.
     * @param request
     * @param sTokenizer
     * @return
     */
    private static Token getJoinToken(String request, StringTokenizer sTokenizer) {
        try{
            int port = Integer.parseInt(sTokenizer.nextToken());

            return new JoinToken(request, port);
        }
        catch(Exception e){
            return new InvalidRequestToken(request);
        }
    }

    /**
     * Gathers a REBALANC token from a request string.
     * @param request
     * @param sTokenizer
     * @return
     */
    private static Token getRebalanceToken(String request, StringTokenizer sTokenizer) {
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

            return new RebalanceToken(request, filesToSend, filesToRemove);
        }
        catch(Exception e){
            return new InvalidRequestToken(request);
        }
    }

    /**
     * Gathers a REBALANCE_STORE token from a request string.
     * @param request
     * @param sTokenizer
     * @return
     */
    private static Token getRebalanceStoreToken(String request, StringTokenizer sTokenizer) {
        String filename = sTokenizer.nextToken();

        try{
            Double filesize = Double.parseDouble(sTokenizer.nextToken());
            return new RebalanceStoreToken(request, filename, filesize);
        }
        catch(Exception e){
            return new InvalidRequestToken(request);
        }
    }

    /**
     * Gathers an ERROR_FILE_DOES_NOT_EXIST token from a request string.
     * @param request
     * @param sTokenizer
     * @return
     */
    private static Token getErrorFileDoesNotExistToken(String request, StringTokenizer sTokenizer) {

        if(sTokenizer.hasMoreTokens()){
            String filename = sTokenizer.nextToken();

            return new ErrorFileDoesNotExistFilenameToken(request, filename);
        }
        else{
            return new ErrorFileDoesNotExistToken(request);
        }
    }
}