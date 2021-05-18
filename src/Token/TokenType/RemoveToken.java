package Token.TokenType;

import Token.Token;

/**
 * Syntax: 
 */
public class RemoveToken extends Token{
    public String filename;

    public RemoveToken(String request, String filename){
        this.request = request;
        this.filename = filename;
    }
}