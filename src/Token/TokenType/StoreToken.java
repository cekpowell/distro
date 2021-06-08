package Token.TokenType;

import Token.Token;

/**
 * Token for ...
 * 
 * Syntax: 
 */
public class StoreToken extends Token{
    public String filename;
    public int filesize;

    public StoreToken(String message, String filename, int filesize){
        this.message = message;
        this.filename = filename;
        this.filesize = filesize;
    }
}
