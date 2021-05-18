package Token.TokenType;

import Token.Token;

/**
 * Syntax: 
 */
public class LoadDataToken extends Token{
    public String filename;

    public LoadDataToken(String request, String filename){
        this.request = request;
        this.filename = filename;
    }
}