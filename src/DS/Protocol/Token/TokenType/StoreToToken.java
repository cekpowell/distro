package DS.Protocol.Token.TokenType;

import java.util.ArrayList;

import DS.Protocol.Token.Token;

/**
 * Token for ...
 * 
 * Syntax: 
 */
public class StoreToToken extends Token{
    public ArrayList<Integer> ports;

    public StoreToToken(String message, ArrayList<Integer> ports){
        this.message = message;
        this.ports = ports;
    }
}
