package files;

public interface TreasureMapDecoder {
    /**
     * Given the index in the file from which a previous clue has been read, and the encoded clue itself,
     * return the location of the next clue.
     *
     * @param clue the data of the clue. Each clue is a 48-bit unsigned value.
     * @param location index in the map file of the clue. The index is given in <b>bits</b>.
     * @param mapLength length of the map file (in bits)
     * @return The index of the next clue (in bits), or -1 if the location contains the treasure itself.
     */
    long decodeClue(long clue, long location, long mapLength);
}
