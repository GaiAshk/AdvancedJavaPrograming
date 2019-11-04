package files;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Random;

import static org.junit.Assert.assertArrayEquals;

public class RandomAccessTest {
	public final static int NUM_EXTENDED = 32; // number of extended tests
	public final static int MAX_LENGTH = 513; // maximum sort length
	public final static int MIN_LENGTH = 2; // minimum sort length

	Random rnd;

	@Before
	public void setup() {
		rnd = new Random(7); // Fixed seed so tests will be repeatable.
	}

	@Test
	public void testSortBytes() throws IOException {
		File temp = File.createTempFile("sortBytes", "byt");
		RandomAccessFile file = new RandomAccessFile(temp, "rw");

		byte[] origBytes = { 0, 1, 5, 4, 3, 2, 10, 20, -1, 1, 2, 3 };
		file.write(origBytes);
		RandomAccess.sortBytes(file);

		file.seek(0);
		byte[] sortedBytes = { 0, 1, 1, 2, 2, 3, 3, 4, 5, 10, 20, -1 };
		byte[] actualBytes = new byte[sortedBytes.length];

		file.readFully(actualBytes);
		assertArrayEquals(sortedBytes, actualBytes);
	}


	@Test
	public void testSortBytesExtended() throws IOException {
		for (int i = 0; i < NUM_EXTENDED; ++i) {
			File temp = File.createTempFile("sortBytes", "byt");
			RandomAccessFile file = new RandomAccessFile(temp, "rw");

			int len = rnd.nextInt(MAX_LENGTH - MIN_LENGTH) + MIN_LENGTH;
			byte[] origBytes = new byte[len];
			rnd.nextBytes(origBytes);

			file.write(origBytes);

			RandomAccess.sortBytes(file);

			file.seek(0);
			byte[] actualBytes = new byte[len];

			file.readFully(actualBytes);

			// Sort as unsigned
			int unsignedBytes[] = new int[origBytes.length];
			for (int j = 0; j < origBytes.length; ++j) {
				unsignedBytes[j] = ((int) origBytes[j]) & 0xff;
			}
			java.util.Arrays.sort(unsignedBytes);
			byte sortedBytes[] = new byte[origBytes.length];
			for (int j = 0; j < origBytes.length; ++j) {
				sortedBytes[j] = (byte) unsignedBytes[j];
			}

			assertArrayEquals("Random sort for file of length " + len
					+ " failed", sortedBytes, actualBytes);
			temp.delete();
		}
	}


	@Test
	public void testSortTriBytes() throws IOException {
		File temp = File.createTempFile("sortTriBytes", "byt");
		RandomAccessFile file = new RandomAccessFile(temp, "rw");

		byte[] origBytes = { 0, 1, 5, 4, 3, 2, 10, 20, 30, 1, 2, 3 };
		file.write(origBytes);
		RandomAccess.sortTriBytes(file);

		file.seek(0);
		byte[] sortedBytes = { 0, 1, 5, 1, 2, 3, 4, 3, 2, 10, 20, 30 };
		byte[] actualBytes = new byte[sortedBytes.length];

		file.readFully(actualBytes);
		assertArrayEquals(sortedBytes, actualBytes);
	}

	@Test
	public void testSortTriBytes2() throws IOException {
		File temp = File.createTempFile("sortTriBytes", "byt");
		RandomAccessFile file = new RandomAccessFile(temp, "rw");

		byte[] origBytes = { 0, 1, 5, 4, 3, 2, 10, 20, 30, 1, 2, 3, 0, 1, 5, 0, 0, 1 };
		file.write(origBytes);
		RandomAccess.sortTriBytes(file);

		file.seek(0);
		byte[] sortedBytes = { 0, 0, 1, 0, 1, 5, 0, 1, 5, 1, 2, 3, 4, 3, 2, 10, 20, 30 };
		byte[] actualBytes = new byte[sortedBytes.length];

		file.readFully(actualBytes);
		assertArrayEquals(sortedBytes, actualBytes);
	}

	@Test
	public void testSortTriBytesExtended() throws IOException {
		for (int i = 0; i < NUM_EXTENDED; ++i) {
			File temp = File.createTempFile("sortBytes", "byt");
			RandomAccessFile file = new RandomAccessFile(temp, "rw");

			int len = rnd.nextInt(MAX_LENGTH - MIN_LENGTH) + MIN_LENGTH;
			len += (3 - len % 3) % 3; // Make sure len is divisible by 3
			byte[] origBytes = new byte[len];
			rnd.nextBytes(origBytes);

			file.write(origBytes);

			RandomAccess.sortTriBytes(file);

			file.seek(0);

			byte[] actualBytes = new byte[origBytes.length];
			file.readFully(actualBytes);

			// Sort as unsigned
			int unsignedTris[] = new int[origBytes.length / 3];
			for (int j = 0; j < unsignedTris.length; ++j) {
				unsignedTris[j] = ((((int) origBytes[3*j]) & 0xff) << 16) |
						((((int) origBytes[3*j+1]) & 0xff) << 8) | 
						((((int) origBytes[3*j+2]) & 0xff));
			}
			java.util.Arrays.sort(unsignedTris);
			byte sortedBytes[] = new byte[origBytes.length];
			for (int j = 0; j < unsignedTris.length; ++j) {
				sortedBytes[3*j] = (byte) (unsignedTris[j] >>> 16);
				sortedBytes[3*j+1] = (byte) (unsignedTris[j] >>> 8);
				sortedBytes[3*j+2] = (byte) (unsignedTris[j]);
			}
			
			assertArrayEquals("Random sort for file of length " + len
					+ " failed", sortedBytes, actualBytes);

		}
	}
}
