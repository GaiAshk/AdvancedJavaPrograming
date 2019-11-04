package files;

import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import static solution.files.Streams.*;

public class StreamsTest {
    public final static int NUM_EXTENDED = 32; // number of extended tests
    public final static int EXPECTED_QUOTES = 2; // number of expected quotes
    public final static int MAX_LEN = 200; // maximum array (stream) length
    public final static int MIN_LEN = 0; // maximum array (stream) length

    Random rnd;

    @Before
    public void setup() {
        rnd = new Random(9); // Fixed seed so tests will be repeatable.
    }

    @Test
    public void testGetQuoted() throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(
                "this is irrelevant \"return this substring\" this is irrelevant"
                        .getBytes());
        byte[] rawExpected = "return this substring".getBytes();
        List<Byte> expected = new ArrayList<Byte>(rawExpected.length);
        for (byte b : rawExpected)
            expected.add(b);

        assertEquals(expected, Streams.getQuoted(in));
    }

    /**
     * Read from an InputStream until a quote character (") is found, then read
     * until another quote character is found and return the bytes in between
     * the two quotes. If no quote character was found return null, if only one,
     * return the bytes from the quote to the end of the stream.
     *
     * @return A list containing the bytes between the first occurrence of a
     * quote character and the second.
     */
    @Test
    public void testGetQuotedExtended() throws IOException {
        for (int i = 0; i < NUM_EXTENDED; ++i) {
            int len = rnd.nextInt(MAX_LEN);
            double quoteProb = EXPECTED_QUOTES / (double) len;

            byte[] arr = new byte[len];
            rnd.nextBytes(arr);

            int numQuotes = 0;
            // Change each character to a quote with prob. quoteProb
            for (int j = 0; j < arr.length; ++j) {
                if (rnd.nextDouble() < quoteProb) {
                    arr[j] = '"';
                    ++numQuotes;
                } else if (arr[j] == '"') // If it's a quote and shouldn't be,
                    // change to something else
                    ++arr[j];
            }
            ByteArrayInputStream in = new ByteArrayInputStream(arr);
            List<Byte> expected = getQuoted(in);
            in.reset();

            assertEquals("Failed random test with length " + len + " and "
                    + numQuotes + " quotes", expected, Streams.getQuoted(in));
        }
    }


    @Test
    public void testReadUntil() throws IOException {
        StringReader in = new StringReader(
                "This is a test<end|nope<endMark> some extra text");
        String expected = "This is a test<end|nope";
        String actual = Streams.readUntil(in, "<endMark>");
        assertEquals(expected, actual);
    }

    @Test
    public void testReadUntil2() throws IOException {
        StringReader in = new StringReader(
                "Th<eis is a t<est<<<<end|nope<endMark> some extra text");
        String expected = "Th<eis is a t<est<<<<end|nope";
        String actual = Streams.readUntil(in, "<endMark>");
        assertEquals(expected, actual);
    }

    @Test
    public void testReadUntilExtended() throws IOException {
        for (int i = 0; i < NUM_EXTENDED; ++i) {
            int len = rnd.nextInt(MAX_LEN) + MIN_LEN;
            byte arr[] = new byte[len * 2];
            rnd.nextBytes(arr);
            String str1 = new String(arr, "UTF-16");
            rnd.nextBytes(arr);
            String str2 = new String(arr, "UTF-16");

            byte emark[] = new byte[20];
            rnd.nextBytes(emark);
            String endMark = new String(emark, "UTF-16");
            String endPart = endMark
                    .substring(0, rnd.nextInt(endMark.length()));

            StringReader in = new StringReader(str1 + endPart + str2 + endMark
                    + str1 + endPart + str1);
            String expected = str1 + endPart + str2;
            String actual = Streams.readUntil(in, endMark);
            assertEquals(
                    "Failed random sequence of characters with random endmark",
                    expected, actual);
        }
    }

    @Test
    public void testFilterOut() throws IOException {
        byte[] bytes = "aabbccddeeaabbccddeeabcde".getBytes();
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        byte[] expectedBytes = "aaccddeeaaccddeeacde".getBytes();
        Streams.filterOut(in, out, (byte) 'b');
        byte[] actualBytes = out.toByteArray();
        assertArrayEquals(expectedBytes, actualBytes);
    }

    /*
     * Copy bytes from input to output, ignoring all occurrences of badByte.
     *
     * @param in
     *
     * @param out
     *
     * @param badByte
     */
    @Test
    public void testFilterOutExtended() throws IOException {
        for (int i = 0; i < NUM_EXTENDED; ++i) {
            int len = rnd.nextInt(MAX_LEN) + MIN_LEN;
            int numExpectedBadBytes = rnd.nextInt((int) (len * 1.5)); // Expected number of bad bytes
            double badByteProb = (double) numExpectedBadBytes / len;
            byte badByte = (byte) rnd.nextInt();

            byte[] bytes = new byte[len];
            rnd.nextBytes(bytes);

            int numBadBytes = 0;
            for (int j = 0; j < bytes.length; ++j) {
                if (rnd.nextDouble() < badByteProb) {
                    bytes[j] = badByte;
                    ++numBadBytes;
                } else if (bytes[j] == badByte) {
                    ++bytes[j];
                }
            }

            ByteArrayInputStream in = new ByteArrayInputStream(bytes);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            filterOut(in, out, badByte);
            byte[] expectedBytes = out.toByteArray();

            in.reset();
            out.reset();

            Streams.filterOut(in, out, badByte);
            byte[] actualBytes = out.toByteArray();
            assertArrayEquals(
                    "Failed random test with length " + len + " and "
                            + numBadBytes + " bad bytes (bad byte was "
                            + badByte + ")", expectedBytes, actualBytes);
        }
    }


    @Test
    public void testReadNumber() throws IOException {
        byte[] bytes = {0x12, 0x34, 0x56, 0x78, 0x0a};
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        long num = Streams.readNumber(in);
        assertEquals(0x123456780aL, num);
    }

    @Test
    public void testReadNumber2() throws IOException {
        byte[] bytes = {0x12, 0x34, 0x56, 0x78};
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        long num = Streams.readNumber(in);
        assertEquals(-1, num);
    }

    /*
     * Read a 48-bit (unsigned) integer from the stream and return it. The
     * number is represented as five bytes, with the most-significant byte
     * first. If the stream ends before 5 bytes are read, return -1.
     *
     * @param in
     *
     * @return the number read from the stream
     */

    @Test
    public void testReadNumberExtended() throws IOException {
        for (int i = 0; i < NUM_EXTENDED; ++i) {
            int len = rnd.nextInt(MAX_LEN);
            if (rnd.nextDouble() < 0.2) {
                // We test for short inputs one fifth of the time
                len = rnd.nextInt(5);
            }
            byte[] bytes = new byte[len];
            rnd.nextBytes(bytes);
            ByteArrayInputStream in = new ByteArrayInputStream(bytes);
            long expected = readNumber(in);
            in.reset();
            long num = Streams.readNumber(in);
            assertEquals("Failed random test with length " + len, expected, num);
        }
    }
}
