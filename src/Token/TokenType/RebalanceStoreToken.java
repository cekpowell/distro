package Token.TokenType;

import Token.Token;

/**
 * Token for ...
 * 
 * Syntax: 
 */
public class RebalanceStoreToken extends Token{
    public String filename;
    public double filesize;

    public RebalanceStoreToken(String request, String filename, double filesize){
        this.request = request;
        this.filename = filename;
        this.filesize = filesize;
    }
}