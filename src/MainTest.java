import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import Controller.*;
import Dstore.*;
import Index.Index;
import Index.State.OperationState;
import Token.RequestTokenizer;
import Token.Token;

public class MainTest {

    public static void main(String[] args) throws Exception{

        // Testing Tokenizer //
        testTokenizer();

        // Testing connections //
        //testConnection();

        // Testing Index Manager //
        //testIndexManager();
    }

    public static void testTokenizer() throws IOException{
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        while(true){
            String request = reader.readLine();

            Token token = RequestTokenizer.getToken(request);

            System.out.println(token.toString());
            
        }
    }

    public static void testConnection() throws Exception{
        // controller
        new Thread(new Runnable(){
            public void run(){
                new ControllerTerminal(4000, 1, 1000, 1000);
            }
        }).start();

        // dstore 1
        new Thread(new Runnable(){
            public void run(){
                new DstoreTerminal (4009, 4000, 1000, "test");
            }
        }).start();

        // dstore 2
        new Thread(new Runnable(){
            public void run(){
                new DstoreTerminal (4007, 4000, 1000, "test");
             }
        }).start();
    }

    public static void testIndexManager(){
        // Index indexManager = new Index(2);

        // System.out.println("Adding dstore 1, 2 and 3....");
        // indexManager.addDstore(1, null);
        // indexManager.addDstore(2, null);
        // indexManager.addDstore(3, null);
        // System.out.println(indexManager.toString());

        // System.out.println("Adding file 'Test' ...");
        // indexManager.startStoring("Test", 1);
        // System.out.println(indexManager.toString());

        // System.out.println("Adding file 'Test Two' ...");
        // indexManager.startStoring("Test Two", 1);
        // System.out.println(indexManager.toString());

        // System.out.println("Adding file 'Test Three' ...");
        // indexManager.startStoring("Test Three", 1);
        // System.out.println(indexManager.toString());

        // System.out.println("Sending STORE_ACK for Dstore on port 1 for file 'Test'...");
        // indexManager.storeAckRecieved(1, "Test");
        // System.out.println(indexManager.toString());

        // System.out.println("Sending STORE_ACK for Dstore on port 2 for file 'Test'...");
        // indexManager.storeAckRecieved(2, "Test");
        // System.out.println(indexManager.toString());

        // System.out.println("Sending STORE_ACK for Dstore on port 3 for file 'Test'...");
        // indexManager.storeAckRecieved(3, "Test");
        // System.out.println(indexManager.toString());

        // System.out.println("Waiting for store to be complete for file 'Test'...");
        // try{
        //     indexManager.waitForOperationComplete("Test", 3000, OperationState.STORE_ACK_RECIEVED, OperationState.IDLE);
        // }
        // catch(Exception e){
        //     System.out.println("*ERROR* : Failed to complete store operation.");
        // }
        // System.out.println(indexManager.toString())
;        
    }
}