package net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.util.List;

/**
 * These interfaces are used by the testing code.
 */
public interface API {
    public interface HelloServer {
        public static final String ERR_MESSAGE = "IO Error!";
        public static final String LISTEN_MESSAGE = "Listening on port: ";
        public static final String HELLO_MESSAGE = "hello ";
        public static final String BYE_MESSAGE = "bye";

        public ServerSocket getServerSocket();

        /**
         * Listen on the first available port in a given list.
         * <p>
         * <p>Note: Should not throw exceptions due to ports being unavailable</p>
         *
         * @return The port number chosen, or -1 if none of the ports were available.
         */
        public int listen(List<Integer> portList)  throws IOException;


        /**
         * Listen on an available port.
         * Any available port may be chosen.
         *
         * @return The port number chosen.
         */
        public int listen() throws IOException;


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
        public void run(PrintStream sysout);
    }


    public interface HelloClient {
        public static final int COUNT = 10;

        /**
         * Connect to a remote host using TCP/IP and set {@link #clientSocket} to be the
         * resulting socket object.
         *
         * @param host remote host to connect to.
         * @param port remote port to connect to.
         * @throws IOException
         */
        public void connect(String host, int port) throws IOException;

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
        public void run(PrintStream sysout, String host, int port, String myname);
    }

    public interface FileServer {
        static final String RESP_OK = "HTTP/1.0 200 OK\r\nConnection: close\r\n\r\n";
        static final String RESP_BADPATH = "HTTP/1.0 403 Forbidden\r\nConnection: close\r\n\r\nForbidden: ";
        static final String RESP_NOTFOUND = "HTTP/1.0 404 Not Found\r\nConnection: close\r\n\r\nNot found: ";
        static final String RESP_BADMETHOD = "HTTP/1.0 405 Method not allowed\r\nConnection: close\r\nAllow: GET\r\n\r\nBad";
        static final String RESP_EXIT = RESP_OK + "Thanks, I'm done.";

        static final String MSG_IOERROR = "There was an IO Error";
        static final String PATH_EXIT = "/exit";

        /**
         * Check if a string is a well-formed absolute path in the filesystem. A well-formed
         * absolute path must satisfy:
         * <ul>
         * <li>Begins with "/"
         * <li>Consists only of English letters, numbers, and the special characters
         * '_', '.', '-', '~' and '/'.
         * <li>Does not contain any occurrences of the string "/../".
         * </ul>
         *
         * @param path The path to check.
         * @return true if the path is well-formed, false otherwise.
         */
        public boolean isLegalAbsolutePath(String path) ;


        /**
         * This method should do the following things, given an open (already
         * listening) server socket:
         * <ol>
         * <li>Do the following in a loop (this is the "main loop"):
         * <ol>
         * <li>Wait for a client to connect. When a client connects, read one line
         * of input (hint: you may use {@link BufferedReader#readLine()} to read a
         * line).
         * <li>The client's first line of input should consist of at least two words
         * separated by spaces (any number of spaces is ok). If it's less than two words,
         * you may handle it in any reasonable way (e.g., just close the connection).
         * <ul>
         * <li>if the first word is not GET (case-insensitive), send the string
         * {@link #RESP_BADMETHOD} to the client and close the connection.
         * <li>otherwise, parse the second word as a full path (a filename,
         * including directories, separated by '/' characters).
         * <li>if the pathname is exactly {@value #PATH_EXIT} (case sensitive), send
         * {@link #RESP_EXIT} to the client and close the connection. Then exit the
         * main loop (do not close the server socket).
         * <li>if the path is not a legal absolute path (use
         * {@link #isLegalAbsolutePath(String)} to check) send
         * {@link #RESP_BADPATH}, followed by the path itself, to the client and close the connection.
         * <li>if the path is a legal path but the file does not exist or cannot be read,
         * {@link #RESP_NOTFOUND}, followed by the path itself, to the client and close the connection.
         * <li>otherwise, send {@link #RESP_OK} to the client, then open the file
         * and send its contents to the client. Finally, close the connection.
         * </ul>
         * <li>If there is an I/O error during communication with the client or
         * reading from the file, output the string {@value #MSG_IOERROR} to sysErr
         * and close the connection (ignore errors during close).
         * </ol>
         * </ol>
         *
         * @param serverSock the {@link ServerSocket} on which to accept connections.
         * @param sysErr     the {@link PrintStream} to which error messages are written.
         */
        public void runSingleClient(ServerSocket serverSock, PrintStream sysErr) ;
    }

}
