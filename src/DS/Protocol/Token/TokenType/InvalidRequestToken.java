package DS.Protocol.Token.TokenType;

import DS.Protocol.Token.Token;

/**
 * Token for invalid request.
 * 
 * Syntax: 
 */
public class InvalidRequestToken extends Token{

    public InvalidRequestToken(String message){
        this.message = message;
    }
}