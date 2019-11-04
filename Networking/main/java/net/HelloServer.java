package net;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class HelloServer implements API.HelloServer {

    ServerSocket serverSocket;
    boolean isListen = false;

    /**
     * Should return the ServerSocket object set up by the HelloServer.
     *
     * This method may return null if it is called before the {@link #listen()} or {@link #listen(List)} are called.
     * Otherwise, the ServerSocket should be listening on the ports (calling {@link ServerSocket#accept()} should return
     * the next incoming connection).
     * @return
     */
    public ServerSocket getServerSocket() {
        //if listen was not used return null
        if(!isListen) return null;
        return this.serverSocket;
    }

    /**
     * Listen on the first available port in a given list.
     * <p>
     * <p>Note: Should not throw exceptions due to ports being unavailable</p>
     *
     * @return The port number chosen, or -1 if none of the ports were available.
     */
    public int listen(List<Integer> portList) throws IOException {
        //marking the listen was used before getServerSocket
        this.isListen = true;
        //run on all the integers in portList, if they are not null return the port number
        for (Integer i : portList){
            try {
                this.serverSocket = new ServerSocket(i);
                if (this.serverSocket != null) return i;
            } catch (Exception e) {
                System.out.println("can't listen on: " + i);
            }
        }
        return -1;
    }


    /**
     * Listen on an available port.
     * Any available port may be chosen.
     *
     * @return The port number chosen.
     */
    public int listen() throws IOException {
        //marking the listen was used before getServerSocket
        this.isListen = true;
        //listen to availabe port
        this.serverSocket = new ServerSocket(0);
        //return the first available port
        return this.serverSocket.getLocalPort();
    }


    /**
     * 1. Start listening on an open port. Write {@link #LISTEN_MESSAGE} followed by the port number (and a newline) to sysout.
     * If there's an IOException at this stage, exit the method.
     * <p>
     * 2. Run in a loop;
     * in each iteration of the loop, wait for a client to connect,
     * then read a line of text from the client. If the text is {@link #BYE_MESSAGE},
     * send {@link #BYE_MESSAGE} to the client and exit the loop. Otherwise, send {@link #HELLO_MESSAGE}
     * to the client, followed by the string sent by the client (and a newline)
     * After sending the hello message, close the client connection and wait for the next client to connect.
     * <p>
     * If there's an IOException while in the loop, or if the client closes the connection before sending a line of text,
     * send the text {@link #ERR_MESSAGE} to sysout, but continue to the next iteration of the loop.
     * <p>
     * *: in any case, before exiting the method you must close the server socket.
     *
     * @param sysout a {@link PrintStream} to which the console messages are sent.
     */
    public void run(PrintStream sysout) {
        try {
            int port = this.listen();
            sysout.println(LISTEN_MESSAGE + port);
        } catch (IOException e) {
            sysout.println("listen to the port didnt work");
            return;
        }
        Socket clientSocket = null;
        while(true){
            try {
                //wait until connection is made
                clientSocket = serverSocket.accept();

                //open Scanner and BufferedWriter
                Scanner scan = new Scanner(clientSocket.getInputStream());
                PrintWriter pw = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

                //get the first line from the client
                String line = scan.nextLine();
                if(line == null) {
                    sysout.println(ERR_MESSAGE);
                    continue;
                }
                if(line.equals(BYE_MESSAGE)){
                    pw.write(BYE_MESSAGE + "\n");
                    pw.flush();
                    pw.close();
                    break;
                } else {
                    pw.write(HELLO_MESSAGE + line + "\n");
                    pw.flush();
                    pw.close();
                }

                //close the socket
                clientSocket.close();

            } catch (IOException e) {
                sysout.println("ERR_MESSAGE");
            }

        }

    }


    /**
     * This is for your own testing.
     * @param args
     */
    public static void main(String args[]) {
        HelloServer server = new HelloServer();

        server.run(System.err);
    }

}
