package files;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

import static java.nio.file.StandardOpenOption.*;
import static org.junit.Assert.*;

public class TreasureHuntTest {
    final static int MAP_SIZE = 1024;

    Random rand;
    FileChannel map = null;
    Path mapFileName = null;

    boolean foundTreasure = false;
    boolean calledDecoder = false;

    @Before
    public void setup() throws IOException {
        rand = new Random(101);
        // Create a random file.
        mapFileName = Files.createTempFile("map-", ".bin");
        map = FileChannel.open(mapFileName, WRITE, READ);

        ByteBuffer buf = ByteBuffer.allocate(MAP_SIZE);
        buf.position(buf.capacity());
        buf.flip();
        rand.nextBytes(buf.array());
        map.write(buf);

        byte[] start = { (byte) 0xaa, 0x55, 1, 2, 0x33, 0x44, 0x79 };
        buf.clear();
        buf.put(start);
        buf.flip();
        map.write(buf, 0);

        buf.clear();
        buf.put((byte) 0x55);
        buf.flip();
        map.write(buf, map.size() - 1);
    }

    @After
    public void tearDown() throws IOException {
        map.close();
        Files.delete(mapFileName);
    }

    void checkTrivial() {
        assertTrue("Your code didn't call the decoder!", calledDecoder);
        assertTrue("Your code didn't find the treasure!", foundTreasure);
    }

    @Test
    public void testFindTreasureDecoding() throws IOException {
        TreasureMapDecoder decoder = (clue, idx, size) -> {
            calledDecoder = true;
            if (idx < 0)
                return 14;
            if (idx % 7 != 0) {
                foundTreasure = true;
                return -1;
            }
            if (idx > 100) {
                foundTreasure = true;
                return -1;
            }
            return idx + 7;
        };

        long actual = TreasureHunt.findTreasure(map, decoder, 0);

        checkTrivial();

        assertEquals("Your solution doesn't use the decoder correctly", 105, actual);
    }

    @Test
    public void testFindTreasureReading() throws IOException {
        final long treasure = (0xaa550102334479L >>> 5) & ((1L << 48) - 1);

        TreasureMapDecoder decoder = (clue, idx, size) -> {
            calledDecoder = true;
            if (idx == -1) {
                return 3;
            } else {
                foundTreasure = true;
                assertEquals("Your code didn't read the map correctly", treasure, clue);
                return -1;
            }
        };

        long actual = TreasureHunt.findTreasure(map, decoder, 0);

        checkTrivial();

        assertEquals("Your solution doesn't detect the treasure correctly when the decoder finds it", 3, actual);
    }


    @Test
    public void testFindTreasureReadPastMapEnd() throws IOException {
        TreasureMapDecoder decoder = (clue, idx, size) -> {
            calledDecoder = true;
            if (idx == -1) {
                return (MAP_SIZE) * 8 - 8;
            } else {
                foundTreasure = true;
                assertEquals("Your code doesn't handle indices beyond map end correctly", 0x550000000000L, clue);
                return -1;
            }
        };

        TreasureHunt.findTreasure(map, decoder, 0);
        checkTrivial();
    }



    @Test
    public void testFindTreasureExtended() throws IOException {
        TreasureMapDecoder decoder = (clue, idx, size) -> {
            calledDecoder = true;
            if (clue % 50 == 0) {
                foundTreasure = true;
                return -1;
            }
            return (clue * 47 + 1) % size;
        };

        long actual = TreasureHunt.findTreasure(map, decoder, 10001);
        checkTrivial();

        long expected = solution.files.TreasureHunt.findTreasure(map, decoder, 10001);

        assertEquals("Your solution didn't match mine", expected, actual);
    }
}