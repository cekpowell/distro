/**
 * Southampton University
 * COMP2207: Distributed Systems
 * @author Charles Powell
 */

package Token.TokenType;

import Token.Token;

/**
 * Syntax: 
 */
public class ErrorFileAlreadyExistsToken extends Token{

    public ErrorFileAlreadyExistsToken(String request){
        this.request = request;
    }
}