package Token.TokenType;

import java.util.ArrayList;

import Token.Token;

/**
 * Syntax: 
 */
public class RebalanceToken extends Token{
    public ArrayList<FileToSend> filesToSend;
    public ArrayList<String> filesToRemove;

    public RebalanceToken(String request, ArrayList<FileToSend> filesToSend, ArrayList<String> filesToRemove){
        this.request = request;
        this.filesToSend = filesToSend;
        this.filesToRemove = filesToRemove;
    }
}