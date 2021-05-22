package Token.TokenType;

import Token.Token;

/**
 * Token for ...
 * 
 * Syntax: 
 */
public class LoadFromToken extends Token{
    public int port;
    public double filesize;

    public LoadFromToken(String message, int port, double filesize){
        this.message = message;
        this.port = port;
        this.filesize = filesize;
    }
}