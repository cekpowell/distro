package DS.Protocol.Token.TokenType;

import DS.Protocol.Token.Token;

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