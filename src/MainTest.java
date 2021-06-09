import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import Controller.*;
import DSClient.DSClient;
import DSClient.DSClientTerminal;
import Dstore.*;
import Index.Index;
import Index.State.OperationState;
import Token.RequestTokenizer;
import Token.Token;

public class MainTest {

    public static void main(String[] args) throws Exception{

        // Testing Tokenizer //
        //testTokenizer();

        // Testing connections //
        //testConnection();

        // Testing Concurrency
        testConcurrency();
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

    public static void testConcurrency(){
        new Thread(() -> {
            DSClientTerminal client = new DSClientTerminal(4000, 2000);

            client.client.handleInputRequest("STORE test.txt 14");
            try{ Thread.sleep(500);} catch(Exception e){}
            client.client.handleInputRequest("REMOVE test2.txt");
        }).start();
        new Thread(() -> {
            DSClientTerminal client = new DSClientTerminal(4000, 2000);

            client.client.handleInputRequest("STORE test1.txt 14");
            try{ Thread.sleep(500);} catch(Exception e){}
            client.client.handleInputRequest("REMOVE test.txt");
        }).start();
        new Thread(() -> {
            DSClientTerminal client = new DSClientTerminal(4000, 2000);

            client.client.handleInputRequest("STORE test2.txt 14");
            try{ Thread.sleep(500);} catch(Exception e){}
            client.client.handleInputRequest("LOAD test.txt");
        }).start();
        new Thread(() -> {
            DSClientTerminal client = new DSClientTerminal(4000, 2000);

            client.client.handleInputRequest("STORE test3.txt 14");
            try{ Thread.sleep(500);} catch(Exception e){}
            client.client.handleInputRequest("LOAD test1.txt");
        }).start();
    }
}