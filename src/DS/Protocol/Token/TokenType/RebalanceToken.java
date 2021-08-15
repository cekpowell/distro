package DS.Protocol.Token.TokenType;

import java.util.ArrayList;

import DS.Protocol.Token.Token;

/**
 * Token for ...
 * 
 * Syntax: 
 */
public class RebalanceToken extends Token{
    public ArrayList<FileToSend> filesToSend;
    public ArrayList<String> filesToRemove;

    public RebalanceToken(String message, ArrayList<FileToSend> filesToSend, ArrayList<String> filesToRemove){
        this.message = message;
        this.filesToSend = filesToSend;
        this.filesToRemove = filesToRemove;
    }
}