package Protocol.Token.TokenType;

import Protocol.Token.Token;

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