package Token.TokenType;

import Token.Token;

/**
 * Syntax: 
 */
public class ErrorLoadToken extends Token{

    public ErrorLoadToken(String request){
        this.request = request;
    }
}