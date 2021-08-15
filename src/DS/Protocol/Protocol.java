package DS.Protocol;

import java.util.ArrayList;

/**
 * Contains all of the messages that can be sent throughout the system and methods to generate them.
 */
public class Protocol {

	// static varibales
	private final static String space = " ";

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
		return JOIN_CLIENT_TOKEN;
	}

	public static String getJoinClientHeartbeatMessage(int clientPort){
		return (JOIN_CLIENT_HEARTBEAT + space + clientPort);
	}

	public static String getListMessage(){
		return LIST_TOKEN;
	}

	public static String getListOfFilesMessage(ArrayList<String> files){
        // forming single string
        String filesString = String.join(" ", files);
        
		return (LIST_TOKEN + space + filesString);
	}

	public static String getStoreMessage(String filename, int filesize){
		return (STORE_TOKEN + space + filename + space + filesize);
	}

	public static String getLoadMessage(String filename){
		return (LOAD_TOKEN + space + filename);
	}

	public static String getLoadDataMessage(String filename){
		return (LOAD_DATA_TOKEN + space + filename);
	}

	public static String getReloadMessage(String filename){
		return (RELOAD_TOKEN + space + filename);
	}

	public static String getRemoveMessage(String filename){
		return (REMOVE_TOKEN + space + filename);
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
		return JOIN_ACK_TOKEN;
	}

	public static String getStoreToMessage(ArrayList<Integer> dstores){
		// converting the list of ports to strings
		ArrayList<String> stringDstores = new ArrayList<String>();
		for(int dstore : dstores){
			stringDstores.add(Integer.toString(dstore));
		}

		// returning the message
		return (Protocol.STORE_TO_TOKEN + space + String.join(space, stringDstores));
	}

	public static String getStoreCompleteMessage(){
		return STORE_COMPLETE_TOKEN;
	}

	public static String getLoadFromMessage(int port, int filesize){
		return (LOAD_FROM_TOKEN + space + port + space + filesize);
	}

	public static String getRemoveCompleteMessage(){
		return REMOVE_COMPLETE_TOKEN;
	}

	public static String getRebalanceMessage(){
		// TODO
		return null;
	}

	public static String getErrorDstorePortInUseMessage(){
		return ERROR_DSTORE_PORT_IN_USE_TOKEN;
	}
	
	public static String getErrorFileDoesNotExistMessage(){
		return ERROR_FILE_DOES_NOT_EXIST_TOKEN;
	}

	public static String getErrorFileDoesNotExistMessage(String filename){
		return (ERROR_FILE_DOES_NOT_EXIST_TOKEN + space + filename);
	}

	public static String getErrorFileAlreadyExistsMessage(){
		return ERROR_FILE_ALREADY_EXISTS_TOKEN;
	}

	public static String getErrorNotEnoughDstoresMessage(){
		return ERROR_NOT_ENOUGH_DSTORES_TOKEN;
	}

	public static String getErrorLoadMessage(){
		return ERROR_LOAD_TOKEN;
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
		return (JOIN_DSTORE_TOKEN + space + port);
	}

	public static String getAckMessage(){
		return ACK_TOKEN;
	}

	public static String getStoreAckMessage(String filename){
		return (STORE_ACK_TOKEN + space + filename);
	}

	public static String getRemoveAckMessage(String filename){
		return (REMOVE_ACK_TOKEN + space + filename);
	}

	public static String getRebalanceStoreMessage(){
		// TODO
		return null;
	}

	public static String getRebalanceCompleteMessage(){
		// TODO
		return null;
	}
}