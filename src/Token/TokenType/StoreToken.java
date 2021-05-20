package Token.TokenType;

import Token.Token;

/**
 * Token for ...
 * 
 * Syntax: 
 */
public class StoreToken extends Token{
    public String filename;
    public double filesize;

    public StoreToken(String request, String filename, double filesize){
        this.request = request;
        this.filename = filename;
        this.filesize = filesize;
    }
}
