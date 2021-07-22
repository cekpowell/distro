package Protocol.Token.TokenType;

import Protocol.Token.Token;

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