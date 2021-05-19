package Connection;

import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * 
 */
public abstract class Connection extends Thread {
    
    // member variables
    Object self;
    private Socket connection;
    
    private PrintWriter textOut;
    private BufferedReader textIn;
    private OutputStream dataOut;
    private InputStream dataIn;

    public Connection(Object self, Socket connection){
        this.self = self;
        this.connection = connection;
        try{
            this.textOut = new PrintWriter (new OutputStreamWriter(connection.getOutputStream())); 
            this.textIn = new BufferedReader (new InputStreamReader(connection.getInputStream()));
            this.dataOut = connection.getOutputStream();
            this.dataIn = connection.getInputStream();
        }
        catch(Exception e){
            System.out.println("ERROR : Unable to create streams.");
        }
    }

    /**
     * Getters and setters
     */
    public Socket getConnection(){
        return this.connection;
    }

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
