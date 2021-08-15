package Network.Protocol.Event;

import Network.Client.Client;

/**
 * Represents the event of a Server receieving a new connection from a Client/Server.
 */
public class ServerConnectionEvent extends NetworkEvent{

    /**
     * Class constructor.
     * 
     * @param clientType The type of Client the connection has come from.
     * @param port The port associated with the client.
     */
    public ServerConnectionEvent(Client.ClientType clientType, int port){
        super("New " + clientType.toString() + " connected on port : " + port + ".");
    }
}
