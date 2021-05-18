package Token.TokenType;

import Token.Token;

/**
 * Syntax: 
 */
public class JoinToken extends Token {
    public int port;

    public JoinToken(String req, int port) {
        this.request = req;
        this.port = port;
    }
}