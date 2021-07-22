package Protocol.Token.TokenType;

import Protocol.Token.Token;

/**
 * Token for ...
 * 
 * Syntax: 
 */
public class ErrorLoadToken extends Token{

    public ErrorLoadToken(String message){
        this.message = message;
    }
}