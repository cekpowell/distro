package DS.Protocol.Token.TokenType;

import java.util.HashMap;

import DS.Protocol.Token.Token;

/**
 * Token for ...
 * 
 * Syntax: 
 */
public class ListFilesToken extends Token{
    
    public HashMap<String, Integer> files;

    public ListFilesToken(String message, HashMap<String, Integer> files){
        this.message = message;
        this.files = files;
    }
}