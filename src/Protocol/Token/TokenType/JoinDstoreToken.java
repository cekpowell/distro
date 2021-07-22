package Protocol.Token.TokenType;

import Protocol.Token.Token;

/**
 * Token for ...
 * 
 * Syntax: 
 */
public class JoinDstoreToken extends Token {
    public int port;

    public JoinDstoreToken(String message, int port) {
        this.message = message;
        this.port = port;
    }
}