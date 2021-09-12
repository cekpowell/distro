package DS.Protocol.Token.TokenType;

import DS.Protocol.Token.Token;

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