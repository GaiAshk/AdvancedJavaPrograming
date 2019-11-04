package files;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class Streams {
	/**
	 * Read from an InputStream until a quote character (") is found, then read
	 * until another quote character is found and return the bytes in between the two quotes. 
	 * If no quote character was found return null, if only one, return the bytes from the quote to the end of the stream.
	 * @param in
	 * @return A list containing the bytes between the first occurrence of a quote character and the second.
	 */
	public static List<Byte> getQuoted(InputStream in) throws IOException {
        List<Byte> output = new ArrayList<Byte>();
        int c;
        // crateing a flag to signal when a the sign " is read
        int flag = 0;
        try {
        //run on the inputStream until we find a second " or the end of the stream
        while ((c = in.read()) != -1) {
            if (flag == 1 && c != '\"') {
                output.add((byte) c);
            }
            if (c == '\"') {
                flag++;
            }
            if (flag == 2) {
                break;
            }
        }
    } catch (IOException e) {
        System.err.println("Error: " + e);
    } finally {
            //if no " signs where discovered then return null
            if (flag == 0) return null;
            return output;
        }
	}
	
	
	/**
	 * Read from the input until a specific string is read, return the string read up to (not including) the endMark.
	 * @param in the Reader to read from
	 * @param endMark the string indicating to stop reading. 
	 * @return The string read up to (not including) the endMark (if the endMark is not found, return up to the end of the stream).
	 */
	public static String readUntil(Reader in, String endMark) throws IOException {
		String line = "";
		int c, flag = 0;
		try {
		    //run on the readers input, if endMark is meet then don't copy and brake the loop
            while ((c = in.read()) != -1) {
                //check if we found the endMark
                if (c == endMark.charAt(0)){
                    char temp = (char) c;
                    //mark the position on the stream if we potentially meet the endMark
                    in.mark(endMark.length());

                    //check if we are indeed at the endMark
                    for (int i = 0; i < endMark.length(); i++) {
                        if(endMark.charAt(i) == c) {
                            c = in.read();
                        } else {
                            in.reset();
                            c = temp;
                            break;
                        }
                        if (i == endMark.length()-1) {
                            flag++;
                        }
                    }
                }
                if(flag == 1) break;
                //concatenate new char to the string
                line += (char) c;
            }
        } catch (IOException e) {
		    //can occur also in mark and reset is not supported by the stream
            System.err.println("Error: " + e);
        } finally {
            return line;
        }
	}
	
	/**
	 * Copy bytes from input to output, ignoring all occurrences of badByte.
	 * @param in
	 * @param out
	 * @param badByte
	 */
	public static void filterOut(InputStream in, OutputStream out, byte badByte) throws IOException {
        try{
            int c;
            //if badByte is negative we will add 256 to it and make in positive
            int negBad = (badByte < 0)? badByte+256 : 0;
            while((c = in.read()) != -1){
                //checking both conditions if badByte was positive or negative
                if((c == badByte && negBad == 0) || (negBad != 0 && c == negBad)){
                    continue;
                } else {
                    out.write(c);
                }
            }
        } catch (IOException e) {
            //print err message to system.err
            System.err.println("Error: " + e);
        }
	}
	
	/**
	 * Read a 48-bit (unsigned) integer from the stream and return it. The number is represented as five bytes, 
	 * with the most-significant byte first. 
	 * If the stream ends before 5 bytes are read, return -1.
	 * @param in
	 * @return the number read from the stream
	 */
	public static long readNumber(InputStream in) throws IOException {
        int c, i = 0;
        long num = 0;
        try {
            while ((c = in.read()) != -1) {
                //if i will be less then 5 we will return -1
                i++;
                //shifting the number
                num <<= 8;
                //after shifting num now add c to the total value
                num += c;
                if(i == 5) break;
            }
        } catch (IOException e) {
            //print err message to system.err
            System.err.println("Error: " + e);
        } finally {
            if (i < 5) {
                return -1;
            } else {
                return num;
            }
        }
    }
}
