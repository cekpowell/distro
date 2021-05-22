package Server;

import java.net.InetAddress;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * Convenience class for Socket.
 * 
 * Creates new socket but also creates the input and output streams at instantiation.
 * 
 * Has getters thta allow for quick access to these output streams.
 */
public class Connection extends Socket{
    
    // member variables
    private PrintWriter textOut;
    private BufferedReader textIn;
    private OutputStream dataOut;
    private InputStream dataIn;

    /**
     * Class constructor.
     * 
     * @param socket The socket involved in the connection
     */
    public Connection(InetAddress address, int port) throws Exception{
        super(address, port);
        this.textOut = new PrintWriter (new OutputStreamWriter(this.getOutputStream())); 
        this.textIn = new BufferedReader (new InputStreamReader(this.getInputStream()));
        this.dataOut = this.getOutputStream();
        this.dataIn = this.getInputStream();
    }

    /////////////////////////
    // GETTERS AND SETTERS //
    /////////////////////////

    public PrintWriter getTextOut(){
        return this.textOut;
    }

    public BufferedReader getTextIn(){
        return this.textIn;
    }

    public OutputStream getDataOut(){
        return this.dataOut;
    }

    public InputStream getDataIn(){
        return this.dataIn;
    }
}
