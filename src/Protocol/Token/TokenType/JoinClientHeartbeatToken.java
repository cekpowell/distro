package Protocol.Token.TokenType;

import Protocol.Token.Token;

/**
 * Token for ...
 * 
 * Syntax: 
 */
public class JoinClientHeartbeatToken extends Token {

    // member variables
    public int port;

    public JoinClientHeartbeatToken(String message, int port) {
        this.message = message;
        this.port = port;
    }
}
