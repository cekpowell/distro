package DSClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

import Network.NetworkInterface;
import Network.Protocol.Exception.*;
import Protocol.Exception.*;
import Protocol.Token.RequestTokenizer;
import Protocol.Token.TokenType.*;
import Protocol.Token.Token;

/**
 * Implementation of a ClientInterface that uses the terminal as an interface
 * betweeen the Client and the user.
 * 
 * Creates a DSClient object, that connects to the Controller when started.
 * 
 * The interface takes requests from the user on stdin and passes them to the DSClient
 * object to be sent to the Controller.
 * 
 * The interface logs the response to requests on stdout.
 */
public class DSClientTerminal extends NetworkInterface{

    // member variables
    public DSClient client;

    /**
     * Class Constructor.
     * 
     * @param cPort The port of the Controller.
     * @param timeout The message timeout period.
     */
    public DSClientTerminal(int cPort, int timeout) {
        // initialising member variables
        this.client = new DSClient(cPort, timeout, this);

        // connecting to network
        this.startNetworkProcess(this.client);

        // waiting for user input
        this.waitForInput();
    }

    ////////////////////////
    // GETTING USER INPUT //
    ////////////////////////

    /**
     * Waits for user to input a request into the terminal.
     */
    public void waitForInput(){
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            // Wait for input 
            while(true){
                // FORMATTING
                System.out.println();
                System.out.print(">");

                // reading in request
                String input = reader.readLine();

                // making sure client request is non-null
                if(input.equals("")){
                    this.client.handleError(new RequestHandlingException("", new NullClientInputRequestException()));
                }
                else{
                    // sending request to controller
                    this.handleInput(input);
                }
            }
        }
        catch(Exception e){
            this.client.handleError(new ClientInputRequestReadException(e));
        }
    }

    ////////////////////
    // INPUT HANDLING //
    ////////////////////

    /**
     * Handles an input that has come from the Client through the terminal.
     * 
     * @param input The input to be handeled.
     */
    public void handleInput(String input){
        try{
            Token requestToken = RequestTokenizer.getToken(input);

            // STORE //
            if(requestToken instanceof StoreToken){
                StoreToken storeToken = (StoreToken) requestToken;
                this.handleStoreInput(storeToken.filename, storeToken.filesize);
            }

            // LOAD //
            else if(requestToken instanceof LoadToken){
                LoadToken loadToken = (LoadToken) requestToken;
                this.handleLoadInput(loadToken.filename);
            }

            // REMOVE //
            else if(requestToken instanceof RemoveToken){
                RemoveToken removeToken = (RemoveToken) requestToken;
                this.handleRemoveInput(removeToken.filename);
            }

            // LIST //
            else if(requestToken instanceof ListToken){
                this.handleListInput();
            }

            // Invalid Request
            else{
                this.handleInvalidInput(input);
            }
        }
        catch(Exception e){
            this.client.handleError(new RequestHandlingException(input, e));
        }
    }

    ///////////
    // STORE //
    ///////////

    /**
     * Handles the input of a STORE request into the terminal.
     * 
     * @param filename The name of the file to be stored.
     * @param filesize The size of the file to be stored (in bytes).
     * 
     * @throws Exception If the request could not be handeled.
     */
    public void handleStoreInput(String filename, int filesize) throws Exception{
        // loading the file
        File file = new File(filename);

        // send the file to the DSClient
        this.client.storeFile(file, filesize);
    }

    //////////
    // LOAD //
    //////////

    /**
     * Handles the input of a LOAD request into the terminal.
     * 
     * @param filename The name of the file to be loaded.
     * 
     * @throws Exception If the request could not be handeled.
     */
    public void handleLoadInput(String filename) throws Exception{
        // gathering the file content
        byte[] fileContent = this.client.loadFile(filename, false);

        // storing the file
        File file = new File(filename);
        FileOutputStream fileOutput = new FileOutputStream(file);
        fileOutput.write(fileContent);
        fileOutput.flush();
        fileOutput.close();
    }

    ////////////
    // REMOVE //
    ////////////
    
    /**
     * Handles the input of a REMOVE request into the terminal.
     * 
     * @param filename The name of the file to be removed.
     * 
     * @throws Exception If the request could not be handeled.
     */
    public void handleRemoveInput(String filename) throws Exception{
        // removes the file from the system
        this.client.removeFile(filename);
    }

    //////////
    // LIST //
    //////////

    /**
     * Handles the input of a LIST request into the terminal.
     * 
     * @throws Exception If the request could not be handeled.
     */
    public void handleListInput() throws Exception{
        // gathering the list of files
        ArrayList files = this.client.getFileList();

        // nothing to do with the list ...
    }

    /////////////
    // INVALID //
    /////////////

    /**
     * Handles the input of an invalid request into the terminal.
     * 
     * @param input The input into the terminal.
     * 
     * @throws Exception If the request could not be handeled.
     */
    public void handleInvalidInput(String input) throws Exception{
        // handling the error
        this.client.handleError(new InvalidClientRequestException(input));
    }


    /////////////
    // LOGGING //
    /////////////

    /**
     * Handles the logging of a message being sent.
     * 
     * @param connection The socket between the sender and reciever.
     * @param message The message to be logged.
     */
    public void logMessageSent(Socket connection, String message){
        System.out.println("[" + connection.getLocalPort() + " -> " + connection.getPort() + "] " + message);
    }

    /**
     * Handles the logging of a message being recieved.
     * 
     * @param connection The socket between the sender and reciever.
     * @param message The message to be logged.
     */
    public void logMessageReceived(Socket connection, String message){
        System.out.println("[" + connection.getLocalPort() + " <- " + connection.getPort() + "] " + message);
    }

    /**
     * Handles the logging of an event.
     * 
     * @param event The event to be logged.
     */
    public void logEvent(String event){
        System.out.println("#EVENT# " + event);
    }

    /**
     * Handles the logging of an error.
     * 
     * @param error The error to be logged.
     */
    public void logError(HandeledNetworkException error){
        // logging error to terminal
        System.out.println(error.toString());

        // HANDLING SPECIFIC CASES //

        // Controller disconnected
        if(error.getException() instanceof ControllerDisconnectException){
            System.exit(0);
        }
        // Client Start Exception
        else if(error.getException() instanceof ClientStartException){
            System.exit(0);
        }
    }


    /////////////////
    // MAIN METHOD //
    /////////////////

    
    /**
     * Main method - instantiates a new Client instance using the command line parammeters.
     * @param args Parameters for the new Client.
     */
    public static void main(String[] args){
        try{
            // gathering parameters
            int cPort = Integer.parseInt(args[0]);
            int timeout = Integer.parseInt(args[1]);

            // Creating new Client instance
            new DSClientTerminal(cPort, timeout);
        }
        catch(Exception e){
            System.out.println("Unable to create Client.");
        }
    }
}