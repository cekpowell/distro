package Protocol.Token.TokenType;

import Protocol.Token.Token;

/**
 * Token for ...
 * 
 * Syntax: 
 */
public class ErrorFileDoesNotExistToken extends Token{

    public ErrorFileDoesNotExistToken(String message){
        this.message = message;
    }
}