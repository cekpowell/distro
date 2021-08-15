package DS.Protocol.Token.TokenType;

import DS.Protocol.Token.Token;

/**
 * Token for ...
 * 
 * Syntax: 
 */
public class LoadToken extends Token{
    public String filename;

    public LoadToken(String message, String filename){
        this.message = message;
        this.filename = filename;
    }
}