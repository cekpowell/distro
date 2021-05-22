package Token.TokenType;

import Token.Token;

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