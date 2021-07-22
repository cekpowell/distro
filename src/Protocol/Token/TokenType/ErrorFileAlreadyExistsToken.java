package Protocol.Token.TokenType;

import Protocol.Token.Token;

/**
 * Token for ...
 * 
 * Syntax: 
 */
public class ErrorFileAlreadyExistsToken extends Token{

    public ErrorFileAlreadyExistsToken(String message){
        this.message = message;
    }
}