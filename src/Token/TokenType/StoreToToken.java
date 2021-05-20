package Token.TokenType;

import java.util.ArrayList;

import Token.Token;

/**
 * Token for ...
 * 
 * Syntax: 
 */
public class StoreToToken extends Token{
    public ArrayList<Integer> ports;

    public StoreToToken(String request, ArrayList<Integer> ports){
        this.request = request;
        this.ports = ports;
    }
}
