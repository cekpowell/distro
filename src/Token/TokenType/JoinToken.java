package Token.TokenType;

import Token.Token;

/**
 * Token for ...
 * 
 * Syntax: 
 */
public class JoinToken extends Token {
    public int port;

    public JoinToken(String message, int port) {
        this.message = message;
        this.port = port;
    }
}