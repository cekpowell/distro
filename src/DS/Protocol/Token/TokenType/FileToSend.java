package DS.Protocol.Token.TokenType;

import java.util.ArrayList;

/**
 * Token for ...
 * 
 * Syntax: 
 */
public class FileToSend{
    
    public String filename;
    public int filesize;
    public ArrayList<Integer> dStores;

    public FileToSend(String filename, int filesize, ArrayList<Integer> dStores){
        this.filename = filename;
        this.filesize = filesize;
        this.dStores = dStores;
    }

    public String toString(){
        String dstoresToSendTo = "";
        for(int dstore : this.dStores){
            dstoresToSendTo += dstore + " ";
        }

        return ("Filename : " + filename + "\n" 
               + "Filesize : " + filesize + "\n"
               + "Dstores to send to : " + dstoresToSendTo);
    }
}
