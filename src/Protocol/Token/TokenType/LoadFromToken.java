package Protocol.Token.TokenType;

import Protocol.Token.Token;

/**
 * Token for ...
 * 
 * Syntax: 
 */
public class LoadFromToken extends Token{
    public int port;
    public int filesize;

    public LoadFromToken(String message, int port, int filesize){
        this.message = message;
        this.port = port;
        this.filesize = filesize;
    }
}