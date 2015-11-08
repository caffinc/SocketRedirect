/**
 * SocketRedirect v0.1 - Simple socket redirection code
 * 
 * @author Testware
 */
package com.caffinc.socketredirect.app;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import com.caffinc.socketredirect.exceptions.NotEnoughParametersException;


public class SocketRedirect
{
    private static final String VERSION_INFO = "SocketRedirect v0.2\n(c) Testware";


    public static void main( String[] args )
    {
        String host = null;
        int inputPort = -1, outputPort = -1;
        System.out.println( getVersionInfo() );

        if ( args.length < 3 ) {
            throw new NotEnoughParametersException( "Not enough parameters\n" + getUsageMessage() );
        }

        for ( String arg : args ) {
            if ( arg.startsWith( "-h" ) ) {
                host = arg.substring( 2 );
            } else if ( arg.startsWith( "-i" ) ) {
                inputPort = Integer.parseInt( arg.substring( 2 ) );
            } else if ( arg.startsWith( "-o" ) ) {
                outputPort = Integer.parseInt( arg.substring( 2 ) );
            }
        }

        if ( host == null || inputPort < 0 || outputPort < 0 ) {
            throw new NotEnoughParametersException( "Not all parameters matched\n" + getUsageMessage() );
        }

        System.out.println( "Ready to connect to " + host + " on port " + outputPort );

        // Start listening
        startListening( inputPort, outputPort, host );
    }


    private static void startListening( int inputPort, int outputPort, String host )
    {

        try {
            // Start listening
            @SuppressWarnings ( "resource") ServerSocket server = new ServerSocket( inputPort );
            System.out.println( "Listening on port " + inputPort + " for new connections..." );

            // Wait for infinite connections (Default max 50 active connections)
            while ( true ) {
                try {
                    // Accept a connection
                    Socket clientConnection = server.accept();
                    System.out.println( "Got a connection from " + clientConnection.getInetAddress() );

                    // Open streams
                    InputStream serverIn = clientConnection.getInputStream();
                    OutputStream serverOut = clientConnection.getOutputStream();

                    // Connect to the target and start redirection
                    @SuppressWarnings ( "resource") Socket targetCon = new Socket( host, outputPort );
                    InputStream targetIn = targetCon.getInputStream();
                    OutputStream targetOut = targetCon.getOutputStream();

                    RedirectThread outgoing = new RedirectThread( targetIn, serverOut );
                    RedirectThread incoming = new RedirectThread( serverIn, targetOut );

                    outgoing.start();
                    incoming.start();

                    System.out.println( "Established a redirect from " + clientConnection.getInetAddress() + " to " + host );
                } catch ( IOException e ) {
                    e.printStackTrace();
                }
            }
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }


    private static String getVersionInfo()
    {
        return VERSION_INFO;
    }


    private static String getUsageMessage()
    {
        return "Usage:\njava -jar socketredirect.jar -i<input port> -o<output port> -h<host>\nEg.: java -jar socketredirect.jar -i6000 -o6667 -hirc.rizon.net";
    }
}


/**
 * Reads from the input stream and writes to the output stream
 * 
 * @author Testware
 */
class RedirectThread extends Thread
{
    InputStream in;
    OutputStream out;


    public RedirectThread( InputStream in, OutputStream out )
    {
        this.in = in;
        this.out = out;
    }


    @Override
    public void run()
    {
        byte[] buffer = new byte[1024];
        int bytesRead;
        try {
            for ( ;; ) {
                bytesRead = in.read( buffer );
                if ( bytesRead == -1 ) {
                    out.close();
                    return;
                }
                out.write( buffer, 0, bytesRead );
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }
}