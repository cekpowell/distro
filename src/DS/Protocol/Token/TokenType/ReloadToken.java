package DS.Protocol.Token.TokenType;

import DS.Protocol.Token.Token;

/**
 * Token for ...
 * 
 * Syntax: 
 */
public class ReloadToken extends Token{
    public String filename;

    public ReloadToken(String message, String filename){
        this.message = message;
        this.filename = filename;
    }
}