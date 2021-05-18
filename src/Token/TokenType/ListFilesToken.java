package Token.TokenType;

import java.util.ArrayList;

import Token.Token;

/**
 * Syntax: 
 */
public class ListFilesToken extends Token{
    public ArrayList<String> filenames;

    public ListFilesToken(String request, ArrayList<String> filenames){
        this.request = request;
        this.filenames = filenames;
    }
}