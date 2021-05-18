package Token.TokenType;

import Token.Token;

/**
 * Syntax: 
 */
public class ReloadToken extends Token{
    public String filename;

    public ReloadToken(String request, String filename){
        this.request = request;
        this.filename = filename;
    }
}