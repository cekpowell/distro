package DS.Protocol.Token.TokenType;

import DS.Protocol.Token.Token;

/**
 * Token for ...
 * 
 * Syntax: 
 */
public class RebalanceStoreToken extends Token{
    
    public String filename;
    public int filesize;

    public RebalanceStoreToken(String message, String filename, int filesize){
        this.message = message;
        this.filename = filename;
        this.filesize = filesize;
    }
}