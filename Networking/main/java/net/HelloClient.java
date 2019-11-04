package net;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class HelloClient implements API.HelloClient {

    Socket clientSocket;

    /**
     * Connect to a remote host using TCP/IP and set {@link #clientSocket} to be the
     * resulting socket object.
     *
     * @param host remote host to connect to.
     * @param port remote port to connect to.
     * @throws IOException
     */
    public void connect(String host, int port) throws IOException {
        try {
            this.clientSocket = new Socket(host, port);
        } catch (IOException e) {
            System.out.println(API.HelloServer.ERR_MESSAGE);
        }
    }

    /**
     * Perform the following actions {@link #COUNT} times in a row: 1. Connect
     * to the remote server (host:port). 2. Write the string in myname (followed
     * by newline) to the server 3. Read one line of response from the server,
     * write it to sysout (without the trailing newline) 4. Close the socket.
     * <p>
     * Then do the following (only once): 1. send
     * {@link API.HelloServer#BYE_MESSAGE} to the server (followed by newline). 2.
     * Read one line of response from the server, write it to sysout (without
     * the trailing newline)
     * <p>
     * If there are any IO Errors during the execution, output {@link API.HelloServer#ERR_MESSAGE}
     * (followed by newline) to sysout. If the error is inside the loop,
     * continue to the next iteration of the loop. Otherwise exit the method.
     *
     * @param sysout
     * @param host
     * @param port
     * @param myname
     */
    public void run(PrintStream sysout, String host, int port, String myname) {
        BufferedWriter out = null;
        BufferedReader in = null;
        String line = "";
        for (int i = 0; i <COUNT ; i++) {
            try {
                //connect to a server
                connect(host, port);

                //establish in and out connection and open Buffered Reader and Writer
                out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                //write myname to the server
                out.write(myname + "\n");
                out.flush();

                //read the servers response and write it to sysout
                line = in.readLine();
                sysout.write(line.getBytes());
                sysout.flush();

            } catch (IOException e) {
                sysout.println(API.HelloServer.ERR_MESSAGE);
                sysout.flush();
                continue;
            }
            try{
                //close out
                if (out != null) out.close();
                //close in
                if (in != null) in.close();
                //close clientSocket
                if(clientSocket != null) clientSocket.close();

            } catch (IOException e) {
                //send an error message and continue
                sysout.println(API.HelloServer.ERR_MESSAGE);
                sysout.flush();
                continue;
            }
        }

        try {

            //connect to a server
            connect(host, port);

            //establish in and out connection and open Buffered Reader and Writer
            out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            //write myname to the server
            out.write(API.HelloServer.BYE_MESSAGE + "\n");
            out.flush();

            //read the servers response and write it to sysout
            line = in.readLine();
            sysout.print(line);

            //close out
            if (out != null) out.close();
            //close in
            if (in != null) in.close();
            //close clientSocket
            if(clientSocket != null) clientSocket.close();

        } catch (IOException e) {
            sysout.println(API.HelloServer.ERR_MESSAGE);
            sysout.flush();
        }

    }
}
