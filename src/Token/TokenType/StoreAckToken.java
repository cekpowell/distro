package Token.TokenType;

import Token.Token;

/**
 * Token for ...
 * 
 * Syntax: 
 */
public class StoreAckToken extends Token{

    // member variables
    public String filename;

    public StoreAckToken(String message, String filename){
        this.message = message;
        this.filename = filename;
    }
}