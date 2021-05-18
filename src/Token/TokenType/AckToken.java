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
public class AckToken extends Token{
    public AckToken(String request){
        this.request = request;
    }
}
