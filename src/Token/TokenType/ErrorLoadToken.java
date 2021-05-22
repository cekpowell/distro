package Token.TokenType;

import Token.Token;

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