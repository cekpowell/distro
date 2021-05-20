import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import Controller.Controller;
import Dstore.Dstore;
import Token.RequestTokenizer;
import Token.Token;

public class MainTest {

    public static void main(String[] args) throws Exception{

        // Testing Tokenizer //
        //testTokenizer();

        // Testing connections //
        testConnection();
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
                Controller controller = new Controller(4000, 1, 1000, 1000);
            }
        }).start();

        // dstore 1
        new Thread(new Runnable(){
            public void run(){
                Dstore dStore = new Dstore (4009, 4000, 1000, "test");
            }
        }).start();

        // dstore 2
        new Thread(new Runnable(){
            public void run(){
                Dstore dStore2 = new Dstore (4007, 4000, 1000, "test");
             }
        }).start();
    }
}