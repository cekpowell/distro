package DS.Protocol;

import java.util.ArrayList;
import java.util.HashMap;

import DS.Protocol.Token.TokenType.FileToSend;

/**
 * Contains all of the messages that can be sent throughout the system and methods to generate them.
 */
public class Protocol {

	// constants
	private final static String SPACE = " ";

	/////////////////////
	// CLIENT MESSAGES //
	///////////////////// 

	// static variables //

	public final static String JOIN_CLIENT_TOKEN = "JOIN_CLIENT";
	public final static String JOIN_CLIENT_HEARTBEAT = "JOIN_CLIENT_HEARTBEAT";
	public final static String LIST_TOKEN = "LIST"; // also from Controller and Dstores
	public final static String STORE_TOKEN = "STORE"; // also from Dstores
	public final static String LOAD_TOKEN = "LOAD";
	public final static String LOAD_DATA_TOKEN = "LOAD_DATA";
	public final static String RELOAD_TOKEN = "RELOAD";
	public final static String REMOVE_TOKEN = "REMOVE"; // also from Controller


	// getter methods //

	public static String getJoinClientMessage(){
		return Protocol.JOIN_CLIENT_TOKEN;
	}

	public static String getJoinClientHeartbeatMessage(int clientPort){
		return (Protocol.JOIN_CLIENT_HEARTBEAT + Protocol.SPACE + clientPort);
	}

	public static String getListMessage(){
		return Protocol.LIST_TOKEN;
	}

	public static String getListOfFilesMessage(HashMap<String, Integer> files){
        if(files.size() == 0){
            return Protocol.LIST_TOKEN + Protocol.SPACE;
        }

        String filesString = "";

        // forming single string
        for(String file : files.keySet()){
            filesString += Protocol.SPACE;
            filesString += file + Protocol.SPACE + files.get(file);
        }
        
		return (Protocol.LIST_TOKEN + filesString);
	}

	public static String getStoreMessage(String filename, int filesize){
		return (Protocol.STORE_TOKEN + Protocol.SPACE + filename + Protocol.SPACE + filesize);
	}

	public static String getLoadMessage(String filename){
		return (Protocol.LOAD_TOKEN + Protocol.SPACE + filename);
	}

	public static String getLoadDataMessage(String filename){
		return (Protocol.LOAD_DATA_TOKEN + Protocol.SPACE + filename);
	}

	public static String getReloadMessage(String filename){
		return (Protocol.RELOAD_TOKEN + Protocol.SPACE + filename);
	}

	public static String getRemoveMessage(String filename){
		return (Protocol.REMOVE_TOKEN + Protocol.SPACE + filename);
	}

	/////////////////////////
	// CONTROLLER MESSAGES //
	/////////////////////////
	
	// static variables //

	public final static String JOIN_ACK_TOKEN = "JOIN_ACK"; // also from Dstores
	public final static String STORE_TO_TOKEN = "STORE_TO";
	public final static String STORE_COMPLETE_TOKEN = "STORE_COMPLETE";
	public final static String LOAD_FROM_TOKEN = "LOAD_FROM";
	public final static String REMOVE_COMPLETE_TOKEN = "REMOVE_COMPLETE";
	public final static String REBALANCE_TOKEN = "REBALANCE";
	public final static String ERROR_DSTORE_PORT_IN_USE_TOKEN = "ERROR_DSTORE_PORT_IN_USE";
	public final static String ERROR_FILE_DOES_NOT_EXIST_TOKEN = "ERROR_FILE_DOES_NOT_EXIST"; // also from Dstores
	public final static String ERROR_FILE_ALREADY_EXISTS_TOKEN = "ERROR_FILE_ALREADY_EXISTS";
	public final static String ERROR_NOT_ENOUGH_DSTORES_TOKEN = "ERROR_NOT_ENOUGH_DSTORES";
	public final static String ERROR_LOAD_TOKEN = "ERROR_LOAD";

	// getter methods //

	public static String getJoinAckMessage(){
		return Protocol.JOIN_ACK_TOKEN;
	}

	public static String getStoreToMessage(ArrayList<Integer> dstores){
		// converting the list of ports to strings
		ArrayList<String> stringDstores = new ArrayList<String>();
		for(int dstore : dstores){
			stringDstores.add(Integer.toString(dstore));
		}

		// returning the message
		return (Protocol.STORE_TO_TOKEN + Protocol.SPACE + String.join(Protocol.SPACE, stringDstores));
	}

	public static String getStoreCompleteMessage(){
		return Protocol.STORE_COMPLETE_TOKEN;
	}

	public static String getLoadFromMessage(int port, int filesize){
		return (Protocol.LOAD_FROM_TOKEN + Protocol.SPACE + port + Protocol.SPACE + filesize);
	}

	public static String getRemoveCompleteMessage(){
		return Protocol.REMOVE_COMPLETE_TOKEN;
	}

	public static String getRebalanceMessage(ArrayList<FileToSend> filesToSend, ArrayList<String> filesToRemove){
        // files to send message
        String filesToSendMessage = "";
        if(filesToSend.size() == 0){
            filesToSendMessage += filesToSend.size() + Protocol.SPACE;
        }
        else{
            filesToSendMessage += filesToSend.size() + Protocol.SPACE;
            for(FileToSend fileToSend : filesToSend){
                filesToSendMessage += fileToSend.filename + Protocol.SPACE + fileToSend.filesize + Protocol.SPACE;

                filesToSendMessage += fileToSend.dStores.size() + Protocol.SPACE;

                for(int dstore : fileToSend.dStores){
                    filesToSendMessage += dstore;
                    filesToSendMessage += Protocol.SPACE;
                }
            }
        }

        // files to remove message
        String filesToRemoveMessage = "";
        if(filesToRemove.size() == 0){
            filesToRemoveMessage += filesToRemove.size();
        }
        else{
            filesToRemoveMessage += filesToRemove.size() + Protocol.SPACE;
            filesToRemoveMessage += String.join(Protocol.SPACE, filesToRemove);
        }

		// final message
        return (Protocol.REBALANCE_TOKEN + Protocol.SPACE + filesToSendMessage + filesToRemoveMessage);
	}

	public static String getErrorDstorePortInUseMessage(){
		return Protocol.ERROR_DSTORE_PORT_IN_USE_TOKEN;
	}
	
	public static String getErrorFileDoesNotExistMessage(){
		return Protocol.ERROR_FILE_DOES_NOT_EXIST_TOKEN;
	}

	public static String getErrorFileDoesNotExistMessage(String filename){
		return (Protocol.ERROR_FILE_DOES_NOT_EXIST_TOKEN + Protocol.SPACE + filename);
	}

	public static String getErrorFileAlreadyExistsMessage(){
		return Protocol.ERROR_FILE_ALREADY_EXISTS_TOKEN;
	}

	public static String getErrorNotEnoughDstoresMessage(){
		return Protocol.ERROR_NOT_ENOUGH_DSTORES_TOKEN;
	}

	public static String getErrorLoadMessage(){
		return Protocol.ERROR_LOAD_TOKEN;
	}

	/////////////////////
	// DSTORE MESSAGES //
	/////////////////////
	
	// static variables //

	public final static String JOIN_DSTORE_TOKEN = "JOIN_DSTORE";
	public final static String ACK_TOKEN = "ACK";
	public final static String STORE_ACK_TOKEN = "STORE_ACK";
	public final static String REMOVE_ACK_TOKEN = "REMOVE_ACK";
	public final static String REBALANCE_STORE_TOKEN = "REBALANCE_STORE";
	public final static String REBALANCE_COMPLETE_TOKEN = "REBALANCE_COMPLETE";

	// getter methods //

	public static String getJoinDstoreMessage(int port){
		return (Protocol.JOIN_DSTORE_TOKEN + Protocol.SPACE + port);
	}

	public static String getAckMessage(){
		return Protocol.ACK_TOKEN;
	}

	public static String getStoreAckMessage(String filename){
		return (Protocol.STORE_ACK_TOKEN + Protocol.SPACE + filename);
	}

	public static String getRemoveAckMessage(String filename){
		return (Protocol.REMOVE_ACK_TOKEN + Protocol.SPACE + filename);
	}

	public static String getRebalanceStoreMessage(String filename, int filesize){
		
		return (Protocol.REBALANCE_STORE_TOKEN + Protocol.SPACE + filename + Protocol.SPACE + filesize);
	}

	public static String getRebalanceCompleteMessage(HashMap<String, Integer> files){
        if(files.size() == 0){
            return (Protocol.REBALANCE_COMPLETE_TOKEN + Protocol.SPACE);
        }
        else{
            String filesString = "";

            // forming single string
            for(String file : files.keySet()){
                filesString += Protocol.SPACE + file + Protocol.SPACE + files.get(file);
            }
            
            return (Protocol.REBALANCE_COMPLETE_TOKEN + filesString);
        }
	}
}