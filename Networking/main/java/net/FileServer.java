package net;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class FileServer implements API.FileServer {


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
    public boolean isLegalAbsolutePath(String path) {
        if(path.charAt(0) != '/' || path.contains("/../")) return false;
        return path.matches("[\\w._\\-~/]*");
    }


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
    public void runSingleClient(ServerSocket serverSock, PrintStream sysErr) {
        Socket client = null;
        BufferedWriter out = null;
        BufferedReader in = null;
        int i, c;
        String line = "", word1 = "", word2 = "";
        try{
            while(true) {
                //wait until connection is made
                client = serverSock.accept();

                //establish in connection and open Buffered Reader
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));

                //read a line from the client and remove all white space to a single space
                line = in.readLine().replaceAll("\\s+"," ");

                //if the line is a single word, close and continue
                i = line.indexOf(" ");
                if (i == -1) {
                    if(client != null) client.close();
                    if(in != null) in.close();
                    continue;
                }

                //open Buffered Writer
                out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));

                //set word1 and word2
                word1 = line.substring(0,i);

                //if word1 is not GET or get close all opened stuff and continue to next iteration
                if(!word1.equals("GET") && !word1.equals("get") ){
                    out.write(API.FileServer.RESP_BADMETHOD);
                    out.flush();
                    if(client != null) client.close();
                    if(in != null) in.close();
                    if(out != null) out.close();
                    continue;
                }

                word2 = line.substring(i+1,line.length());
                //if word2 has more then 1 word cut the rest of the word out
                i = word2.indexOf(" ");
                if(i != -1){
                    word2 = word2.substring(0, i);
                }
                //convert Windows path to POSIX
                word2 = convertWindowsToPOSIX(word2);

                //if path is not legal send response and close
                if(!isLegalAbsolutePath(word2)){
                    out.write(API.FileServer.RESP_BADPATH + word2);
                    out.flush();
                    if(client != null) client.close();
                    if(in != null) in.close();
                    if(out != null) out.close();
                    continue;
                }

                //at this point the path is legal
                //the path is legal and accurate to PATH EXIT
                if(word2.equals(PATH_EXIT)) {
                    out.write(RESP_EXIT);
                    out.flush();
                    if(in != null) in.close();
                    if(out != null) out.close();
                    break;
                }

                //try to open the new file if it cannot be opened or read, continue to next iteration
                File file = new File(word2);
                //file does not exist or cannot be read
                if(!file.exists() || !file.canRead()){
                    out.write(RESP_NOTFOUND + word2);
                    out.flush();
                    if(client != null) client.close();
                    if(in != null) in.close();
                    if(out != null) out.close();
                    continue;
                }
                //set in to the new BufferedReader to read from the file
                in = new BufferedReader(new FileReader(file));

                //if the file is good read from the file and send the text to the client
                out.write(RESP_OK);
                out.flush();
                while((c = in.read()) != -1) {
                    out.write((char) c);
                    out.flush();
                }

                //finally close the file and the server
                if(client != null) client.close();
                if(in != null) in.close();
                if(out != null) out.close();
            }
        } catch (IOException e) {
            sysErr.println(MSG_IOERROR);
            sysErr.flush();
            }
    }

    /**
     * Convert a windows-style path (e.g. "C:\dir\dir2") to POSIX style (e.g. "/dir1/dir2")
     */
    static String convertWindowsToPOSIX(String path) {
        return path.replaceFirst("^[a-zA-Z]:", "").replaceAll("\\\\", "/");
    }

    /**
     * This is for your own testing.
     * If you wrote the code correctly and run the program,
     * you should be able to enter the "test URL" printed
     * on the console in a browser, see  a "Yahoo!..." message,
     * and click on a link to exit.
     *
     * @param args
     */
    static public void main(String[] args) {
        FileServer fs = new FileServer();

        HelloServer serve = new HelloServer();

        File tmpFile = null;
        try {
            try {
                tmpFile = File.createTempFile("test", ".html");
                FileOutputStream fos = new FileOutputStream(tmpFile);
                PrintStream out = new PrintStream(fos);
                out.println("<!DOCTYPE html>\n<html>\n<body>Yahoo! Your test was successful! <a href=\"" + PATH_EXIT + "\">Click here to exit</a></body>\n</html>");
                out.close();

                int port = serve.listen();
                System.err.println("Test URL: http://localhost:" + port
                        + convertWindowsToPOSIX(tmpFile.getAbsolutePath()));
            } catch (IOException e) {
                System.err.println("Exception, exiting: " + e);
                return;
            }

            fs.runSingleClient(serve.getServerSocket(), System.err);
            System.err.println("Exiting due to client request");

            try {
                serve.getServerSocket().close();
            } catch (IOException e) {
                System.err.println("Exception closing server socket, ignoring:"
                        + e);
            }
        } finally {
            if (tmpFile != null)
                tmpFile.delete();
        }
    }

}