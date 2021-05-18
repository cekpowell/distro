package Token.TokenType;

import Token.Token;

/**
 * Syntax: 
 */
public class LoadFromToken extends Token{
    public int port;
    public double filesize;

    public LoadFromToken(String request, int port, double filesize){
        this.request = request;
        this.port = port;
        this.filesize = filesize;
    }
}