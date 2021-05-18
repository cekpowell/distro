package Token.TokenType;

import java.util.ArrayList;

public class FileToSend{
    public String filename;
    ArrayList<Integer> dStores;

    public FileToSend(String filename, ArrayList<Integer> dStores){
        this.filename = filename;
        this.dStores = dStores;
    }
}
