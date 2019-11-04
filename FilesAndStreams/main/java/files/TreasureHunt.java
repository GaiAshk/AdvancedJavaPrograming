package files;

import java.io.IOException;
import java.nio.channels.FileChannel;

public class TreasureHunt {
   /**
     * Find the treasure by following the map.
     *
     * Starting with the firstClue, and until it has found the treasure (the decoder returned -1), this method should
     * repeat the following actions:
     * <ol>
     *     <li>Decode the clue (using the decoder) to get the index of the next clue. </li>
     *     <li>Read the next clue from the map. Each clue consists of the 48 bits of the map starting at the index
     *     returned from the decoder (treat anything beyond the end of the map as 0).
     *
     *     The index of a clue is given in bits from the beginning of the map,
     *     where 0 is the MSB of the first byte (and map_size_in_bytes*8-1 is the LSB of the last byte). </li>
     * </ol>
     *
     *
     * @param map This is a {@link FileChannel} containing the encoded treasure map.
     * @param decoder The decoder used to find the location of the next clue
     * @param firstClue The first clue.
     * @return The index of the treasure in the file (in bits)
     * @throws IOException
     */
    public static long findTreasure(FileChannel map, TreasureMapDecoder decoder, long firstClue) throws IOException {
        // TODO: implement
        return 0;
    }
}
