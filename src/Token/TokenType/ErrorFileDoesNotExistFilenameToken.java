package Token.TokenType;

import Token.Token;

/**
 * Syntax: 
 */
public class ErrorFileDoesNotExistFilenameToken extends Token{

    public String filename;

    public ErrorFileDoesNotExistFilenameToken(String request, String filename){
        this.request = request;
        this.filename = filename;
    }
}