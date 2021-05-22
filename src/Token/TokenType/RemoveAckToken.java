package Token.TokenType;

import Token.Token;

/**
 * Token for ...
 * 
 * Syntax: 
 */
public class RemoveAckToken extends Token{
    
    public String filename;

    public RemoveAckToken(String message, String filename){
        this.message = message;
        this.filename = filename;
    }
}