import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import Token.RequestTokenizer;
import Token.Token;

public class MainTest {

    public static void main(String[] args) throws IOException{

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        while(true){
            String request = reader.readLine();

            Token token = RequestTokenizer.getToken(request);

            System.out.println(token.toString());
            
        }
    }
}
