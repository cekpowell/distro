package Token.TokenType;

import Token.Token;

/**
 * Token for invalid request.
 */
public class InvalidRequestToken extends Token{

    public InvalidRequestToken(String request){
        this.request = request;
    }
}