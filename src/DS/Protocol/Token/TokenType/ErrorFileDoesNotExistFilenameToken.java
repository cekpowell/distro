package DS.Protocol.Token.TokenType;

import DS.Protocol.Token.Token;

/**
 * Token for ...
 * 
 * Syntax: 
 */
public class ErrorFileDoesNotExistFilenameToken extends Token{

    public String filename;

    public ErrorFileDoesNotExistFilenameToken(String message, String filename){
        this.message = message;
        this.filename = filename;
    }
}