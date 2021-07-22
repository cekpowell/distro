package Protocol.Token.TokenType;

import Protocol.Token.Token;

/**
 * Token for ...
 * 
 * Syntax: 
 */
public class LoadDataToken extends Token{
    public String filename;

    public LoadDataToken(String message, String filename){
        this.message = message;
        this.filename = filename;
    }
}