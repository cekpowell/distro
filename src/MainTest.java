import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import Controller.*;
import DSClient.DSClientTerminal;
import Dstore.*;
import Protocol.Token.RequestTokenizer;
import Protocol.Token.Token;

public class MainTest {

    public static void main(String[] args) throws Exception{

        // Testing Random //
        testRandom();

        // Testing Tokenizer //
        //testTokenizer();

        // Testing connections //
        //testConnection();

        // Testing Concurrency
        //testConcurrency();
    }

    public static void testRandom(){
        String string = "JOIN_DSTORE 5000";
        System.out.println(string.contains("JOIN_DSTORE"));

        System.out.println(string.split(" ")[1]);
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
            try{ Thread.sleep(3000);} catch(Exception e){}
            client.client.handleInputRequest("REMOVE test.txt");
        }).start();
        new Thread(() -> {
            DSClientTerminal client = new DSClientTerminal(4000, 2000);

            client.client.handleInputRequest("STORE test2.txt 14");
            try{ Thread.sleep(3000);} catch(Exception e){}
            client.client.handleInputRequest("REMOVE test2.txt");
        }).start();
        new Thread(() -> {
            DSClientTerminal client = new DSClientTerminal(4000, 2000);

            client.client.handleInputRequest("STORE test3.txt 14");
            try{ Thread.sleep(3000);} catch(Exception e){}
            client.client.handleInputRequest("REMOVE test3.txt");
        }).start();
    }
}