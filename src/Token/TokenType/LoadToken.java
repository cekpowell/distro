package Token.TokenType;

import Token.Token;

/**
 * Token for ...
 * 
 * Syntax: 
 */
public class LoadToken extends Token{
    public String filename;

    public LoadToken(String request, String filename){
        this.request = request;
        this.filename = filename;
    }
}