package Token.TokenType;

import Token.Token;

/**
 * Token for ...
 * 
 * Syntax: 
 */
public class RemoveToken extends Token{
    public String filename;

    public RemoveToken(String message, String filename){
        this.message = message;
        this.filename = filename;
    }
}