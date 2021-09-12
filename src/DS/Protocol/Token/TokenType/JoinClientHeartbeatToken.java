package DS.Protocol.Token.TokenType;

import DS.Protocol.Token.Token;

/**
 * Token for ...
 * 
 * Syntax: 
 */
public class JoinClientHeartbeatToken extends Token {
    
    public int port;

    public JoinClientHeartbeatToken(String message, int port) {
        this.message = message;
        this.port = port;
    }
}
