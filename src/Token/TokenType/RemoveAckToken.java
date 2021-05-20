package Token.TokenType;

import Token.Token;

/**
 * Token for ...
 * 
 * Syntax: 
 */
public class RemoveAckToken extends Token{
    
    public String filename;

    public RemoveAckToken(String request, String filename){
        this.request = request;
        this.filename = filename;
    }
}